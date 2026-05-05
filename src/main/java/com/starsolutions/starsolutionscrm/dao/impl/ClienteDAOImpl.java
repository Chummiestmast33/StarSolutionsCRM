package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.IClienteDAO;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.crm.Cliente;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements IClienteDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public BigDecimal obtenerDescuentoCliente(int idCliente) throws SQLException {
        String sql = "SELECT descuento FROM crm_cliente_descuento WHERE id_cliente = ? AND activo = 1 LIMIT 1";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("descuento");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public List<Cliente> listarActivos() throws SQLException {
        String sql = "SELECT id_cliente, nombre, direccion, rfc, activo FROM crm_cliente WHERE activo = 1 ORDER BY nombre";
        List<Cliente> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("id_cliente"));
                c.setNombre(rs.getString("nombre"));
                c.setDireccion(rs.getString("direccion"));
                c.setRfc(rs.getString("rfc"));
                c.setActivo(rs.getBoolean("activo"));
                lista.add(c);
            }
        }
        return lista;
    }

    @Override
    public Cliente buscarPorId(int idCliente) throws SQLException {
        String sql = "SELECT id_cliente, nombre, direccion, rfc, activo FROM crm_cliente WHERE id_cliente = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente c = new Cliente();
                    c.setIdCliente(rs.getInt("id_cliente"));
                    c.setNombre(rs.getString("nombre"));
                    c.setDireccion(rs.getString("direccion"));
                    c.setRfc(rs.getString("rfc"));
                    c.setActivo(rs.getBoolean("activo"));
                    return c;
                }
            }
        }
        return null;
    }

    @Override
    public boolean crear(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO crm_cliente (nombre, direccion, rfc) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDireccion());
            ps.setString(3, cliente.getRfc());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        cliente.setIdCliente(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE crm_cliente SET nombre = ?, direccion = ?, rfc = ? WHERE id_cliente = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDireccion());
            ps.setString(3, cliente.getRfc());
            ps.setInt(4, cliente.getIdCliente());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean desactivar(int idCliente) throws SQLException {
        String sql = "UPDATE crm_cliente SET activo = 0 WHERE id_cliente = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;
        }
    }
}