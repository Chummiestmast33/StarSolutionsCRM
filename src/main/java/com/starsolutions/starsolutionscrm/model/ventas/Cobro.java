package com.starsolutions.starsolutionscrm.model.ventas;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Cobro {
    private int idCobro;
    private int idVenta;
    private int idCliente;
    private BigDecimal monto;
    private LocalDate fecha;

    public Cobro() {}

    // Getters y Setters
    public int getIdCobro() { return idCobro; }
    public void setIdCobro(int idCobro) { this.idCobro = idCobro; }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cobro cobro = (Cobro) o;
        return idCobro == cobro.idCobro;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(idCobro);
    }

    @Override
    public String toString() {
        return "Cobro{" +
                "idCobro=" + idCobro +
                ", monto=" + monto +
                '}';
    }
}