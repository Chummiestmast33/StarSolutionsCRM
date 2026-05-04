package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.IAsistenciaDAO;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.rrhh.Asistencia;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaDAOImpl implements IAsistenciaDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ----------------------------------------------------------------
    // REGISTRAR ENTRADA
    // ----------------------------------------------------------------
    @Override
    public boolean registrarEntrada(int idEmpleado, LocalDate fecha) throws SQLException {
        String sql = "INSERT INTO rh_asistencia (id_empleado, fecha, hora_entrada) " +
                "VALUES (?, ?, NOW())";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setDate(2, Date.valueOf(fecha));
            return ps.executeUpdate() > 0;
        }
    }

    // ----------------------------------------------------------------
    // REGISTRAR SALIDA
    // ----------------------------------------------------------------
    @Override
    public boolean registrarSalida(int idEmpleado, LocalDate fecha) throws SQLException {
        String sql = "UPDATE rh_asistencia " +
                "SET hora_salida = NOW() " +
                "WHERE id_empleado = ? AND fecha = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setDate(2, Date.valueOf(fecha));
            return ps.executeUpdate() > 0;
        }
    }

    // ----------------------------------------------------------------
    // OBTENER POR EMPLEADO
    // ----------------------------------------------------------------
    @Override
    public List<Asistencia> obtenerPorEmpleado(int idEmpleado) throws SQLException {
        String sql = "SELECT id_asistencia, id_empleado, fecha, hora_entrada, hora_salida " +
                "FROM rh_asistencia " +
                "WHERE id_empleado = ? " +
                "ORDER BY fecha DESC";

        List<Asistencia> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    // ----------------------------------------------------------------
    // OBTENER POR EMPLEADO Y FECHA
    // ----------------------------------------------------------------
    @Override
    public Asistencia obtenerPorEmpleadoYFecha(int idEmpleado, LocalDate fecha) throws SQLException {
        String sql = "SELECT id_asistencia, id_empleado, fecha, hora_entrada, hora_salida " +
                "FROM rh_asistencia " +
                "WHERE id_empleado = ? AND fecha = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setDate(2, Date.valueOf(fecha));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    // ----------------------------------------------------------------
    // MÉTODO PRIVADO — mapear ResultSet → Asistencia
    // ----------------------------------------------------------------
    private Asistencia mapear(ResultSet rs) throws SQLException {
        Asistencia a = new Asistencia();
        a.setIdAsistencia(rs.getInt("id_asistencia"));
        a.setIdEmpleado(rs.getInt("id_empleado"));
        a.setFecha(rs.getDate("fecha").toLocalDate());

        // hora_entrada y hora_salida pueden ser NULL
        Time entrada = rs.getTime("hora_entrada");
        Time salida  = rs.getTime("hora_salida");
        a.setHoraEntrada(entrada != null ? entrada.toLocalTime() : null);
        a.setHoraSalida(salida   != null ? salida.toLocalTime()  : null);

        return a;
    }
}