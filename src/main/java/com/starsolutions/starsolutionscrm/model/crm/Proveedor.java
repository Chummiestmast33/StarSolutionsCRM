package com.starsolutions.starsolutionscrm.model.crm;

public class Proveedor {
    private int idProveedor;
    private String nombre;
    private String rfc;
    private String direccion;
    private boolean activo;

    // Constructor vacio
    public Proveedor() {}

    // Getters y setters
    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() { return nombre; }
}