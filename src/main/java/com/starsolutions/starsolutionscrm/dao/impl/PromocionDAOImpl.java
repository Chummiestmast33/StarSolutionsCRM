package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.IPromocionDAO;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.ventas.Promocion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromocionDAOImpl implements IPromocionDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // Metodo para obtener todas las promociones activas
    @Override
    public List<Promocion> listarActivas() throws SQLException {
        String sql = "SELECT id_promocion, id_producto, nombre, porcentaje_desc, fecha_inicio, fecha_fin, activa " +
                "FROM ven_promocion WHERE activa = 1";
        List<Promocion> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Promocion p = new Promocion();
                p.setIdPromocion(rs.getInt("id_promocion"));
                p.setIdProducto(rs.getInt("id_producto"));
                p.setNombre(rs.getString("nombre"));
                p.setPorcentajeDesc(rs.getBigDecimal("porcentaje_desc"));

                // Manejo seguro de fechas que pueden ser nulas
                Date fInicio = rs.getDate("fecha_inicio");
                Date fFin = rs.getDate("fecha_fin");
                p.setFechaInicio(fInicio != null ? fInicio.toLocalDate() : null);
                p.setFechaFin(fFin != null ? fFin.toLocalDate() : null);

                p.setActiva(rs.getBoolean("activa"));
                lista.add(p);
            }
        }
        return lista;
    }

    // Metodo para registrar una nueva promocion
    @Override
    public boolean crear(Promocion promocion) throws SQLException {
        String sql = "INSERT INTO ven_promocion (id_producto, nombre, porcentaje_desc, fecha_inicio, fecha_fin, activa) " +
                "VALUES (?, ?, ?, ?, ?, 1)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, promocion.getIdProducto());
            ps.setString(2, promocion.getNombre());
            ps.setBigDecimal(3, promocion.getPorcentajeDesc());

            ps.setDate(4, promocion.getFechaInicio() != null ? Date.valueOf(promocion.getFechaInicio()) : null);
            ps.setDate(5, promocion.getFechaFin() != null ? Date.valueOf(promocion.getFechaFin()) : null);

            return ps.executeUpdate() > 0;
        }
    }

    // Metodo para desactivar (baja logica)
    @Override
    public boolean desactivar(int idPromocion) throws SQLException {
        String sql = "UPDATE ven_promocion SET activa = 0 WHERE id_promocion = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idPromocion);
            return ps.executeUpdate() > 0;
        }
    }
    // NUENO METODO EN PromocionDAOImpl.java
    public Promocion obtenerPromocionActivaPorProducto(int idProducto) throws SQLException {
        String sql = "SELECT * FROM ven_promocion WHERE id_producto = ? AND activa = 1 " +
                "AND (fecha_inicio IS NULL OR fecha_inicio <= CURDATE()) " +
                "AND (fecha_fin IS NULL OR fecha_fin >= CURDATE()) LIMIT 1";

        try (java.sql.PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Promocion p = new Promocion();
                    p.setIdPromocion(rs.getInt("id_promocion"));
                    p.setIdProducto(rs.getInt("id_producto"));
                    p.setPorcentajeDesc(rs.getBigDecimal("porcentaje_desc"));
                    return p;
                }
            }
        }
        return null;
    }
}