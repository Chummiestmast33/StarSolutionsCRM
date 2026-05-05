package com.starsolutions.starsolutionscrm.model.inventario;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {

    @Test
    void constructorVacioDeberiaCrearInstanciaConValoresPredeterminados() {
        Stock s = new Stock();
        assertNull(s.getIdStock());
        assertNull(s.getIdProducto());
        assertEquals(0, s.getCantidadActual());
        assertEquals(0, s.getStockMinimo());
        assertNull(s.getStockMaximo());
        assertNull(s.getUbicacion());
    }

    @Test
    void constructorCompletoDeberiaAsignarTodosLosCampos() {
        Stock s = new Stock(1, 2, 50, 10, 200, "Almacén A");
        assertEquals(1, s.getIdStock());
        assertEquals(2, s.getIdProducto());
        assertEquals(50, s.getCantidadActual());
        assertEquals(10, s.getStockMinimo());
        assertEquals(200, s.getStockMaximo());
        assertEquals("Almacén A", s.getUbicacion());
    }

    @Test
    void settersDeberianActualizarCamposCorrectamente() {
        Stock s = new Stock();
        s.setIdStock(3);
        s.setIdProducto(4);
        s.setCantidadActual(100);
        s.setStockMinimo(5);
        s.setStockMaximo(500);
        s.setUbicacion("Estante 2");

        assertEquals(3, s.getIdStock());
        assertEquals(4, s.getIdProducto());
        assertEquals(100, s.getCantidadActual());
        assertEquals(5, s.getStockMinimo());
        assertEquals(500, s.getStockMaximo());
        assertEquals("Estante 2", s.getUbicacion());
    }

    @Test
    void igualMismoIdDeberiaSerIgual() {
        Stock s1 = new Stock(1, 2, 10, 5, 100, "A");
        Stock s2 = new Stock(1, 3, 20, 5, 200, "B");
        assertEquals(s1, s2);
    }

    @Test
    void diferenteIdNODeberiaSerIgual() {
        Stock s1 = new Stock(1, 2, 10, 5, 100, "A");
        Stock s2 = new Stock(2, 2, 10, 5, 100, "A");
        assertNotEquals(s1, s2);
    }

    @Test
    void mismoIdDeberianTenerMismoHashCode() {
        Stock s1 = new Stock(5, 1, 1, 0, null, null);
        Stock s2 = new Stock(5, 2, 2, 1, 10, "B");
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void stockMaximoNuloPorDefecto() {
        Stock s = new Stock();
        assertNull(s.getStockMaximo());
    }

    @Test
    void toStringDeberiaContenerCamposClave() {
        Stock s = new Stock(1, 2, 50, 10, 200, "Almacén A");
        String str = s.toString();
        assertTrue(str.contains("50"));
        assertTrue(str.contains("Almacén A"));
    }
}
