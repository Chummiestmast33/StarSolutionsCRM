package com.starsolutions.starsolutionscrm.dao.interfaces;

import com.starsolutions.starsolutionscrm.model.rrhh.Asistencia;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface IAsistenciaDAO {

    // Registrar entrada (crea el registro del día con hora_entrada)
    boolean registrarEntrada(int idEmpleado, LocalDate fecha) throws SQLException;

    // Registrar salida (actualiza hora_salida del registro existente)
    boolean registrarSalida(int idEmpleado, LocalDate fecha) throws SQLException;

    // Consultar asistencias de un empleado en un rango de fechas
    List<Asistencia> obtenerPorEmpleado(int idEmpleado) throws SQLException;

    // Consultar asistencia de un empleado en una fecha específica
    Asistencia obtenerPorEmpleadoYFecha(int idEmpleado, LocalDate fecha) throws SQLException;
}