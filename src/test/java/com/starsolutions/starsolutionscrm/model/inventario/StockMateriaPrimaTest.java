package com.starsolutions.starsolutionscrm.model.inventario;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockMateriaPrimaTest {

    @Test
    void constructorVacioDeberiaCrearInstanciaConValoresPredeterminados() {
        StockMateriaPrima s = new StockMateriaPrima();
        assertNull(s.getIdStockMateriaPrima());
        assertNull(s.getIdMateria());
        assertEquals(0, s.getCantidadActual());
        assertEquals(0, s.getStockMinimo());
        assertNull(s.getUbicacion());
    }

    @Test
    void constructorCompletoDeberiaAsignarTodosLosCampos() {
        StockMateriaPrima s = new StockMateriaPrima(1, 2, 300, 50, "Bodega 1");
        assertEquals(1, s.getIdStockMateriaPrima());
        assertEquals(2, s.getIdMateria());
        assertEquals(300, s.getCantidadActual());
        assertEquals(50, s.getStockMinimo());
        assertEquals("Bodega 1", s.getUbicacion());
    }

    @Test
    void settersDeberianActualizarCamposCorrectamente() {
        StockMateriaPrima s = new StockMateriaPrima();
        s.setIdStockMateriaPrima(4);
        s.setIdMateria(5);
        s.setCantidadActual(150);
        s.setStockMinimo(20);
        s.setUbicacion("Rack 3");

        assertEquals(4, s.getIdStockMateriaPrima());
        assertEquals(5, s.getIdMateria());
        assertEquals(150, s.getCantidadActual());
        assertEquals(20, s.getStockMinimo());
        assertEquals("Rack 3", s.getUbicacion());
    }

    @Test
    void igualMismoIdDeberiaSerIgual() {
        StockMateriaPrima s1 = new StockMateriaPrima(1, 2, 100, 10, "A");
        StockMateriaPrima s2 = new StockMateriaPrima(1, 3, 200, 20, "B");
        assertEquals(s1, s2);
    }

    @Test
    void diferenteIdNODeberiaSerIgual() {
        StockMateriaPrima s1 = new StockMateriaPrima(1, 2, 100, 10, "A");
        StockMateriaPrima s2 = new StockMateriaPrima(2, 2, 100, 10, "A");
        assertNotEquals(s1, s2);
    }

    @Test
    void mismoIdDeberianTenerMismoHashCode() {
        StockMateriaPrima s1 = new StockMateriaPrima(6, 1, 10, 1, null);
        StockMateriaPrima s2 = new StockMateriaPrima(6, 2, 20, 2, "X");
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void toStringDeberiaContenerCamposClave() {
        StockMateriaPrima s = new StockMateriaPrima(1, 2, 300, 50, "Bodega 1");
        String str = s.toString();
        assertTrue(str.contains("300"));
        assertTrue(str.contains("Bodega 1"));
    }
}
