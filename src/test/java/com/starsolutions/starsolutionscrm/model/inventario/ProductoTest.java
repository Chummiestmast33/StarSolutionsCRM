package com.starsolutions.starsolutionscrm.model.inventario;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    @Test
    void constructorVacioDeberiaCrearInstanciaConValoresPredeterminados() {
        Producto p = new Producto();
        assertNull(p.getIdProducto());
        assertNull(p.getNombre());
        assertNull(p.getDescripcion());
        assertNull(p.getPrecioUnitario());
        assertNull(p.getIdCategoria());
        assertFalse(p.isActivo());
    }

    @Test
    void constructorCompletoDeberiaAsignarTodosLosCampos() {
        Producto p = new Producto(1, "Widget", "desc", new BigDecimal("9.99"), 2, true);
        assertEquals(1, p.getIdProducto());
        assertEquals("Widget", p.getNombre());
        assertEquals("desc", p.getDescripcion());
        assertEquals(new BigDecimal("9.99"), p.getPrecioUnitario());
        assertEquals(2, p.getIdCategoria());
        assertTrue(p.isActivo());
    }

    @Test
    void settersDeberianActualizarCamposCorrectamente() {
        Producto p = new Producto();
        p.setIdProducto(5);
        p.setNombre("Gadget");
        p.setDescripcion("some desc");
        p.setPrecioUnitario(new BigDecimal("19.99"));
        p.setIdCategoria(3);
        p.setActivo(true);

        assertEquals(5, p.getIdProducto());
        assertEquals("Gadget", p.getNombre());
        assertEquals("some desc", p.getDescripcion());
        assertEquals(new BigDecimal("19.99"), p.getPrecioUnitario());
        assertEquals(3, p.getIdCategoria());
        assertTrue(p.isActivo());
    }

    @Test
    void igualMismoIdDeberiaSerIgual() {
        Producto p1 = new Producto(1, "A", null, BigDecimal.ONE, 1, true);
        Producto p2 = new Producto(1, "B", null, BigDecimal.TEN, 2, false);
        assertEquals(p1, p2);
    }

    @Test
    void diferenteIdNODeberiaSerIgual() {
        Producto p1 = new Producto(1, "A", null, BigDecimal.ONE, 1, true);
        Producto p2 = new Producto(2, "A", null, BigDecimal.ONE, 1, true);
        assertNotEquals(p1, p2);
    }

    @Test
    void mismoIdDeberianTenerMismoHashCode() {
        Producto p1 = new Producto(3, "X", null, BigDecimal.ONE, 1, true);
        Producto p2 = new Producto(3, "Y", null, BigDecimal.TEN, 2, false);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void igualMismaInstanciaDeberiaRetornarTrue() {
        Producto p = new Producto(1, "A", null, BigDecimal.ONE, 1, true);
        assertEquals(p, p);
    }

    @Test
    void igualNullDeberiaRetornarFalse() {
        Producto p = new Producto(1, "A", null, BigDecimal.ONE, 1, true);
        assertNotEquals(null, p);
    }

    @Test
    void toStringDeberiaContenerCamposClave() {
        Producto p = new Producto(1, "Widget", "desc", new BigDecimal("9.99"), 2, true);
        String s = p.toString();
        assertTrue(s.contains("Widget"));
        assertTrue(s.contains("9.99"));
    }
}
