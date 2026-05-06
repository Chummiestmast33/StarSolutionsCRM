package com.starsolutions.starsolutionscrm.model.ventas;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PromocionTest {

    @Test
    void constructorVacioDeberiaIniciarValoresPorDefecto() {
        Promocion p = new Promocion();

        assertEquals(0, p.getIdPromocion());
        assertEquals(0, p.getIdProducto());
        assertNull(p.getNombre());
        assertNull(p.getPorcentajeDesc());
        assertNull(p.getFechaInicio());
        assertNull(p.getFechaFin());
        assertFalse(p.isActiva()); // Los booleanos en Java inician en false por defecto
    }

    @Test
    void settersYGettersDeberianFuncionarCorrectamente() {
        Promocion p = new Promocion();
        LocalDate hoy = LocalDate.now();
        LocalDate manana = hoy.plusDays(1);

        p.setIdPromocion(1);
        p.setIdProducto(15);
        p.setNombre("Descuento Estudiantil");
        p.setPorcentajeDesc(new BigDecimal("15.50"));
        p.setFechaInicio(hoy);
        p.setFechaFin(manana);
        p.setActiva(true);

        assertEquals(1, p.getIdPromocion());
        assertEquals(15, p.getIdProducto());
        assertEquals("Descuento Estudiantil", p.getNombre());
        assertEquals(new BigDecimal("15.50"), p.getPorcentajeDesc());
        assertEquals(hoy, p.getFechaInicio());
        assertEquals(manana, p.getFechaFin());
        assertTrue(p.isActiva());
    }
    @Test
    void igualMismoIdDeberiaSerIgual() {
        Promocion p1 = new Promocion(); p1.setIdPromocion(10); p1.setNombre("A");
        Promocion p2 = new Promocion(); p2.setIdPromocion(10); p2.setNombre("B");
        assertEquals(p1, p2);
    }

    @Test
    void diferenteIdNODeberiaSerIgual() {
        Promocion p1 = new Promocion(); p1.setIdPromocion(1);
        Promocion p2 = new Promocion(); p2.setIdPromocion(2);
        assertNotEquals(p1, p2);
    }

    @Test
    void mismoIdDeberianTenerMismoHashCode() {
        Promocion p1 = new Promocion(); p1.setIdPromocion(5);
        Promocion p2 = new Promocion(); p2.setIdPromocion(5);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void toStringDeberiaContenerCamposClave() {
        Promocion p = new Promocion();
        p.setIdPromocion(3);
        p.setNombre("Promo Verano");
        assertTrue(p.toString().contains("3"));
        assertTrue(p.toString().contains("Promo Verano"));
    }
}