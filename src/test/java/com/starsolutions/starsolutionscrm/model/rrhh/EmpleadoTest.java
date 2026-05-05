package com.starsolutions.starsolutionscrm.model.rrhh;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmpleadoTest {

    @Test
    void constructorVacioDeberiaCrearInstanciaConValoresPredeterminados() {
        Empleado e = new Empleado();
        assertEquals(0, e.getNum());
        assertNull(e.getNombre());
        assertNull(e.getContrasena());
        assertEquals(0.0, e.getProductividad());
        assertEquals(0.0, e.getEficiencia());
        assertNull(e.getTipoEmpleado());
        assertFalse(e.isActivo());
    }

    @Test
    void constructorCompletoDeberiaAsignarTodosLosCampos() {
        Empleado e = new Empleado(1, "Ana López", "secret", 90.5, 85.0, "Ventas", true);
        assertEquals(1, e.getNum());
        assertEquals("Ana López", e.getNombre());
        assertEquals("secret", e.getContrasena());
        assertEquals(90.5, e.getProductividad());
        assertEquals(85.0, e.getEficiencia());
        assertEquals("Ventas", e.getTipoEmpleado());
        assertTrue(e.isActivo());
    }

    @Test
    void settersDeberianActualizarCamposCorrectamente() {
        Empleado e = new Empleado();
        e.setNum(5);
        e.setNombre("Carlos");
        e.setContrasena("pass123");
        e.setProductividad(70.0);
        e.setEficiencia(60.0);
        e.setTipoEmpleado("RH");
        e.setActivo(true);

        assertEquals(5, e.getNum());
        assertEquals("Carlos", e.getNombre());
        assertEquals("pass123", e.getContrasena());
        assertEquals(70.0, e.getProductividad());
        assertEquals(60.0, e.getEficiencia());
        assertEquals("RH", e.getTipoEmpleado());
        assertTrue(e.isActivo());
    }

    @Test
    void toStringDeberiaContenercamposClave() {
        Empleado e = new Empleado(2, "María", "x", 0, 0, "Inventario", false);
        String s = e.toString();
        assertTrue(s.contains("2"));
        assertTrue(s.contains("María"));
        assertTrue(s.contains("Inventario"));
        assertTrue(s.contains("false"));
    }

    @Test
    void activoFalsePorDefectoConConstructorVacio() {
        Empleado e = new Empleado();
        assertFalse(e.isActivo());
    }
}
