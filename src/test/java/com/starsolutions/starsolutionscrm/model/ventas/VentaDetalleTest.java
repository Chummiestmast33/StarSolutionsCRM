package com.starsolutions.starsolutionscrm.model.ventas;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class VentaDetalleTest {

    @Test
    void constructorVacioDeberiaCrearInstanciaConValoresPredeterminados() {
        VentaDetalle vd = new VentaDetalle();

        // Verificamos que los enteros primitivos inicien en 0
        assertEquals(0, vd.getIdDetalle());
        assertEquals(0, vd.getIdVenta());
        assertEquals(0, vd.getIdProducto());
        assertEquals(0, vd.getCantidad());

        // Verificamos que los objetos inicien nulos o con sus valores por defecto
        assertNull(vd.getIdPromocion());
        assertNull(vd.getPrecioUnitario());
        assertEquals(BigDecimal.ZERO, vd.getDescuentoAplicado());
    }

    @Test
    void constructorConParametrosDeberiaAsignarCamposCorrectamente() {
        BigDecimal precio = new BigDecimal("150.50");
        VentaDetalle vd = new VentaDetalle(10, 3, precio); // idProducto, cantidad, precioUnitario

        assertEquals(10, vd.getIdProducto());
        assertEquals(3, vd.getCantidad());
        assertEquals(precio, vd.getPrecioUnitario());
    }

    @Test
    void settersDeberianActualizarCamposCorrectamente() {
        VentaDetalle vd = new VentaDetalle();

        vd.setIdDetalle(1);
        vd.setIdVenta(100);
        vd.setIdProducto(5);
        vd.setIdPromocion(2); // Integer permite valores nulos, pero aqui probamos un entero
        vd.setCantidad(2);
        vd.setPrecioUnitario(new BigDecimal("200.00"));
        vd.setDescuentoAplicado(new BigDecimal("50.00"));

        assertEquals(1, vd.getIdDetalle());
        assertEquals(100, vd.getIdVenta());
        assertEquals(5, vd.getIdProducto());
        assertEquals(2, vd.getIdPromocion());
        assertEquals(2, vd.getCantidad());
        assertEquals(new BigDecimal("200.00"), vd.getPrecioUnitario());
        assertEquals(new BigDecimal("50.00"), vd.getDescuentoAplicado());
    }

    @Test
    void getImporteTotalLineaDeberiaCalcularCorrectamenteSinDescuento() {
        // Llevamos 4 productos de $50.00 cada uno = $200.00
        VentaDetalle vd = new VentaDetalle(1, 4, new BigDecimal("50.00"));

        BigDecimal totalEsperado = new BigDecimal("200.00");
        assertEquals(totalEsperado, vd.getImporteTotalLinea());
    }

    @Test
    void getImporteTotalLineaDeberiaCalcularCorrectamenteConDescuento() {
        // Llevamos 2 productos de $100.00 cada uno = $200.00
        VentaDetalle vd = new VentaDetalle(1, 2, new BigDecimal("100.00"));

        // Le aplicamos un descuento total de $30.00 en esa linea
        vd.setDescuentoAplicado(new BigDecimal("30.00"));

        // 200 - 30 = 170
        BigDecimal totalEsperado = new BigDecimal("170.00");
        assertEquals(totalEsperado, vd.getImporteTotalLinea());
    }

    @Test
    void getImporteTotalLineaDeberiaDevolverCeroSiPrecioEsNulo() {
        VentaDetalle vd = new VentaDetalle();
        vd.setCantidad(5);
        // No le seteamos precio unitario, por lo que es null

        assertEquals(BigDecimal.ZERO, vd.getImporteTotalLinea());
    }
    @Test
    void igualMismoIdDeberiaSerIgual() {
        VentaDetalle vd1 = new VentaDetalle(); vd1.setIdDetalle(15);
        VentaDetalle vd2 = new VentaDetalle(); vd2.setIdDetalle(15);
        assertEquals(vd1, vd2);
    }

    @Test
    void diferenteIdNODeberiaSerIgual() {
        VentaDetalle vd1 = new VentaDetalle(); vd1.setIdDetalle(1);
        VentaDetalle vd2 = new VentaDetalle(); vd2.setIdDetalle(2);
        assertNotEquals(vd1, vd2);
    }

    @Test
    void mismoIdDeberianTenerMismoHashCode() {
        VentaDetalle vd1 = new VentaDetalle(); vd1.setIdDetalle(8);
        VentaDetalle vd2 = new VentaDetalle(); vd2.setIdDetalle(8);
        assertEquals(vd1.hashCode(), vd2.hashCode());
    }
}