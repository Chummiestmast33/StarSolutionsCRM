package com.starsolutions.starsolutionscrm.model.inventario;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MateriaPrimaTest {

    @Test
    void constructorVacioDeberiaCrearInstanciaConValoresPredeterminados() {
        MateriaPrima m = new MateriaPrima();
        assertNull(m.getIdMateria());
        assertNull(m.getNombre());
        assertNull(m.getUnidad());
        assertNull(m.getDescripcion());
        assertFalse(m.isActivo());
    }

    @Test
    void constructorCompletoDeberiaAsignarTodosLosCampos() {
        MateriaPrima m = new MateriaPrima(1, "Acero", "kg", "Acero inoxidable", true);
        assertEquals(1, m.getIdMateria());
        assertEquals("Acero", m.getNombre());
        assertEquals("kg", m.getUnidad());
        assertEquals("Acero inoxidable", m.getDescripcion());
        assertTrue(m.isActivo());
    }

    @Test
    void settersDeberianActualizarCamposCorrectamente() {
        MateriaPrima m = new MateriaPrima();
        m.setIdMateria(3);
        m.setNombre("Plástico");
        m.setUnidad("litros");
        m.setDescripcion("Polietileno");
        m.setActivo(true);

        assertEquals(3, m.getIdMateria());
        assertEquals("Plástico", m.getNombre());
        assertEquals("litros", m.getUnidad());
        assertEquals("Polietileno", m.getDescripcion());
        assertTrue(m.isActivo());
    }

    @Test
    void igualMismoIdDeberiaSerIgual() {
        MateriaPrima m1 = new MateriaPrima(2, "Acero", "kg", null, true);
        MateriaPrima m2 = new MateriaPrima(2, "Hierro", "ton", null, false);
        assertEquals(m1, m2);
    }

    @Test
    void diferenteIdNODeberiaSerIgual() {
        MateriaPrima m1 = new MateriaPrima(1, "Acero", "kg", null, true);
        MateriaPrima m2 = new MateriaPrima(2, "Acero", "kg", null, true);
        assertNotEquals(m1, m2);
    }

    @Test
    void mismoIdDeberianTenerMismoHashCode() {
        MateriaPrima m1 = new MateriaPrima(4, "A", "u", null, true);
        MateriaPrima m2 = new MateriaPrima(4, "B", "v", null, false);
        assertEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    void toStringDeberiaContenerCamposClave() {
        MateriaPrima m = new MateriaPrima(1, "Acero", "kg", "desc", true);
        String s = m.toString();
        assertTrue(s.contains("Acero"));
        assertTrue(s.contains("kg"));
    }
}
