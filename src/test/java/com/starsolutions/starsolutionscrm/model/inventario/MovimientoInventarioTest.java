package com.starsolutions.starsolutionscrm.model.inventario;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MovimientoInventarioTest {

    @Test
    void constructorVacioDeberiaCrearInstanciaConValoresPredeterminados() {
        MovimientoInventario m = new MovimientoInventario();
        assertNull(m.getIdMovimiento());
        assertNull(m.getIdStock());
        assertNull(m.getIdEmpleadoInventario());
        assertNull(m.getTipo());
        assertEquals(0, m.getCantidad());
        assertNull(m.getFecha());
        assertNull(m.getReferencia());
    }

    @Test
    void constructorCompletoDeberiaAsignarTodosLosCampos() {
        LocalDate fecha = LocalDate.of(2026, 4, 1);
        MovimientoInventario m = new MovimientoInventario(1, 2, 3, "ENTRADA", 100, fecha, "REF-001");

        assertEquals(1, m.getIdMovimiento());
        assertEquals(2, m.getIdStock());
        assertEquals(3, m.getIdEmpleadoInventario());
        assertEquals("ENTRADA", m.getTipo());
        assertEquals(100, m.getCantidad());
        assertEquals(fecha, m.getFecha());
        assertEquals("REF-001", m.getReferencia());
    }

    @Test
    void settersDeberianActualizarCamposCorrectamente() {
        MovimientoInventario m = new MovimientoInventario();
        LocalDate fecha = LocalDate.now();
        m.setIdMovimiento(5);
        m.setIdStock(6);
        m.setIdEmpleadoInventario(7);
        m.setTipo("SALIDA");
        m.setCantidad(50);
        m.setFecha(fecha);
        m.setReferencia("REF-002");

        assertEquals(5, m.getIdMovimiento());
        assertEquals(6, m.getIdStock());
        assertEquals(7, m.getIdEmpleadoInventario());
        assertEquals("SALIDA", m.getTipo());
        assertEquals(50, m.getCantidad());
        assertEquals(fecha, m.getFecha());
        assertEquals("REF-002", m.getReferencia());
    }

    @Test
    void igualMismoIdDeberiaSerIgual() {
        MovimientoInventario m1 = new MovimientoInventario(1, 2, 3, "ENTRADA", 10, LocalDate.now(), null);
        MovimientoInventario m2 = new MovimientoInventario(1, 5, 6, "SALIDA",  20, LocalDate.now(), "X");
        assertEquals(m1, m2);
    }

    @Test
    void diferenteIdNODeberiaSerIgual() {
        MovimientoInventario m1 = new MovimientoInventario(1, 1, 1, "ENTRADA", 10, LocalDate.now(), null);
        MovimientoInventario m2 = new MovimientoInventario(2, 1, 1, "ENTRADA", 10, LocalDate.now(), null);
        assertNotEquals(m1, m2);
    }

    @Test
    void mismoIdDeberianTenerMismoHashCode() {
        MovimientoInventario m1 = new MovimientoInventario(3, 1, 1, "ENTRADA", 1, LocalDate.now(), null);
        MovimientoInventario m2 = new MovimientoInventario(3, 2, 2, "SALIDA",  5, LocalDate.now(), "x");
        assertEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    void toStringDeberiaContenerCamposClave() {
        LocalDate fecha = LocalDate.of(2026, 1, 15);
        MovimientoInventario m = new MovimientoInventario(1, 2, 3, "ENTRADA", 100, fecha, "REF");
        String s = m.toString();
        assertTrue(s.contains("ENTRADA"));
        assertTrue(s.contains("100"));
    }
}
