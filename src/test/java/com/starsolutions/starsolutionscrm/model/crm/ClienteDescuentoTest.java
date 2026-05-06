package com.starsolutions.starsolutionscrm.model.crm;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ClienteDescuentoTest {

    @Test
    void constructorVacioDeberiaIniciarValoresPorDefecto() {
        ClienteDescuento cd = new ClienteDescuento();

        assertEquals(0, cd.getIdDescuento());
        assertEquals(0, cd.getIdCliente());
        assertNull(cd.getDescuento());
        assertNull(cd.getDescripcion());
        assertFalse(cd.isActivo());
    }

    @Test
    void settersYGettersDeberianFuncionarCorrectamente() {
        ClienteDescuento cd = new ClienteDescuento();

        cd.setIdDescuento(5);
        cd.setIdCliente(102);
        cd.setDescuento(new BigDecimal("10.00"));
        cd.setDescripcion("Cliente Mayorista Diamante");
        cd.setActivo(true);

        assertEquals(5, cd.getIdDescuento());
        assertEquals(102, cd.getIdCliente());
        assertEquals(new BigDecimal("10.00"), cd.getDescuento());
        assertEquals("Cliente Mayorista Diamante", cd.getDescripcion());
        assertTrue(cd.isActivo());
    }
    @Test
    void igualMismoIdDeberiaSerIgual() {
        ClienteDescuento cd1 = new ClienteDescuento(); cd1.setIdDescuento(7);
        ClienteDescuento cd2 = new ClienteDescuento(); cd2.setIdDescuento(7);
        assertEquals(cd1, cd2);
    }

    @Test
    void diferenteIdNODeberiaSerIgual() {
        ClienteDescuento cd1 = new ClienteDescuento(); cd1.setIdDescuento(1);
        ClienteDescuento cd2 = new ClienteDescuento(); cd2.setIdDescuento(2);
        assertNotEquals(cd1, cd2);
    }

    @Test
    void mismoIdDeberianTenerMismoHashCode() {
        ClienteDescuento cd1 = new ClienteDescuento(); cd1.setIdDescuento(9);
        ClienteDescuento cd2 = new ClienteDescuento(); cd2.setIdDescuento(9);
        assertEquals(cd1.hashCode(), cd2.hashCode());
    }

    @Test
    void toStringDeberiaContenerCamposClave() {
        ClienteDescuento cd = new ClienteDescuento();
        cd.setIdDescuento(4);
        cd.setDescripcion("VIP Platino");
        assertTrue(cd.toString().contains("4"));
        assertTrue(cd.toString().contains("VIP Platino"));
    }
}