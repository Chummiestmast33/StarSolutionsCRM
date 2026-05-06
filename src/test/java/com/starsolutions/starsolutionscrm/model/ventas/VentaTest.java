package com.starsolutions.starsolutionscrm.model.ventas;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class VentaTest {

    @Test
    void constructorVacioDeberiaIniciarValoresPorDefecto() {
        Venta v = new Venta();
        assertEquals(0, v.getIdVenta());
        assertEquals(0, v.getIdCliente());
        assertNull(v.getTotal());
        assertNull(v.getEstatus());
    }

    @Test
    void settersDeberianActualizarCamposCorrectamente() {
        Venta v = new Venta();
        v.setIdVenta(5);
        v.setTotal(new BigDecimal("1500.00"));
        v.setEstatus("Activa");

        assertEquals(5, v.getIdVenta());
        assertEquals(new BigDecimal("1500.00"), v.getTotal());
        assertEquals("Activa", v.getEstatus());
    }

    @Test
    void igualMismoIdDeberiaSerIgual() {
        Venta v1 = new Venta();
        v1.setIdVenta(10);
        v1.setTotal(new BigDecimal("100.00"));

        Venta v2 = new Venta();
        v2.setIdVenta(10);
        v2.setTotal(new BigDecimal("500.00")); // Diferente total, pero mismo ID

        assertEquals(v1, v2);
    }

    @Test
    void diferenteIdNODeberiaSerIgual() {
        Venta v1 = new Venta();
        v1.setIdVenta(1);

        Venta v2 = new Venta();
        v2.setIdVenta(2);

        assertNotEquals(v1, v2);
    }

    @Test
    void mismoIdDeberianTenerMismoHashCode() {
        Venta v1 = new Venta();
        v1.setIdVenta(7);

        Venta v2 = new Venta();
        v2.setIdVenta(7);

        assertEquals(v1.hashCode(), v2.hashCode());
    }

    @Test
    void toStringDeberiaContenerCamposClave() {
        Venta v = new Venta();
        v.setIdVenta(99);
        v.setTotal(new BigDecimal("1234.56"));

        String str = v.toString();
        assertTrue(str.contains("99"));
        assertTrue(str.contains("1234.56"));
    }
}