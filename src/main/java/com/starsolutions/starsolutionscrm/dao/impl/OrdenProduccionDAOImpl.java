package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.IOrdenProduccionDAO;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.produccion.OrdenProduccion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdenProduccionDAOImpl implements IOrdenProduccionDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ----------------------------------------------------------------
    // CREAR
    // ----------------------------------------------------------------
    @Override
    public boolean crear(OrdenProduccion orden) throws SQLException {
        String sql = "INSERT INTO prd_orden_produccion " +
                "(id_empleado, id_producto_final, cantidad_planificada, fecha_estimada_fin) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, orden.getIdEmpleado());
            ps.setInt(2, orden.getIdProductoFinal());
            ps.setInt(3, orden.getCantidadPlanificada());

            // fecha_estimada_fin puede ser null
            if (orden.getFechaEstimadaFin() != null) {
                ps.setDate(4, Date.valueOf(orden.getFechaEstimadaFin()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            int filas = ps.executeUpdate();
            if (filas == 0) return false;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    orden.setIdOrdenProd(keys.getInt(1));
                }
            }
        }
        return true;
    }

    // ----------------------------------------------------------------
    // COMPLETAR
    // ----------------------------------------------------------------
    @Override
    public boolean completar(int idOrdenProd, int cantidadProducida) throws SQLException {
        String sql = "UPDATE prd_orden_produccion " +
                "SET estado = ?, cantidad_producida = ?, fecha_real_fin = NOW() " +
                "WHERE id_orden_prod = ? AND estado = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, OrdenProduccion.ESTADO_COMPLETADA);
            ps.setInt(2, cantidadProducida);
            ps.setInt(3, idOrdenProd);
            ps.setString(4, OrdenProduccion.ESTADO_EN_PROCESO); // solo se puede completar si está En Proceso

            return ps.executeUpdate() > 0;
        }
    }

    // ----------------------------------------------------------------
    // CANCELAR
    // ----------------------------------------------------------------
    @Override
    public boolean cancelar(int idOrdenProd) throws SQLException {
        String sql = "UPDATE prd_orden_produccion " +
                "SET estado = ? " +
                "WHERE id_orden_prod = ? AND estado = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, OrdenProduccion.ESTADO_CANCELADA);
            ps.setInt(2, idOrdenProd);
            ps.setString(3, OrdenProduccion.ESTADO_EN_PROCESO); // solo se puede cancelar si está En Proceso

            return ps.executeUpdate() > 0;
        }
    }

    // ----------------------------------------------------------------
    // OBTENER TODAS
    // ----------------------------------------------------------------
    @Override
    public List<OrdenProduccion> obtenerTodas() throws SQLException {
        String sql = "SELECT id_orden_prod, id_empleado, id_producto_final, " +
                "cantidad_planificada, cantidad_producida, " +
                "fecha_inicio, fecha_estimada_fin, fecha_real_fin, estado " +
                "FROM prd_orden_produccion " +
                "ORDER BY fecha_inicio DESC";

        List<OrdenProduccion> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // ----------------------------------------------------------------
    // OBTENER POR ESTADO
    // ----------------------------------------------------------------
    @Override
    public List<OrdenProduccion> obtenerPorEstado(String estado) throws SQLException {
        String sql = "SELECT id_orden_prod, id_empleado, id_producto_final, " +
                "cantidad_planificada, cantidad_producida, " +
                "fecha_inicio, fecha_estimada_fin, fecha_real_fin, estado " +
                "FROM prd_orden_produccion " +
                "WHERE estado = ? " +
                "ORDER BY fecha_inicio DESC";

        List<OrdenProduccion> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, estado);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    // ----------------------------------------------------------------
    // OBTENER POR ID
    // ----------------------------------------------------------------
    @Override
    public OrdenProduccion obtenerPorId(int idOrdenProd) throws SQLException {
        String sql = "SELECT id_orden_prod, id_empleado, id_producto_final, " +
                "cantidad_planificada, cantidad_producida, " +
                "fecha_inicio, fecha_estimada_fin, fecha_real_fin, estado " +
                "FROM prd_orden_produccion " +
                "WHERE id_orden_prod = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idOrdenProd);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    // ----------------------------------------------------------------
    // MÉTODO PRIVADO — mapear ResultSet → OrdenProduccion
    // ----------------------------------------------------------------
    private OrdenProduccion mapear(ResultSet rs) throws SQLException {
        OrdenProduccion o = new OrdenProduccion();
        o.setIdOrdenProd(rs.getInt("id_orden_prod"));
        o.setIdEmpleado(rs.getInt("id_empleado"));
        o.setIdProductoFinal(rs.getInt("id_producto_final"));
        o.setCantidadPlanificada(rs.getInt("cantidad_planificada"));
        o.setCantidadProducida(rs.getInt("cantidad_producida"));
        o.setFechaInicio(rs.getTimestamp("fecha_inicio").toLocalDateTime());

        // fecha_estimada_fin y fecha_real_fin pueden ser NULL
        Date fechaEst = rs.getDate("fecha_estimada_fin");
        o.setFechaEstimadaFin(fechaEst != null ? fechaEst.toLocalDate() : null);

        Timestamp fechaReal = rs.getTimestamp("fecha_real_fin");
        o.setFechaRealFin(fechaReal != null ? fechaReal.toLocalDateTime() : null);

        o.setEstado(rs.getString("estado"));
        return o;
    }
}