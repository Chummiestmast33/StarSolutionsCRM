package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.IVentaDAO;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.ventas.Cobro;
import com.starsolutions.starsolutionscrm.model.ventas.Venta;
import com.starsolutions.starsolutionscrm.model.ventas.VentaDetalle;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDAOImpl implements IVentaDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public int crearVentaConDetalles(Venta venta, List<VentaDetalle> detalles) throws SQLException {
        Connection conn = getConn();
        String sqlVenta = "INSERT INTO ven_venta (id_cliente, id_empleado, subtotal, descuento_aplicado, total, estatus, condicion_pago, fecha) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO ven_venta_detalle (id_venta, id_producto, cantidad, precio_unitario, id_promocion, descuento_aplicado) VALUES (?, ?, ?, ?, ?, ?)";

        int idVentaGenerado = -1;

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement psVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setInt(1, venta.getIdCliente());
                psVenta.setInt(2, venta.getIdEmpleado());
                psVenta.setBigDecimal(3, venta.getSubtotal());
                psVenta.setBigDecimal(4, venta.getDescuentoAplicado());
                psVenta.setBigDecimal(5, venta.getTotal());
                psVenta.setString(6, venta.getEstatus() != null ? venta.getEstatus() : "Activa");
                psVenta.setString(7, venta.getCondicionPago() != null ? venta.getCondicionPago() : "Contado");
                psVenta.setDate(8, Date.valueOf(LocalDate.now()));

                psVenta.executeUpdate();
                try (ResultSet rs = psVenta.getGeneratedKeys()) {
                    if (rs.next()) {
                        idVentaGenerado = rs.getInt(1);
                    }
                }
            }

            if (idVentaGenerado != -1) {
                try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle)) {
                    for (VentaDetalle det : detalles) {
                        psDetalle.setInt(1, idVentaGenerado);
                        psDetalle.setInt(2, det.getIdProducto());
                        psDetalle.setInt(3, det.getCantidad());
                        psDetalle.setBigDecimal(4, det.getPrecioUnitario());

                        // Si la regla de 2 prioridades encontro una promo, la guardamos
                        if (det.getIdPromocion() != null) {
                            psDetalle.setInt(5, det.getIdPromocion());
                        } else {
                            psDetalle.setNull(5, Types.INTEGER);
                        }
                        psDetalle.setBigDecimal(6, det.getDescuentoAplicado());
                        psDetalle.addBatch();
                    }
                    psDetalle.executeBatch();
                }
            }

            conn.commit();
            return idVentaGenerado;

        } catch (SQLException e) {
            conn.rollback();
            throw new SQLException("Error al registrar venta: " + e.getMessage());
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public BigDecimal obtenerSaldoPendiente(int idVenta) throws SQLException {
        String sql = "SELECT v.total - COALESCE((SELECT SUM(monto) FROM ven_cobro WHERE id_venta = ?), 0) AS saldo " +
                "FROM ven_venta v WHERE v.id_venta = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ps.setInt(2, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("saldo");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public boolean registrarCobro(Cobro cobro) throws SQLException {
        String sql = "INSERT INTO ven_cobro (id_venta, id_cliente, monto, fecha) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, cobro.getIdVenta());
            ps.setInt(2, cobro.getIdCliente());
            ps.setBigDecimal(3, cobro.getMonto());
            ps.setDate(4, Date.valueOf(LocalDate.now()));
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean actualizarEstatus(int idVenta, String estatus) throws SQLException {
        String sql = "UPDATE ven_venta SET estatus = ? WHERE id_venta = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, estatus);
            ps.setInt(2, idVenta);
            return ps.executeUpdate() > 0;
        }
    }

    // ==========================================
    // NUEVO METODO: Listar Historial de Ventas
    // ==========================================
    @Override
    public List<Venta> listarVentas() throws SQLException {
        String sql = "SELECT id_venta, id_cliente, id_empleado, subtotal, descuento_aplicado, total, estatus, condicion_pago, fecha " +
                "FROM ven_venta ORDER BY fecha DESC, id_venta DESC";

        List<Venta> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Venta v = new Venta();
                v.setIdVenta(rs.getInt("id_venta"));
                v.setIdCliente(rs.getInt("id_cliente"));
                v.setIdEmpleado(rs.getInt("id_empleado"));
                v.setSubtotal(rs.getBigDecimal("subtotal"));
                v.setDescuentoAplicado(rs.getBigDecimal("descuento_aplicado"));
                v.setTotal(rs.getBigDecimal("total"));
                v.setEstatus(rs.getString("estatus"));
                v.setCondicionPago(rs.getString("condicion_pago"));

                Date fechaSql = rs.getDate("fecha");
                if (fechaSql != null) {
                    v.setFecha(fechaSql.toLocalDate());
                }

                lista.add(v);
            }
        }
        return lista;
    }
}