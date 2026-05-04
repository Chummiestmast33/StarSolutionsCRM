package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.INominaDAO;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.rrhh.Nomina;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NominaDAOImpl implements INominaDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ----------------------------------------------------------------
    // CREAR
    // ----------------------------------------------------------------
    @Override
    public boolean crear(Nomina nomina) throws SQLException {
        // IMPORTANTE: neto NO se incluye — es columna GENERATED en la BD
        String sql = "INSERT INTO rh_nomina (id_empleado, salario_base, deducciones, periodo) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, nomina.getIdEmpleado());
            ps.setDouble(2, nomina.getSalarioBase());
            ps.setDouble(3, nomina.getDeducciones());
            ps.setDate(4, Date.valueOf(nomina.getPeriodo()));

            int filas = ps.executeUpdate();
            if (filas == 0) return false;

            // Recuperar el id generado y asignarlo al objeto
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    nomina.setIdNomina(keys.getInt(1));
                }
            }
        }
        return true;
    }

    // ----------------------------------------------------------------
    // OBTENER POR EMPLEADO
    // ----------------------------------------------------------------
    @Override
    public List<Nomina> obtenerPorEmpleado(int idEmpleado) throws SQLException {
        String sql = "SELECT id_nomina, id_empleado, salario_base, deducciones, neto, periodo " +
                "FROM rh_nomina " +
                "WHERE id_empleado = ? " +
                "ORDER BY periodo DESC";

        List<Nomina> lista = new ArrayList<>();

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
    // OBTENER POR PERÍODO
    // ----------------------------------------------------------------
    @Override
    public List<Nomina> obtenerPorPeriodo(LocalDate periodo) throws SQLException {
        String sql = "SELECT id_nomina, id_empleado, salario_base, deducciones, neto, periodo " +
                "FROM rh_nomina " +
                "WHERE periodo = ? " +
                "ORDER BY id_empleado";

        List<Nomina> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(periodo));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    // ----------------------------------------------------------------
    // OBTENER POR EMPLEADO Y PERÍODO
    // ----------------------------------------------------------------
    @Override
    public Nomina obtenerPorEmpleadoYPeriodo(int idEmpleado, LocalDate periodo) throws SQLException {
        String sql = "SELECT id_nomina, id_empleado, salario_base, deducciones, neto, periodo " +
                "FROM rh_nomina " +
                "WHERE id_empleado = ? AND periodo = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setDate(2, Date.valueOf(periodo));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    // ----------------------------------------------------------------
    // MÉTODO PRIVADO — mapear ResultSet → Nomina
    // ----------------------------------------------------------------
    private Nomina mapear(ResultSet rs) throws SQLException {
        Nomina n = new Nomina();
        n.setIdNomina(rs.getInt("id_nomina"));
        n.setIdEmpleado(rs.getInt("id_empleado"));
        n.setSalarioBase(rs.getDouble("salario_base"));
        n.setDeducciones(rs.getDouble("deducciones"));
        n.setNeto(rs.getDouble("neto"));                        // solo lectura desde BD
        n.setPeriodo(rs.getDate("periodo").toLocalDate());
        return n;
    }
}