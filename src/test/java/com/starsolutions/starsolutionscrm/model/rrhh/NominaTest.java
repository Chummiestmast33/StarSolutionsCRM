package com.starsolutions.starsolutionscrm.model.rrhh;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class NominaTest {

    @Test
    void constructorVacioDeberiaCrearInstanciaConValoresPredeterminados() {
        Nomina n = new Nomina();
        assertEquals(0, n.getIdNomina());
        assertEquals(0, n.getIdEmpleado());
        assertEquals(0.0, n.getSalarioBase());
        assertEquals(0.0, n.getDeducciones());
        assertEquals(0.0, n.getNeto());
        assertNull(n.getPeriodo());
    }

    @Test
    void constructorCompletoDeberiaAsignarTodosLosCampos() {
        LocalDate periodo = LocalDate.of(2026, 5, 1);
        Nomina n = new Nomina(10, 3, 15000.00, 500.00, 14500.00, periodo);

        assertEquals(10, n.getIdNomina());
        assertEquals(3, n.getIdEmpleado());
        assertEquals(15000.00, n.getSalarioBase());
        assertEquals(500.00, n.getDeducciones());
        assertEquals(14500.00, n.getNeto());
        assertEquals(periodo, n.getPeriodo());
    }

    @Test
    void settersDeberianActualizarCamposCorrectamente() {
        Nomina n = new Nomina();
        LocalDate periodo = LocalDate.of(2026, 1, 1);
        n.setIdNomina(7);
        n.setIdEmpleado(2);
        n.setSalarioBase(20000.0);
        n.setDeducciones(1000.0);
        n.setNeto(19000.0);
        n.setPeriodo(periodo);

        assertEquals(7, n.getIdNomina());
        assertEquals(2, n.getIdEmpleado());
        assertEquals(20000.0, n.getSalarioBase());
        assertEquals(1000.0, n.getDeducciones());
        assertEquals(19000.0, n.getNeto());
        assertEquals(periodo, n.getPeriodo());
    }

    @Test
    void toStringDeberiaContenerCamposClave() {
        LocalDate periodo = LocalDate.of(2026, 5, 1);
        Nomina n = new Nomina(1, 2, 10000.0, 200.0, 9800.0, periodo);
        String s = n.toString();
        assertTrue(s.contains("1"));
        assertTrue(s.contains("10000"));
        assertTrue(s.contains("200"));
    }
}
