package com.starsolutions.starsolutionscrm.model.ventas;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CobroTest {

    @Test
    void constructorVacioDeberiaIniciarValoresPorDefecto() {
        Cobro c = new Cobro();
        assertEquals(0, c.getIdCobro());
        assertEquals(0, c.getIdVenta());
        assertEquals(0, c.getIdCliente());
        assertNull(c.getMonto());
        assertNull(c.getFecha());
    }

    @Test
    void settersDeberianActualizarCamposCorrectamente() {
        Cobro c = new Cobro();
        LocalDate fecha = LocalDate.now();

        c.setIdCobro(10);
        c.setIdVenta(5);
        c.setIdCliente(2);
        c.setMonto(new BigDecimal("500.00"));
        c.setFecha(fecha);

        assertEquals(10, c.getIdCobro());
        assertEquals(5, c.getIdVenta());
        assertEquals(2, c.getIdCliente());
        assertEquals(new BigDecimal("500.00"), c.getMonto());
        assertEquals(fecha, c.getFecha());
    }

    @Test
    void igualMismoIdDeberiaSerIgual() {
        Cobro c1 = new Cobro(); c1.setIdCobro(25); c1.setMonto(new BigDecimal("100.00"));
        Cobro c2 = new Cobro(); c2.setIdCobro(25); c2.setMonto(new BigDecimal("999.00"));
        assertEquals(c1, c2); // Son iguales porque tienen el mismo ID de cobro
    }

    @Test
    void diferenteIdNODeberiaSerIgual() {
        Cobro c1 = new Cobro(); c1.setIdCobro(1);
        Cobro c2 = new Cobro(); c2.setIdCobro(2);
        assertNotEquals(c1, c2);
    }

    @Test
    void mismoIdDeberianTenerMismoHashCode() {
        Cobro c1 = new Cobro(); c1.setIdCobro(30);
        Cobro c2 = new Cobro(); c2.setIdCobro(30);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void toStringDeberiaContenerCamposClave() {
        Cobro c = new Cobro();
        c.setIdCobro(123);
        c.setMonto(new BigDecimal("450.50"));

        String str = c.toString();
        assertTrue(str.contains("123"));
        assertTrue(str.contains("450.50"));
    }
}