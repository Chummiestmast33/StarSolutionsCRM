package com.starsolutions.starsolutionscrm.model.ventas;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Devolucion {
    private int idDevolucion;
    private int idVenta;
    private int idProducto;
    private int cantidad;
    private BigDecimal montoDevuelto;
    private String motivo;
    private LocalDateTime fecha;

    // Constructor vacio obligatorio
    public Devolucion() {
    }

    // Getters y Setters
    public int getIdDevolucion() { return idDevolucion; }
    public void setIdDevolucion(int idDevolucion) { this.idDevolucion = idDevolucion; }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getMontoDevuelto() { return montoDevuelto; }
    public void setMontoDevuelto(BigDecimal montoDevuelto) { this.montoDevuelto = montoDevuelto; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Devolucion that = (Devolucion) o;
        return idDevolucion == that.idDevolucion;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(idDevolucion);
    }

    @Override
    public String toString() {
        return "Devolucion{" +
                "idDevolucion=" + idDevolucion +
                ", motivo='" + motivo + '\'' +
                '}';
    }
}