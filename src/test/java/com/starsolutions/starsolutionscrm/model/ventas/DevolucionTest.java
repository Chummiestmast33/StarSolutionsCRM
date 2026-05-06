package com.starsolutions.starsolutionscrm.model.ventas;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DevolucionTest {

    @Test
    void constructorVacioDeberiaIniciarValoresPorDefecto() {
        Devolucion d = new Devolucion();
        assertEquals(0, d.getIdDevolucion());
        assertEquals(0, d.getIdVenta());
        assertEquals(0, d.getIdProducto());
        assertEquals(0, d.getCantidad());
        assertNull(d.getMontoDevuelto());
        assertNull(d.getMotivo());
        assertNull(d.getFecha());
    }

    @Test
    void settersDeberianActualizarCamposCorrectamente() {
        Devolucion d = new Devolucion();
        LocalDateTime ahora = LocalDateTime.now();

        d.setIdDevolucion(1);
        d.setIdVenta(20);
        d.setIdProducto(3);
        d.setCantidad(2);
        d.setMontoDevuelto(new BigDecimal("150.00"));
        d.setMotivo("Producto dañado");
        d.setFecha(ahora);

        assertEquals(1, d.getIdDevolucion());
        assertEquals(20, d.getIdVenta());
        assertEquals(3, d.getIdProducto());
        assertEquals(2, d.getCantidad());
        assertEquals(new BigDecimal("150.00"), d.getMontoDevuelto());
        assertEquals("Producto dañado", d.getMotivo());
        assertEquals(ahora, d.getFecha());
    }

    @Test
    void igualMismoIdDeberiaSerIgual() {
        Devolucion d1 = new Devolucion(); d1.setIdDevolucion(55); d1.setMotivo("A");
        Devolucion d2 = new Devolucion(); d2.setIdDevolucion(55); d2.setMotivo("B");
        assertEquals(d1, d2);
    }

    @Test
    void diferenteIdNODeberiaSerIgual() {
        Devolucion d1 = new Devolucion(); d1.setIdDevolucion(10);
        Devolucion d2 = new Devolucion(); d2.setIdDevolucion(11);
        assertNotEquals(d1, d2);
    }

    @Test
    void mismoIdDeberianTenerMismoHashCode() {
        Devolucion d1 = new Devolucion(); d1.setIdDevolucion(77);
        Devolucion d2 = new Devolucion(); d2.setIdDevolucion(77);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void toStringDeberiaContenerCamposClave() {
        Devolucion d = new Devolucion();
        d.setIdDevolucion(88);
        d.setMotivo("Talla incorrecta");

        String str = d.toString();
        assertTrue(str.contains("88"));
        assertTrue(str.contains("Talla incorrecta"));
    }
}