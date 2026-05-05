package com.starsolutions.starsolutionscrm.model.produccion;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrdenProduccionTest {

    @Test
    void constantesDeEstadoDeberianTenerValoresCorrectos() {
        assertEquals("En Proceso",  OrdenProduccion.ESTADO_EN_PROCESO);
        assertEquals("Completada",  OrdenProduccion.ESTADO_COMPLETADA);
        assertEquals("Cancelada",   OrdenProduccion.ESTADO_CANCELADA);
    }

    @Test
    void constructorVacioDeberiaCrearInstanciaConValoresPredeterminados() {
        OrdenProduccion o = new OrdenProduccion();
        assertEquals(0, o.getIdOrdenProd());
        assertEquals(0, o.getIdEmpleado());
        assertEquals(0, o.getIdProductoFinal());
        assertEquals(0, o.getCantidadPlanificada());
        assertEquals(0, o.getCantidadProducida());
        assertNull(o.getFechaInicio());
        assertNull(o.getFechaEstimadaFin());
        assertNull(o.getFechaRealFin());
        assertNull(o.getEstado());
    }

    @Test
    void constructorCompletoDeberiaAsignarTodosLosCampos() {
        LocalDateTime inicio = LocalDateTime.of(2026, 5, 1, 8, 0);
        LocalDate estimadaFin = LocalDate.of(2026, 5, 10);
        LocalDateTime realFin = LocalDateTime.of(2026, 5, 9, 17, 0);

        OrdenProduccion o = new OrdenProduccion(
                1, 2, 3, 100, 95, inicio, estimadaFin, realFin, "Completada");

        assertEquals(1, o.getIdOrdenProd());
        assertEquals(2, o.getIdEmpleado());
        assertEquals(3, o.getIdProductoFinal());
        assertEquals(100, o.getCantidadPlanificada());
        assertEquals(95, o.getCantidadProducida());
        assertEquals(inicio, o.getFechaInicio());
        assertEquals(estimadaFin, o.getFechaEstimadaFin());
        assertEquals(realFin, o.getFechaRealFin());
        assertEquals("Completada", o.getEstado());
    }

    @Test
    void settersDeberianActualizarCamposCorrectamente() {
        OrdenProduccion o = new OrdenProduccion();
        LocalDateTime inicio = LocalDateTime.now();
        LocalDate estimadaFin = LocalDate.now().plusDays(5);

        o.setIdOrdenProd(10);
        o.setIdEmpleado(3);
        o.setIdProductoFinal(7);
        o.setCantidadPlanificada(50);
        o.setCantidadProducida(50);
        o.setFechaInicio(inicio);
        o.setFechaEstimadaFin(estimadaFin);
        o.setFechaRealFin(null);
        o.setEstado(OrdenProduccion.ESTADO_EN_PROCESO);

        assertEquals(10, o.getIdOrdenProd());
        assertEquals(3, o.getIdEmpleado());
        assertEquals(7, o.getIdProductoFinal());
        assertEquals(50, o.getCantidadPlanificada());
        assertEquals(50, o.getCantidadProducida());
        assertEquals(inicio, o.getFechaInicio());
        assertEquals(estimadaFin, o.getFechaEstimadaFin());
        assertNull(o.getFechaRealFin());
        assertEquals(OrdenProduccion.ESTADO_EN_PROCESO, o.getEstado());
    }

    @Test
    void toStringDeberiaContenerCamposClave() {
        OrdenProduccion o = new OrdenProduccion();
        o.setIdOrdenProd(5);
        o.setEstado("En Proceso");
        o.setCantidadPlanificada(10);
        String s = o.toString();
        assertTrue(s.contains("5"));
        assertTrue(s.contains("En Proceso"));
        assertTrue(s.contains("10"));
    }
}
