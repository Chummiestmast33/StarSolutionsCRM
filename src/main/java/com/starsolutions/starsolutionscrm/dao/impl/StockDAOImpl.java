package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.IStockDAO;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.inventario.MovimientoInventario;
import com.starsolutions.starsolutionscrm.model.inventario.Stock;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StockDAOImpl implements IStockDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ----------------------------------------------------------------
    // VERIFICAR DISPONIBILIDAD POR UBICACIÓN
    // ----------------------------------------------------------------
    @Override
    public boolean verificarDisponibilidadPorUbicacion(int idProducto, String ubicacion, int cantidadRequerida) throws SQLException {
        String sql = "SELECT cantidad_actual FROM inv_stock " +
                "WHERE id_producto = ? AND TRIM(LOWER(ubicacion)) = TRIM(LOWER(?))";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.setString(2, ubicacion != null ? ubicacion : "");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int cantidadActual = rs.getInt("cantidad_actual");
                    return cantidadActual >= cantidadRequerida;
                }
            }
        }
        return false; // No existe el stock en esa ubicación
    }

    // ----------------------------------------------------------------
    // VERIFICAR DISPONIBILIDAD TOTAL POR PRODUCTO
    // ----------------------------------------------------------------
    @Override
    public boolean verificarDisponibilidadTotalProducto(int idProducto, int cantidadRequerida) throws SQLException {
        String sql = "SELECT COALESCE(SUM(cantidad_actual), 0) AS total " +
                "FROM inv_stock WHERE id_producto = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int totalDisponible = rs.getInt("total");
                    return totalDisponible >= cantidadRequerida;
                }
            }
        }
        return false;
    }

    // ----------------------------------------------------------------
    // REGISTRAR MOVIMIENTO
    // ----------------------------------------------------------------
    @Override
    public int registrarMovimiento(MovimientoInventario movimiento) throws SQLException {
        // Para salidas, validar disponibilidad previa
        if ("SALIDA".equalsIgnoreCase(movimiento.getTipo())) {
            // Obtener la cantidad actual del stock antes de la salida
            String sqlVerifica = "SELECT cantidad_actual FROM inv_stock WHERE id_stock = ?";
            try (PreparedStatement ps = getConn().prepareStatement(sqlVerifica)) {
                ps.setInt(1, movimiento.getIdStock());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Stock no encontrado para id_stock: " + movimiento.getIdStock());
                    }
                    int cantidadActual = rs.getInt("cantidad_actual");
                    if (cantidadActual < movimiento.getCantidad()) {
                        throw new SQLException("Stock insuficiente para la salida. Disponible: " + cantidadActual + ", Requerido: " + movimiento.getCantidad());
                    }
                }
            }
        }

        // Insertar movimiento
        String sql = "INSERT INTO inv_movimiento (id_stock, id_emp_inv, tipo, cantidad, fecha, referencia) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, movimiento.getIdStock());
            if (movimiento.getIdEmpleadoInventario() != null) {
                ps.setInt(2, movimiento.getIdEmpleadoInventario());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, movimiento.getTipo());
            ps.setInt(4, movimiento.getCantidad());
            ps.setDate(5, Date.valueOf(movimiento.getFecha() != null ? movimiento.getFecha() : LocalDate.now()));
            ps.setString(6, movimiento.getReferencia());

            int filas = ps.executeUpdate();
            if (filas == 0) return -1;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    // ----------------------------------------------------------------
    // OBTENER STOCK POR UBICACIÓN
    // ----------------------------------------------------------------
    @Override
    public Stock obtenerStockPorUbicacion(int idProducto, String ubicacion) throws SQLException {
        String sql = "SELECT id_stock, id_producto, cantidad_actual, stock_minimo, stock_maximo, ubicacion " +
                "FROM inv_stock " +
                "WHERE id_producto = ? AND TRIM(LOWER(ubicacion)) = TRIM(LOWER(?))";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.setString(2, ubicacion != null ? ubicacion : "");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearStock(rs);
                }
            }
        }
        return null;
    }

    // ----------------------------------------------------------------
    // LISTAR STOCK POR PRODUCTO
    // ----------------------------------------------------------------
    @Override
    public List<Stock> listarStockPorProducto(int idProducto) throws SQLException {
        String sql = "SELECT id_stock, id_producto, cantidad_actual, stock_minimo, stock_maximo, ubicacion " +
                "FROM inv_stock WHERE id_producto = ? ORDER BY ubicacion";

        List<Stock> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearStock(rs));
                }
            }
        }
        return lista;
    }

    // ----------------------------------------------------------------
    // MÉTODO PRIVADO — MAPEAR
    // ----------------------------------------------------------------
    private Stock mapearStock(ResultSet rs) throws SQLException {
        Stock s = new Stock();
        s.setIdStock(rs.getInt("id_stock"));
        s.setIdProducto(rs.getInt("id_producto"));
        s.setCantidadActual(rs.getInt("cantidad_actual"));
        s.setStockMinimo(rs.getInt("stock_minimo"));
        s.setStockMaximo(rs.getInt("stock_maximo"));
        s.setUbicacion(rs.getString("ubicacion"));
        return s;
    }
}

