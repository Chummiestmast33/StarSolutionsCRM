package com.starsolutions.starsolutionscrm.model.crm;

import java.math.BigDecimal;

public class ClienteDescuento {
    private int idDescuento;
    private int idCliente;
    private BigDecimal descuento;
    private String descripcion;
    private boolean activo;

    public ClienteDescuento() {}

    // Getters y Setters
    public int getIdDescuento() { return idDescuento; }
    public void setIdDescuento(int idDescuento) { this.idDescuento = idDescuento; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClienteDescuento that = (ClienteDescuento) o;
        return idDescuento == that.idDescuento;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(idDescuento);
    }

    @Override
    public String toString() {
        return "ClienteDescuento{" +
                "idDescuento=" + idDescuento +
                ", idCliente=" + idCliente +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}