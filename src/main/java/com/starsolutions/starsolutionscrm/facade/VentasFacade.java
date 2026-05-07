package com.starsolutions.starsolutionscrm.facade;

import com.starsolutions.starsolutionscrm.dao.impl.ClienteDescuentoDAOImpl;
import com.starsolutions.starsolutionscrm.dao.impl.PromocionDAOImpl;
import com.starsolutions.starsolutionscrm.dao.impl.VentaDAOImpl;
import com.starsolutions.starsolutionscrm.model.crm.ClienteDescuento;
import com.starsolutions.starsolutionscrm.model.inventario.MovimientoInventario;
import com.starsolutions.starsolutionscrm.model.inventario.Producto;
import com.starsolutions.starsolutionscrm.model.inventario.Stock;
import com.starsolutions.starsolutionscrm.model.ventas.Cobro;
import com.starsolutions.starsolutionscrm.model.ventas.Promocion;
import com.starsolutions.starsolutionscrm.model.ventas.Venta;
import com.starsolutions.starsolutionscrm.model.ventas.VentaDetalle;

import java.math.BigDecimal;
import java.util.List;

public class VentasFacade {

    private final VentaDAOImpl ventaDAO = new VentaDAOImpl();
    private final PromocionDAOImpl promocionDAO = new PromocionDAOImpl();
    private final ClienteDescuentoDAOImpl clienteDescuentoDAO = new ClienteDescuentoDAOImpl();
    private final InventarioFacade inventario = new InventarioFacade();

    // Metodo para buscar productos desde el punto de venta

    public List<Venta> listarVentasCreditoActivasPendientes() throws Exception {
        return ventaDAO.listarVentasCreditoPendientes();
    }

    public List<Venta> listarVentasParaDevolucion() throws Exception {
        return ventaDAO.listarVentasActivasOLiquidadas();
    }

    public Producto buscarProducto(int idProducto) throws Exception {
        return inventario.obtenerProductoPorId(idProducto);
    }

    // El mejor descuento (Promocion vs VIP)
    public void calcularTicket(int idCliente, List<VentaDetalle> detalles, Venta ventaCalculada) throws Exception {
        // Obtenemos el descuento VIP del cliente si existe
        ClienteDescuento cd = clienteDescuentoDAO.obtenerPorCliente(idCliente);
        BigDecimal descVIP = (cd != null) ? cd.getDescuento() : BigDecimal.ZERO;

        BigDecimal subtotalGlobal = BigDecimal.ZERO;
        BigDecimal descuentoGlobal = BigDecimal.ZERO;

        // Recorremos cada producto del carrito
        for (VentaDetalle det : detalles) {
            BigDecimal subtotalLinea = det.getPrecioUnitario().multiply(new BigDecimal(det.getCantidad()));
            subtotalGlobal = subtotalGlobal.add(subtotalLinea);

            // Buscamos si el articulo especifico tiene oferta vigente hoy
            Promocion promo = promocionDAO.obtenerPromocionActivaPorProducto(det.getIdProducto());
            BigDecimal porcentajePromo = (promo != null) ? promo.getPorcentajeDesc() : BigDecimal.ZERO;

            BigDecimal descuentoLinea = BigDecimal.ZERO;

            // EVALUACIÓN: ¿Cuál descuento es mayor?
            if (porcentajePromo.compareTo(BigDecimal.ZERO) > 0 && porcentajePromo.compareTo(descVIP) >= 0) {
                // Gana la promocion del articulo (es mayor o igual al VIP)
                descuentoLinea = subtotalLinea.multiply(porcentajePromo).divide(new BigDecimal("100"));
                det.setIdPromocion(promo.getIdPromocion());

            } else if (descVIP.compareTo(BigDecimal.ZERO) > 0) {
                // Gana el descuento VIP del cliente (es mayor que la promocion)
                descuentoLinea = subtotalLinea.multiply(descVIP).divide(new BigDecimal("100"));
                det.setIdPromocion(null); // No se ata a un ID de promocion especifica

            } else {
                // Sin descuentos
                det.setIdPromocion(null);
            }

            // Asignamos el descuento calculado a la linea actual
            det.setDescuentoAplicado(descuentoLinea);
            descuentoGlobal = descuentoGlobal.add(descuentoLinea);
        }

        // Asignamos los montos consolidados al objeto Venta maestro
        ventaCalculada.setSubtotal(subtotalGlobal);
        ventaCalculada.setDescuentoAplicado(descuentoGlobal);
        ventaCalculada.setTotal(subtotalGlobal.subtract(descuentoGlobal));
    }

    // =========================================================================
    // Registro y Procesamiento de la Venta
    // =========================================================================
    public int procesarNuevaVenta(Venta venta, List<VentaDetalle> detalles) throws Exception {

        // 1. Validar que exista inventario suficiente antes de proceder
        for (VentaDetalle det : detalles) {
            boolean hayStock = inventario.verificarDisponibilidadTotalProducto(det.getIdProducto(), det.getCantidad());
            if (!hayStock) {
                throw new Exception("Stock insuficiente para el producto ID: " + det.getIdProducto());
            }
        }

        // 2. Aplicar la logica de prioridades y calcular el ticket final
        calcularTicket(venta.getIdCliente(), detalles, venta);

        // 3. Asignar el estatus segun el metodo de pago seleccionado
        venta.setEstatus("Contado".equals(venta.getCondicionPago()) ? "Liquidada" : "Activa");

        // 4. Guardar en la base de datos de manera transaccional
        int idVenta = ventaDAO.crearVentaConDetalles(venta, detalles);

        // 5. Generar movimientos de salida para descontar inventario fisico
        for (VentaDetalle det : detalles) {
            List<Stock> listaStock = inventario.listarStockPorProducto(det.getIdProducto());
            if (!listaStock.isEmpty()) {
                Stock stockPrincipal = listaStock.get(0);

                MovimientoInventario mov = new MovimientoInventario();
                mov.setIdStock(stockPrincipal.getIdStock());
                mov.setTipo("SALIDA");
                mov.setCantidad(det.getCantidad());
                mov.setReferencia("Venta ID: " + idVenta);
                // Nota: No se asigna el ID del empleado para evitar conflicto de FK con RH

                inventario.registrarMovimiento(mov);
            }
        }

        return idVenta;
    }

    // =========================================================================
    // Modulo de Cobranza
    // =========================================================================
    public void registrarCobroParcial(Cobro cobro) throws Exception {
        BigDecimal saldoPendiente = ventaDAO.obtenerSaldoPendiente(cobro.getIdVenta());

        if (cobro.getMonto().compareTo(saldoPendiente) > 0) {
            throw new Exception("El abono supera el saldo pendiente ($" + saldoPendiente + ")");
        }

        // Guardar el abono
        ventaDAO.registrarCobro(cobro);

        // Si la deuda llega a cero, actualizar el ticket a Liquidada
        if (cobro.getMonto().compareTo(saldoPendiente) == 0) {
            ventaDAO.actualizarEstatus(cobro.getIdVenta(), "Liquidada");
        }
    }
}


