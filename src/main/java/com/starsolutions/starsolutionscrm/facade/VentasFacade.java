package com.starsolutions.starsolutionscrm.facade;

import com.starsolutions.starsolutionscrm.dao.impl.PromocionDAOImpl;
import com.starsolutions.starsolutionscrm.dao.impl.VentaDAOImpl;
import com.starsolutions.starsolutionscrm.dao.interfaces.IPromocionDAO;
import com.starsolutions.starsolutionscrm.dao.interfaces.IVentaDAO;
import com.starsolutions.starsolutionscrm.model.ventas.Venta;
import com.starsolutions.starsolutionscrm.model.ventas.VentaDetalle;

import java.math.BigDecimal;
import java.util.List;

public class VentasFacade {

    // Inicializar DAOs igual que tus companeros
    private final IVentaDAO ventaDAO = new VentaDAOImpl();
    private final IPromocionDAO promoDAO = new PromocionDAOImpl();

    public int procesarNuevaVenta(Venta venta, List<VentaDetalle> lineas) throws Exception {

        for (VentaDetalle linea : lineas) {

            // Usamos la interfaz del pendejo de PotatoStar lo del descuento
            BigDecimal porcentajeDesc = promoDAO.buscarDescuentoAplicable(venta.getIdCliente(), linea.getIdProducto());

            BigDecimal precioTotalLinea = linea.getPrecioUnitario().multiply(new BigDecimal(linea.getCantidad()));
            BigDecimal montoDescuento = BigDecimal.ZERO;

            // Convertir porcentaje a dinero
            if (porcentajeDesc.compareTo(BigDecimal.ZERO) > 0) {
                montoDescuento = precioTotalLinea.multiply(porcentajeDesc).divide(new BigDecimal("100"));
            }

            linea.setDescuentoAplicado(montoDescuento);
        }

        // El trigger hara las matematicas finales en la BD
        return ventaDAO.registrarVentaCompleta(venta, lineas);
    }
}