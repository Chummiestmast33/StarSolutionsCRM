package com.starsolutions.starsolutionscrm.model.ventas;

import java.math.BigDecimal;

public class VentaDetalle {
    private int idDetalle;
    private int idVenta;
    private int idProducto;
    private Integer idPromocion; // Usamos Integer para permitir valores nulos (null)
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;

    public VentaDetalle() {}

    public VentaDetalle(int idProducto, int cantidad, BigDecimal precioUnitario) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getImporteTotalLinea() {
        if (precioUnitario == null) return BigDecimal.ZERO;
        BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
        return subtotal.subtract(descuentoAplicado != null ? descuentoAplicado : BigDecimal.ZERO);
    }

    // Getters y Setters
    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public Integer getIdPromocion() { return idPromocion; }
    public void setIdPromocion(Integer idPromocion) { this.idPromocion = idPromocion; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getDescuentoAplicado() { return descuentoAplicado; }
    public void setDescuentoAplicado(BigDecimal descuentoAplicado) { this.descuentoAplicado = descuentoAplicado; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VentaDetalle detalle = (VentaDetalle) o;
        return idDetalle == detalle.idDetalle;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(idDetalle);
    }

    @Override
    public String toString() {
        return "VentaDetalle{" +
                "idDetalle=" + idDetalle +
                ", idProducto=" + idProducto +
                ", cantidad=" + cantidad +
                '}';
    }
}