package com.starsolutions.starsolutionscrm.model.crm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void constructorVacioDeberiaIniciarValoresPorDefecto() {
        Cliente c = new Cliente();
        assertEquals(0, c.getIdCliente());
        assertNull(c.getNombre());
        assertFalse(c.isActivo());
    }

    @Test
    void settersDeberianActualizarCamposCorrectamente() {
        Cliente c = new Cliente();
        c.setIdCliente(100);
        c.setNombre("Tech Solutions");
        c.setActivo(true);

        assertEquals(100, c.getIdCliente());
        assertEquals("Tech Solutions", c.getNombre());
        assertTrue(c.isActivo());
    }

    @Test
    void igualMismoIdDeberiaSerIgual() {
        Cliente c1 = new Cliente();
        c1.setIdCliente(50);
        c1.setNombre("Empresa A");

        Cliente c2 = new Cliente();
        c2.setIdCliente(50);
        c2.setNombre("Empresa B");

        assertEquals(c1, c2); // Son iguales porque el ID manda
    }

    @Test
    void diferenteIdNODeberiaSerIgual() {
        Cliente c1 = new Cliente();
        c1.setIdCliente(1);

        Cliente c2 = new Cliente();
        c2.setIdCliente(2);

        assertNotEquals(c1, c2);
    }

    @Test
    void mismoIdDeberianTenerMismoHashCode() {
        Cliente c1 = new Cliente();
        c1.setIdCliente(8);

        Cliente c2 = new Cliente();
        c2.setIdCliente(8);

        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void toStringDeberiaContenerCamposClave() {
        Cliente c = new Cliente();
        c.setIdCliente(44);
        c.setNombre("Gamer Store");

        String str = c.toString();
        assertTrue(str.contains("44"));
        assertTrue(str.contains("Gamer Store"));
    }
}