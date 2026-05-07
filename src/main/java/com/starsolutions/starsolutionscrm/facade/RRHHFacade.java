package com.starsolutions.starsolutionscrm.facade;

import com.starsolutions.starsolutionscrm.dao.impl.AsistenciaDAOImpl;
import com.starsolutions.starsolutionscrm.dao.impl.EmpleadoDAOImpl;
import com.starsolutions.starsolutionscrm.dao.impl.NominaDAOImpl;
import com.starsolutions.starsolutionscrm.dao.interfaces.IAsistenciaDAO;
import com.starsolutions.starsolutionscrm.dao.interfaces.IEmpleadoDAO;
import com.starsolutions.starsolutionscrm.dao.interfaces.INominaDAO;
import com.starsolutions.starsolutionscrm.model.rrhh.Asistencia;
import com.starsolutions.starsolutionscrm.model.rrhh.Empleado;
import com.starsolutions.starsolutionscrm.model.rrhh.Nomina;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RRHHFacade {

    private final IEmpleadoDAO empleadoDAO = new EmpleadoDAOImpl();
    private final INominaDAO nominaDAO = new NominaDAOImpl();
    private final IAsistenciaDAO asistenciaDAO = new AsistenciaDAOImpl();

    // ----------------------------------------------------------------
    // EMPLEADOS
    // ----------------------------------------------------------------

    public List<Empleado> obtenerEmpleados() throws SQLException {
        return empleadoDAO.obtenerTodos();
    }

    public Empleado obtenerEmpleadoPorNum(int num) throws SQLException {
        return empleadoDAO.obtenerPorNum(num);
    }

    public boolean crearEmpleado(Empleado empleado) throws SQLException {
        return empleadoDAO.crear(empleado);
    }

    public boolean actualizarEmpleado(Empleado empleado) throws SQLException {
        return empleadoDAO.actualizar(empleado);
    }

    public boolean desactivarEmpleado(int num) throws SQLException {
        return empleadoDAO.desactivar(num);
    }

    // ----------------------------------------------------------------
    // NÓMINA
    // ----------------------------------------------------------------

    public boolean registrarNomina(Nomina nomina) throws SQLException {
        return nominaDAO.crear(nomina);
    }

    public List<Nomina> obtenerNominaPorEmpleado(int idEmpleado) throws SQLException {
        return nominaDAO.obtenerPorEmpleado(idEmpleado);
    }

    public List<Nomina> obtenerNominaPorPeriodo(LocalDate periodo) throws SQLException {
        return nominaDAO.obtenerPorPeriodo(periodo);
    }

    // ----------------------------------------------------------------
    // ASISTENCIA
    // ----------------------------------------------------------------

    public boolean registrarEntrada(int idEmpleado) throws SQLException {
        // Usa la fecha de hoy automáticamente
        return asistenciaDAO.registrarEntrada(idEmpleado, LocalDate.now());
    }

    public boolean registrarSalida(int idEmpleado) throws SQLException {
        // Usa la fecha de hoy automáticamente
        return asistenciaDAO.registrarSalida(idEmpleado, LocalDate.now());
    }

    public List<Asistencia> obtenerAsistenciaPorEmpleado(int idEmpleado) throws SQLException {
        return asistenciaDAO.obtenerPorEmpleado(idEmpleado);
    }

    public Asistencia obtenerAsistenciaHoy(int idEmpleado) throws SQLException {
        return asistenciaDAO.obtenerPorEmpleadoYFecha(idEmpleado, LocalDate.now());
    }

    public List<Empleado> listarEmpleadosActivos() throws SQLException {
        return empleadoDAO.obtenerTodos();
    }

    public boolean actualizarIndicadores(int num, double productividad, double eficiencia) throws SQLException {
        return empleadoDAO.actualizarIndicadores(num, productividad, eficiencia);
    }
}