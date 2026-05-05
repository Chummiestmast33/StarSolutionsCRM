package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.rrhh.Empleado;
import com.starsolutions.starsolutionscrm.model.rrhh.Nomina;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NominaDAOImplTest {

    private static NominaDAOImpl nominaDAO;
    private static int idEmpleado;

    @BeforeAll
    static void setUpClass() throws Exception {
        H2TestHelper.init("test_nomina");
        nominaDAO = new NominaDAOImpl();

        // Create a reusable employee for all nomina tests
        EmpleadoDAOImpl empleadoDAO = new EmpleadoDAOImpl();
        Empleado e = new Empleado();
        e.setNombre("Empleado Nomina");
        e.setContrasena("x");
        e.setTipoEmpleado("RH");
        empleadoDAO.crear(e);
        idEmpleado = e.getNum();
    }

    @AfterAll
    static void tearDownClass() throws Exception {
        H2TestHelper.dropSchema();
    }

    @BeforeEach
    void limpiarNominas() throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (var s = conn.createStatement()) {
            s.execute("DELETE FROM rh_nomina");
        }
    }

    // ------------------------------------------------------------------
    // Tests: crear
    // ------------------------------------------------------------------

    @Test
    void crearNominaDeberiaRetornarTrueYAsignarId() throws Exception {
        Nomina n = new Nomina();
        n.setIdEmpleado(idEmpleado);
        n.setSalarioBase(10000.0);
        n.setDeducciones(500.0);
        n.setPeriodo(LocalDate.of(2026, 5, 1));

        boolean resultado = nominaDAO.crear(n);

        assertTrue(resultado);
        assertTrue(n.getIdNomina() > 0);
    }

    @Test
    void crearNominaConCeroDeducciones() throws Exception {
        Nomina n = new Nomina();
        n.setIdEmpleado(idEmpleado);
        n.setSalarioBase(8000.0);
        n.setDeducciones(0.0);
        n.setPeriodo(LocalDate.of(2026, 4, 1));

        boolean resultado = nominaDAO.crear(n);
        assertTrue(resultado);
    }

    // ------------------------------------------------------------------
    // Tests: obtenerPorEmpleado
    // ------------------------------------------------------------------

    @Test
    void obtenerPorEmpleadoSinNominasDeberiaRetornarListaVacia() throws Exception {
        List<Nomina> lista = nominaDAO.obtenerPorEmpleado(idEmpleado);
        assertTrue(lista.isEmpty());
    }

    @Test
    void obtenerPorEmpleadoDeberiaRetornarNominasDelEmpleado() throws Exception {
        Nomina n1 = buildNomina(idEmpleado, 10000.0, 500.0, LocalDate.of(2026, 1, 1));
        Nomina n2 = buildNomina(idEmpleado, 11000.0, 600.0, LocalDate.of(2026, 2, 1));
        nominaDAO.crear(n1);
        nominaDAO.crear(n2);

        List<Nomina> lista = nominaDAO.obtenerPorEmpleado(idEmpleado);

        assertEquals(2, lista.size());
    }

    @Test
    void obtenerPorEmpleadoDeberiaCalcularNetoCorrecto() throws Exception {
        Nomina n = buildNomina(idEmpleado, 10000.0, 1500.0, LocalDate.of(2026, 3, 1));
        nominaDAO.crear(n);

        List<Nomina> lista = nominaDAO.obtenerPorEmpleado(idEmpleado);

        assertEquals(1, lista.size());
        assertEquals(8500.0, lista.get(0).getNeto(), 0.01);
    }

    // ------------------------------------------------------------------
    // Tests: obtenerPorPeriodo
    // ------------------------------------------------------------------

    @Test
    void obtenerPorPeriodoDeberiaRetornarNominasDePeriodo() throws Exception {
        LocalDate mayo = LocalDate.of(2026, 5, 1);
        LocalDate junio = LocalDate.of(2026, 6, 1);
        nominaDAO.crear(buildNomina(idEmpleado, 10000.0, 0.0, mayo));
        nominaDAO.crear(buildNomina(idEmpleado, 10000.0, 0.0, junio));

        List<Nomina> lista = nominaDAO.obtenerPorPeriodo(mayo);

        assertEquals(1, lista.size());
        assertEquals(mayo, lista.get(0).getPeriodo());
    }

    @Test
    void obtenerPorPeriodoSinResultadosDeberiaRetornarListaVacia() throws Exception {
        List<Nomina> lista = nominaDAO.obtenerPorPeriodo(LocalDate.of(2020, 1, 1));
        assertTrue(lista.isEmpty());
    }

    // ------------------------------------------------------------------
    // Tests: obtenerPorEmpleadoYPeriodo
    // ------------------------------------------------------------------

    @Test
    void obtenerPorEmpleadoYPeriodoDeberiaRetornarNominaCorrecta() throws Exception {
        LocalDate periodo = LocalDate.of(2026, 5, 1);
        Nomina n = buildNomina(idEmpleado, 12000.0, 800.0, periodo);
        nominaDAO.crear(n);

        Nomina resultado = nominaDAO.obtenerPorEmpleadoYPeriodo(idEmpleado, periodo);

        assertNotNull(resultado);
        assertEquals(12000.0, resultado.getSalarioBase(), 0.01);
        assertEquals(800.0, resultado.getDeducciones(), 0.01);
        assertEquals(11200.0, resultado.getNeto(), 0.01);
    }

    @Test
    void obtenerPorEmpleadoYPeriodoInexistenteDeberiaRetornarNull() throws Exception {
        Nomina resultado = nominaDAO.obtenerPorEmpleadoYPeriodo(idEmpleado, LocalDate.of(1999, 1, 1));
        assertNull(resultado);
    }

    // ------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------

    private Nomina buildNomina(int idEmp, double base, double ded, LocalDate periodo) {
        Nomina n = new Nomina();
        n.setIdEmpleado(idEmp);
        n.setSalarioBase(base);
        n.setDeducciones(ded);
        n.setPeriodo(periodo);
        return n;
    }
}
