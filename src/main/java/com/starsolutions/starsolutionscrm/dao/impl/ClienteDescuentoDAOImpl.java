package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.IClienteDescuentoDAO;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.crm.ClienteDescuento;

import java.sql.*;

public class ClienteDescuentoDAOImpl implements IClienteDescuentoDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public ClienteDescuento obtenerPorCliente(int idCliente) throws SQLException {
        String sql = "SELECT id_descuento, id_cliente, descuento, descripcion, activo " +
                "FROM crm_cliente_descuento WHERE id_cliente = ? AND activo = 1";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ClienteDescuento cd = new ClienteDescuento();
                    cd.setIdDescuento(rs.getInt("id_descuento"));
                    cd.setIdCliente(rs.getInt("id_cliente"));
                    cd.setDescuento(rs.getBigDecimal("descuento"));
                    cd.setDescripcion(rs.getString("descripcion"));
                    cd.setActivo(rs.getBoolean("activo"));
                    return cd;
                }
            }
        }
        return null;
    }

    @Override
    public boolean guardar(ClienteDescuento cd) throws SQLException {
        // Verifica si el cliente ya tiene un registro
        ClienteDescuento existente = obtenerPorCliente(cd.getIdCliente());

        String sql;
        if (existente == null) {
            sql = "INSERT INTO crm_cliente_descuento (id_cliente, descuento, descripcion, activo) VALUES (?, ?, ?, 1)";
            try (PreparedStatement ps = getConn().prepareStatement(sql)) {
                ps.setInt(1, cd.getIdCliente());
                ps.setBigDecimal(2, cd.getDescuento());
                ps.setString(3, cd.getDescripcion());
                return ps.executeUpdate() > 0;
            }
        } else {
            sql = "UPDATE crm_cliente_descuento SET descuento = ?, descripcion = ? WHERE id_cliente = ?";
            try (PreparedStatement ps = getConn().prepareStatement(sql)) {
                ps.setBigDecimal(1, cd.getDescuento());
                ps.setString(2, cd.getDescripcion());
                ps.setInt(3, cd.getIdCliente());
                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public boolean desactivar(int idDescuento) throws SQLException {
        String sql = "UPDATE crm_cliente_descuento SET activo = 0 WHERE id_descuento = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idDescuento);
            return ps.executeUpdate() > 0;
        }
    }
}