package com.starsolutions.starsolutionscrm.model.ventas;

import java.math.BigDecimal;

public class VentaDetalle {
    private int idDetalle;
    private int idVenta;
    private int idProducto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoAplicado;

    // Constructor vacio
    public VentaDetalle() {
        this.precioUnitario = BigDecimal.ZERO;
        this.descuentoAplicado = BigDecimal.ZERO;
    }

    // Constructor para agregar linea desde la interfaz grafica
    public VentaDetalle(int idProducto, int cantidad, BigDecimal precioUnitario) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descuentoAplicado = BigDecimal.ZERO;
    }

    // Getters y setters
    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }
    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getDescuentoAplicado() { return descuentoAplicado; }
    public void setDescuentoAplicado(BigDecimal descuentoAplicado) { this.descuentoAplicado = descuentoAplicado; }

    public BigDecimal getImporteTotalLinea() {
        if (precioUnitario == null) return BigDecimal.ZERO;

        BigDecimal totalSinDescuento = precioUnitario.multiply(new BigDecimal(cantidad));

        if (descuentoAplicado != null) {
            return totalSinDescuento.subtract(descuentoAplicado);
        }

        return totalSinDescuento;
    }
}