package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.IProveedorDAO;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.crm.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAOImpl implements IProveedorDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public List<Proveedor> listarActivos() throws SQLException {
        String sql = "SELECT id_proveedor, nombre, direccion, rfc, activo FROM crm_proveedor WHERE activo = 1 ORDER BY nombre";
        List<Proveedor> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setIdProveedor(rs.getInt("id_proveedor"));
                p.setNombre(rs.getString("nombre"));
                p.setDireccion(rs.getString("direccion"));
                p.setRfc(rs.getString("rfc"));
                p.setActivo(rs.getBoolean("activo"));
                lista.add(p);
            }
        }
        return lista;
    }

    @Override
    public Proveedor buscarPorId(int idProveedor) throws SQLException {
        String sql = "SELECT id_proveedor, nombre, direccion, rfc, activo FROM crm_proveedor WHERE id_proveedor = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Proveedor p = new Proveedor();
                    p.setIdProveedor(rs.getInt("id_proveedor"));
                    p.setNombre(rs.getString("nombre"));
                    p.setDireccion(rs.getString("direccion"));
                    p.setRfc(rs.getString("rfc"));
                    p.setActivo(rs.getBoolean("activo"));
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public boolean crear(Proveedor proveedor) throws SQLException {
        String sql = "INSERT INTO crm_proveedor (nombre, direccion, rfc) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getDireccion());
            ps.setString(3, proveedor.getRfc());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        proveedor.setIdProveedor(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean actualizar(Proveedor proveedor) throws SQLException {
        String sql = "UPDATE crm_proveedor SET nombre = ?, direccion = ?, rfc = ? WHERE id_proveedor = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getDireccion());
            ps.setString(3, proveedor.getRfc());
            ps.setInt(4, proveedor.getIdProveedor());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean desactivar(int idProveedor) throws SQLException {
        String sql = "UPDATE crm_proveedor SET activo = 0 WHERE id_proveedor = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            return ps.executeUpdate() > 0;
        }
    }
}