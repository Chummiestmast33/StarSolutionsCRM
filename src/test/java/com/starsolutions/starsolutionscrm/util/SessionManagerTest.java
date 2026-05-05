package com.starsolutions.starsolutionscrm.util;

import com.starsolutions.starsolutionscrm.model.rrhh.Empleado;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    @BeforeEach
    void setUp() {
        // Ensure clean state before each test
        SessionManager.getInstance().cerrarSesion();
    }

    @AfterEach
    void tearDown() {
        // Leave session clean after each test
        SessionManager.getInstance().cerrarSesion();
    }

    @Test
    void getInstanceDeberiaRetornarLaMismaInstancia() {
        SessionManager sm1 = SessionManager.getInstance();
        SessionManager sm2 = SessionManager.getInstance();
        assertSame(sm1, sm2);
    }

    @Test
    void sinSesionActivaHaySesionActivaDeberiaRetornarFalse() {
        assertFalse(SessionManager.getInstance().haySesionActiva());
    }

    @Test
    void iniciarSesionDeberiaEstablecerEmpleadoYActivarSesion() {
        Empleado e = new Empleado(1, "Ana", "pass", 80.0, 75.0, "Ventas", true);
        SessionManager.getInstance().iniciarSesion(e);

        assertTrue(SessionManager.getInstance().haySesionActiva());
        assertSame(e, SessionManager.getInstance().getEmpleadoActual());
    }

    @Test
    void cerrarSesionDeberiaEliminarEmpleadoYDesactivarSesion() {
        Empleado e = new Empleado(1, "Ana", "pass", 80.0, 75.0, "Ventas", true);
        SessionManager.getInstance().iniciarSesion(e);
        SessionManager.getInstance().cerrarSesion();

        assertFalse(SessionManager.getInstance().haySesionActiva());
        assertNull(SessionManager.getInstance().getEmpleadoActual());
    }

    @Test
    void getTipoEmpleadoSinSesionDeberiaRetornarNull() {
        assertNull(SessionManager.getInstance().getTipoEmpleado());
    }

    @Test
    void getTipoEmpleadoConSesionDeberiaRetornarTipo() {
        Empleado e = new Empleado(2, "Carlos", "pass", 0, 0, "RH", true);
        SessionManager.getInstance().iniciarSesion(e);

        assertEquals("RH", SessionManager.getInstance().getTipoEmpleado());
    }

    @Test
    void iniciarSesionConNuevoEmpleadoDeberiaReemplazarSesionAnterior() {
        Empleado e1 = new Empleado(1, "Ana", "p1", 0, 0, "Ventas", true);
        Empleado e2 = new Empleado(2, "Luis", "p2", 0, 0, "RH", true);

        SessionManager.getInstance().iniciarSesion(e1);
        SessionManager.getInstance().iniciarSesion(e2);

        assertSame(e2, SessionManager.getInstance().getEmpleadoActual());
        assertEquals("RH", SessionManager.getInstance().getTipoEmpleado());
    }

    @Test
    void getEmpleadoActualSinSesionDeberiaRetornarNull() {
        assertNull(SessionManager.getInstance().getEmpleadoActual());
    }
}
