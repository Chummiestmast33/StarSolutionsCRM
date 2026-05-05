package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.rrhh.Asistencia;
import com.starsolutions.starsolutionscrm.model.rrhh.Empleado;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AsistenciaDAOImplTest {

    private static AsistenciaDAOImpl asistenciaDAO;
    private static int idEmpleado;

    @BeforeAll
    static void setUpClass() throws Exception {
        H2TestHelper.init("test_asistencia");
        asistenciaDAO = new AsistenciaDAOImpl();

        EmpleadoDAOImpl empleadoDAO = new EmpleadoDAOImpl();
        Empleado e = new Empleado();
        e.setNombre("Empleado Asistencia");
        e.setContrasena("pass");
        e.setTipoEmpleado("Ventas");
        empleadoDAO.crear(e);
        idEmpleado = e.getNum();
    }

    @AfterAll
    static void tearDownClass() throws Exception {
        H2TestHelper.dropSchema();
    }

    @BeforeEach
    void limpiarAsistencias() throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (var s = conn.createStatement()) {
            s.execute("DELETE FROM rh_asistencia");
        }
    }

    // ------------------------------------------------------------------
    // Tests: registrarEntrada
    // ------------------------------------------------------------------

    @Test
    void registrarEntradaDeberiaRetornarTrue() throws Exception {
        boolean resultado = asistenciaDAO.registrarEntrada(idEmpleado, LocalDate.now());
        assertTrue(resultado);
    }

    @Test
    void registrarEntradaDeberiaCrearRegistroConHoraEntrada() throws Exception {
        LocalDate hoy = LocalDate.now();
        asistenciaDAO.registrarEntrada(idEmpleado, hoy);

        Asistencia a = asistenciaDAO.obtenerPorEmpleadoYFecha(idEmpleado, hoy);
        assertNotNull(a);
        assertEquals(idEmpleado, a.getIdEmpleado());
        assertEquals(hoy, a.getFecha());
        assertNotNull(a.getHoraEntrada());
        assertNull(a.getHoraSalida());
    }

    // ------------------------------------------------------------------
    // Tests: registrarSalida
    // ------------------------------------------------------------------

    @Test
    void registrarSalidaSinEntradaDeberiaRetornarFalse() throws Exception {
        LocalDate fecha = LocalDate.of(2026, 3, 1);
        boolean resultado = asistenciaDAO.registrarSalida(idEmpleado, fecha);
        assertFalse(resultado);
    }

    @Test
    void registrarSalidaDespuesDeEntradaDeberiaActualizarHora() throws Exception {
        LocalDate hoy = LocalDate.now();
        asistenciaDAO.registrarEntrada(idEmpleado, hoy);

        boolean resultado = asistenciaDAO.registrarSalida(idEmpleado, hoy);
        assertTrue(resultado);

        Asistencia a = asistenciaDAO.obtenerPorEmpleadoYFecha(idEmpleado, hoy);
        assertNotNull(a.getHoraSalida());
    }

    // ------------------------------------------------------------------
    // Tests: obtenerPorEmpleado
    // ------------------------------------------------------------------

    @Test
    void obtenerPorEmpleadoSinAsistenciasDeberiaRetornarListaVacia() throws Exception {
        List<Asistencia> lista = asistenciaDAO.obtenerPorEmpleado(idEmpleado);
        assertTrue(lista.isEmpty());
    }

    @Test
    void obtenerPorEmpleadoDeberiaRetornarTodasLasAsistencias() throws Exception {
        asistenciaDAO.registrarEntrada(idEmpleado, LocalDate.of(2026, 4, 1));
        asistenciaDAO.registrarEntrada(idEmpleado, LocalDate.of(2026, 4, 2));
        asistenciaDAO.registrarEntrada(idEmpleado, LocalDate.of(2026, 4, 3));

        List<Asistencia> lista = asistenciaDAO.obtenerPorEmpleado(idEmpleado);
        assertEquals(3, lista.size());
    }

    // ------------------------------------------------------------------
    // Tests: obtenerPorEmpleadoYFecha
    // ------------------------------------------------------------------

    @Test
    void obtenerPorEmpleadoYFechaExistenteDeberiaRetornarAsistencia() throws Exception {
        LocalDate fecha = LocalDate.of(2026, 5, 10);
        asistenciaDAO.registrarEntrada(idEmpleado, fecha);

        Asistencia a = asistenciaDAO.obtenerPorEmpleadoYFecha(idEmpleado, fecha);

        assertNotNull(a);
        assertEquals(idEmpleado, a.getIdEmpleado());
        assertEquals(fecha, a.getFecha());
    }

    @Test
    void obtenerPorEmpleadoYFechaInexistenteDeberiaRetornarNull() throws Exception {
        Asistencia a = asistenciaDAO.obtenerPorEmpleadoYFecha(idEmpleado, LocalDate.of(1999, 1, 1));
        assertNull(a);
    }
}
