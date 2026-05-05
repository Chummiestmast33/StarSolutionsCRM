package com.starsolutions.starsolutionscrm.model.crm;

public class Cliente {
    private int idCliente;
    private String nombre;
    private String direccion;
    private String rfc;
    private boolean activo;

    // Constructor vacio
    public Cliente() {}

    // Constructor basico
    public Cliente(String nombre, String direccion, String rfc) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.rfc = rfc;
        this.activo = true;
    }

    // Getters y setters
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() { return nombre; }
}