package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.IVentaDAO;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.ventas.Venta;
import com.starsolutions.starsolutionscrm.model.ventas.VentaDetalle;

import java.sql.*;
import java.util.List;

public class VentaDAOImpl implements IVentaDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public int registrarVentaCompleta(Venta venta, List<VentaDetalle> detalles) throws SQLException {

        String sqlVenta = "INSERT INTO ven_venta (id_cliente, id_empleado, estatus, condicion_pago) VALUES (?, ?, ?, ?)";

        // Subconsulta para no romper la regla del id promocion
        String sqlDetalle = "INSERT INTO ven_venta_detalle (id_venta, id_producto, id_promocion, cantidad, precio_unitario, descuento_aplicado) " +
                "VALUES (?, ?, " +
                "(SELECT id_promocion FROM ven_promocion WHERE id_producto = ? AND activa = 1 AND (fecha_inicio IS NULL OR fecha_inicio <= CURDATE()) AND (fecha_fin IS NULL OR fecha_fin >= CURDATE()) LIMIT 1), " +
                "?, ?, ?)";

        Connection conn = null;
        int idVentaGenerado = 0;

        try {
            conn = getConn();
            conn.setAutoCommit(false);

            // Insertar cabecera
            try (PreparedStatement stmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                stmtVenta.setInt(1, venta.getIdCliente());
                stmtVenta.setInt(2, venta.getIdEmpleado());
                stmtVenta.setString(3, venta.getEstatus() != null ? venta.getEstatus() : "Activa");
                stmtVenta.setString(4, venta.getCondicionPago() != null ? venta.getCondicionPago() : "Contado");
                stmtVenta.executeUpdate();

                try (ResultSet rs = stmtVenta.getGeneratedKeys()) {
                    if (rs.next()) {
                        idVentaGenerado = rs.getInt(1);
                    } else {
                        throw new SQLException("Fallo al obtener ID de venta");
                    }
                }
            }

            // Insertar detalles
            try (PreparedStatement stmtDetalle = conn.prepareStatement(sqlDetalle)) {
                for (VentaDetalle detalle : detalles) {
                    stmtDetalle.setInt(1, idVentaGenerado);
                    stmtDetalle.setInt(2, detalle.getIdProducto());
                    stmtDetalle.setInt(3, detalle.getIdProducto());
                    stmtDetalle.setInt(4, detalle.getCantidad());
                    stmtDetalle.setBigDecimal(5, detalle.getPrecioUnitario());
                    stmtDetalle.setBigDecimal(6, detalle.getDescuentoAplicado());

                    stmtDetalle.addBatch();
                }
                stmtDetalle.executeBatch();
            }

            conn.commit();
            return idVentaGenerado;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }
}