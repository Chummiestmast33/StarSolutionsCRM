package com.starsolutions.starsolutionscrm.model.ventas;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Promocion {
    private int idPromocion;
    private int idProducto;
    private String nombre;
    private BigDecimal porcentajeDesc;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activa;

    public Promocion() {}

    // Getters y Setters
    public int getIdPromocion() { return idPromocion; }
    public void setIdPromocion(int idPromocion) { this.idPromocion = idPromocion; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPorcentajeDesc() { return porcentajeDesc; }
    public void setPorcentajeDesc(BigDecimal porcentajeDesc) { this.porcentajeDesc = porcentajeDesc; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Promocion promocion = (Promocion) o;
        return idPromocion == promocion.idPromocion;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(idPromocion);
    }

    @Override
    public String toString() {
        return "Promocion{" +
                "idPromocion=" + idPromocion +
                ", nombre='" + nombre + '\'' +
                ", porcentajeDesc=" + porcentajeDesc +
                '}';
    }
}