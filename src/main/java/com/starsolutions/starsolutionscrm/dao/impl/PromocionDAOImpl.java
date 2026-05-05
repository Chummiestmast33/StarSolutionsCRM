package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.IPromocionDAO;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PromocionDAOImpl implements IPromocionDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public BigDecimal buscarDescuentoAplicable(int idCliente, int idProducto) throws SQLException {

        // Prioridad 1 Promo de producto
        String sqlPromo = "SELECT porcentaje_desc FROM ven_promocion " +
                "WHERE activa = 1 AND id_producto = ? " +
                "AND (fecha_inicio IS NULL OR fecha_inicio <= CURDATE()) " +
                "AND (fecha_fin IS NULL OR fecha_fin >= CURDATE()) LIMIT 1";

        try (PreparedStatement stmt = getConn().prepareStatement(sqlPromo)) {
            stmt.setInt(1, idProducto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("porcentaje_desc");
                }
            }
        }

        // Prioridad 2 Descuento general del cliente
        String sqlCli = "SELECT descuento FROM crm_cliente_descuento WHERE id_cliente = ? AND activo = 1 LIMIT 1";
        try (PreparedStatement stmt = getConn().prepareStatement(sqlCli)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("descuento");
                }
            }
        }

        // Si no hay nada retorna 0
        return BigDecimal.ZERO;
    }
}