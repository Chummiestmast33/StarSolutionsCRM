package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.ICategoriaDAO;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.inventario.CategoriaProducto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAOImpl implements ICategoriaDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public List<CategoriaProducto> listarTodas() throws SQLException {
        String sql = "SELECT id_categoria, nombre, descripcion, activo " +
                     "FROM cat_categoria_producto ORDER BY nombre";
        List<CategoriaProducto> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    @Override
    public boolean crear(CategoriaProducto c) throws SQLException {
        String sql = "INSERT INTO cat_categoria_producto (nombre, descripcion, activo) VALUES (?, ?, 1)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean actualizar(CategoriaProducto c) throws SQLException {
        String sql = "UPDATE cat_categoria_producto SET nombre = ?, descripcion = ? WHERE id_categoria = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setInt(3, c.getIdCategoria());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean desactivar(int idCategoria) throws SQLException {
        String sql = "UPDATE cat_categoria_producto SET activo = 0 WHERE id_categoria = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            return ps.executeUpdate() > 0;
        }
    }

    private CategoriaProducto mapear(ResultSet rs) throws SQLException {
        return new CategoriaProducto(
            rs.getInt("id_categoria"),
            rs.getString("nombre"),
            rs.getString("descripcion"),
            rs.getBoolean("activo")
        );
    }
}