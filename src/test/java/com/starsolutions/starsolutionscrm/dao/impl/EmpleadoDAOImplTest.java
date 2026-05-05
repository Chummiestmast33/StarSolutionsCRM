package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.model.rrhh.Empleado;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmpleadoDAOImplTest {

    private static EmpleadoDAOImpl dao;

    @BeforeAll
    static void setUpClass() throws Exception {
        H2TestHelper.init("test_empleado");
        dao = new EmpleadoDAOImpl();
    }

    @AfterAll
    static void tearDownClass() throws Exception {
        H2TestHelper.dropSchema();
    }

    @BeforeEach
    void limpiarTabla() throws Exception {
        var conn = com.starsolutions.starsolutionscrm.database.DatabaseConnection.getInstance().getConnection();
        try (var s = conn.createStatement()) {
            s.execute("SET REFERENTIAL_INTEGRITY FALSE");
            s.execute("DELETE FROM rh_empleado_ventas");
            s.execute("DELETE FROM rh_empleado_rh");
            s.execute("DELETE FROM rh_empleado_inventario");
            s.execute("DELETE FROM rh_empleado_produccion");
            s.execute("DELETE FROM rh_empleado");
            s.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
    }

    // ------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------

    private Empleado crearEmpleadoVentas(String nombre, String pass) throws Exception {
        Empleado e = new Empleado();
        e.setNombre(nombre);
        e.setContrasena(pass);
        e.setTipoEmpleado("Ventas");
        dao.crear(e);
        return e;
    }

    // ------------------------------------------------------------------
    // Tests: crear
    // ------------------------------------------------------------------

    @Test
    void crearEmpleadoDeberiaRetornarTrueYAsignarNum() throws Exception {
        Empleado e = new Empleado();
        e.setNombre("Juan Pérez");
        e.setContrasena("pass123");
        e.setTipoEmpleado("RH");

        boolean resultado = dao.crear(e);

        assertTrue(resultado);
        assertTrue(e.getNum() > 0, "El num generado debe ser mayor a 0");
    }

    // ------------------------------------------------------------------
    // Tests: login
    // ------------------------------------------------------------------

    @Test
    void loginConCredencialesCorrectasDeberiaRetornarEmpleado() throws Exception {
        Empleado e = crearEmpleadoVentas("María García", "miContrasena");

        Empleado resultado = dao.login(e.getNum(), "miContrasena");

        assertNotNull(resultado);
        assertEquals(e.getNum(), resultado.getNum());
        assertEquals("María García", resultado.getNombre());
    }

    @Test
    void loginConContrasenaIncorrectaDeberiaRetornarNull() throws Exception {
        Empleado e = crearEmpleadoVentas("Carlos López", "correcto");

        Empleado resultado = dao.login(e.getNum(), "incorrecto");

        assertNull(resultado);
    }

    @Test
    void loginConNumInexistenteDeberiaRetornarNull() throws Exception {
        Empleado resultado = dao.login(99999, "cualquierPass");
        assertNull(resultado);
    }

    @Test
    void loginConEmpleadoInactivoDeberiaRetornarNull() throws Exception {
        Empleado e = crearEmpleadoVentas("Inactivo", "pass");
        dao.desactivar(e.getNum());

        Empleado resultado = dao.login(e.getNum(), "pass");
        assertNull(resultado);
    }

    // ------------------------------------------------------------------
    // Tests: obtenerTodos
    // ------------------------------------------------------------------

    @Test
    void obtenerTodosConSinEmpleadosDeberiaRetornarListaVacia() throws Exception {
        List<Empleado> lista = dao.obtenerTodos();
        assertTrue(lista.isEmpty());
    }

    @Test
    void obtenerTodosDeberiaRetornarSoloActivos() throws Exception {
        Empleado activo = crearEmpleadoVentas("Activo", "pass");
        Empleado inactivo = crearEmpleadoVentas("Inactivo", "pass");
        dao.desactivar(inactivo.getNum());

        List<Empleado> lista = dao.obtenerTodos();

        assertEquals(1, lista.size());
        assertEquals("Activo", lista.get(0).getNombre());
    }

    @Test
    void obtenerTodosDeberiaRetornarVariosEmpleadosActivos() throws Exception {
        crearEmpleadoVentas("Ana", "p1");
        crearEmpleadoVentas("Luis", "p2");

        List<Empleado> lista = dao.obtenerTodos();

        assertEquals(2, lista.size());
    }

    // ------------------------------------------------------------------
    // Tests: obtenerPorNum
    // ------------------------------------------------------------------

    @Test
    void obtenerPorNumExistenteDeberiaRetornarEmpleado() throws Exception {
        Empleado e = crearEmpleadoVentas("Rosa", "pass");

        Empleado resultado = dao.obtenerPorNum(e.getNum());

        assertNotNull(resultado);
        assertEquals("Rosa", resultado.getNombre());
    }

    @Test
    void obtenerPorNumInexistenteDeberiaRetornarNull() throws Exception {
        Empleado resultado = dao.obtenerPorNum(99999);
        assertNull(resultado);
    }

    @Test
    void obtenerPorNumInactivoDeberiaRetornarEmpleado() throws Exception {
        Empleado e = crearEmpleadoVentas("Inactivo", "pass");
        dao.desactivar(e.getNum());

        // obtenerPorNum returns regardless of activo status
        Empleado resultado = dao.obtenerPorNum(e.getNum());
        assertNotNull(resultado);
        assertFalse(resultado.isActivo());
    }

    // ------------------------------------------------------------------
    // Tests: actualizar
    // ------------------------------------------------------------------

    @Test
    void actualizarDeberiaModificarNombreYTipo() throws Exception {
        Empleado e = crearEmpleadoVentas("Nombre Original", "pass");

        e.setNombre("Nombre Actualizado");
        e.setTipoEmpleado("Inventario");
        boolean resultado = dao.actualizar(e);

        assertTrue(resultado);

        Empleado actualizado = dao.obtenerPorNum(e.getNum());
        assertEquals("Nombre Actualizado", actualizado.getNombre());
        assertEquals("Inventario", actualizado.getTipoEmpleado());
    }

    @Test
    void actualizarNumInexistenteDeberiaRetornarFalse() throws Exception {
        Empleado e = new Empleado();
        e.setNum(99999);
        e.setNombre("Fantasma");
        e.setTipoEmpleado("RH");

        boolean resultado = dao.actualizar(e);
        assertFalse(resultado);
    }

    // ------------------------------------------------------------------
    // Tests: desactivar
    // ------------------------------------------------------------------

    @Test
    void desactivarDeberiaPonerActivoEnFalso() throws Exception {
        Empleado e = crearEmpleadoVentas("Para Desactivar", "pass");

        boolean resultado = dao.desactivar(e.getNum());

        assertTrue(resultado);
        Empleado desactivado = dao.obtenerPorNum(e.getNum());
        assertFalse(desactivado.isActivo());
    }

    @Test
    void desactivarNumInexistenteDeberiaRetornarFalse() throws Exception {
        boolean resultado = dao.desactivar(99999);
        assertFalse(resultado);
    }
}
