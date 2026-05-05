package com.starsolutions.starsolutionscrm.model.inventario;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaProductoTest {

    @Test
    void constructorVacioDeberiaCrearInstanciaConValoresPredeterminados() {
        CategoriaProducto c = new CategoriaProducto();
        assertNull(c.getIdCategoria());
        assertNull(c.getNombre());
        assertNull(c.getDescripcion());
        assertFalse(c.isActivo());
    }

    @Test
    void constructorCompletoDeberiaAsignarTodosLosCampos() {
        CategoriaProducto c = new CategoriaProducto(1, "Electrónica", "Productos electrónicos", true);
        assertEquals(1, c.getIdCategoria());
        assertEquals("Electrónica", c.getNombre());
        assertEquals("Productos electrónicos", c.getDescripcion());
        assertTrue(c.isActivo());
    }

    @Test
    void settersDeberianActualizarCamposCorrectamente() {
        CategoriaProducto c = new CategoriaProducto();
        c.setIdCategoria(5);
        c.setNombre("Ropa");
        c.setDescripcion("Prendas de vestir");
        c.setActivo(true);

        assertEquals(5, c.getIdCategoria());
        assertEquals("Ropa", c.getNombre());
        assertEquals("Prendas de vestir", c.getDescripcion());
        assertTrue(c.isActivo());
    }

    @Test
    void igualMismoIdDeberiaSerIgual() {
        CategoriaProducto c1 = new CategoriaProducto(1, "A", null, true);
        CategoriaProducto c2 = new CategoriaProducto(1, "B", null, false);
        assertEquals(c1, c2);
    }

    @Test
    void diferenteIdNODeberiaSerIgual() {
        CategoriaProducto c1 = new CategoriaProducto(1, "A", null, true);
        CategoriaProducto c2 = new CategoriaProducto(2, "A", null, true);
        assertNotEquals(c1, c2);
    }

    @Test
    void mismoIdDeberianTenerMismoHashCode() {
        CategoriaProducto c1 = new CategoriaProducto(3, "X", null, true);
        CategoriaProducto c2 = new CategoriaProducto(3, "Y", null, false);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void igualNullDeberiaRetornarFalse() {
        CategoriaProducto c = new CategoriaProducto(1, "A", null, true);
        assertNotEquals(null, c);
    }

    @Test
    void toStringDeberiaContenerCamposClave() {
        CategoriaProducto c = new CategoriaProducto(1, "Electrónica", "desc", true);
        String s = c.toString();
        assertTrue(s.contains("Electrónica"));
        assertTrue(s.contains("true"));
    }
}
