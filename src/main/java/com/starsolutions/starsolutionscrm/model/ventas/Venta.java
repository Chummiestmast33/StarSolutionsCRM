package com.starsolutions.starsolutionscrm.model.ventas;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Venta {
    private int idVenta;
    private int idCliente;
    private int idEmpleado;
    private BigDecimal subtotal;
    private BigDecimal descuentoAplicado;
    private BigDecimal total;
    private LocalDate fecha;
    private String estatus;
    private String condicionPago;

    // Constructor vacio inicializando montos
    public Venta() {
        this.subtotal = BigDecimal.ZERO;
        this.descuentoAplicado = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
    }

    // Getters y setters
    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getDescuentoAplicado() { return descuentoAplicado; }
    public void setDescuentoAplicado(BigDecimal descuentoAplicado) { this.descuentoAplicado = descuentoAplicado; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    public String getCondicionPago() { return condicionPago; }
    public void setCondicionPago(String condicionPago) { this.condicionPago = condicionPago; }
}