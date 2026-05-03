package com.starsolutions.starsolutionscrm.dao.interfaces;

import com.starsolutions.starsolutionscrm.model.rrhh.Nomina;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface INominaDAO {

    // Registrar una nómina (NO incluir neto — la BD lo calcula)
    boolean crear(Nomina nomina) throws SQLException;

    // Consultar todas las nóminas de un empleado
    List<Nomina> obtenerPorEmpleado(int idEmpleado) throws SQLException;

    // Consultar nóminas de un período específico (ej. mayo 2026)
    List<Nomina> obtenerPorPeriodo(LocalDate periodo) throws SQLException;

    // Consultar una nómina específica de un empleado en un período
    Nomina obtenerPorEmpleadoYPeriodo(int idEmpleado, LocalDate periodo) throws SQLException;
}