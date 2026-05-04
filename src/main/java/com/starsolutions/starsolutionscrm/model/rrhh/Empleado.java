package com.starsolutions.starsolutionscrm.model.rrhh;

public class Empleado {

    private int num;
    private String nombre;
    private String contrasena;
    private double productividad;
    private double eficiencia;
    private String tipoEmpleado;   // "Ventas" | "RH" | "Inventario" | "Produccion"
    private boolean activo;

    // Constructor vacío (necesario para instanciar desde el DAO)
    public Empleado() {}

    // Constructor completo
    public Empleado(int num, String nombre, String contrasena,
                    double productividad, double eficiencia,
                    String tipoEmpleado, boolean activo) {
        this.num           = num;
        this.nombre        = nombre;
        this.contrasena    = contrasena;
        this.productividad = productividad;
        this.eficiencia    = eficiencia;
        this.tipoEmpleado  = tipoEmpleado;
        this.activo        = activo;
    }

    // Getters
    public int     getNum()           { return num; }
    public String  getNombre()        { return nombre; }
    public String  getContrasena()    { return contrasena; }
    public double  getProductividad() { return productividad; }
    public double  getEficiencia()    { return eficiencia; }
    public String  getTipoEmpleado()  { return tipoEmpleado; }
    public boolean isActivo()         { return activo; }

    // Setters
    public void setNum(int num)                     { this.num           = num; }
    public void setNombre(String nombre)            { this.nombre        = nombre; }
    public void setContrasena(String contrasena)    { this.contrasena    = contrasena; }
    public void setProductividad(double p)          { this.productividad = p; }
    public void setEficiencia(double e)             { this.eficiencia    = e; }
    public void setTipoEmpleado(String tipo)        { this.tipoEmpleado  = tipo; }
    public void setActivo(boolean activo)           { this.activo        = activo; }

    @Override
    public String toString() {
        return "Empleado{num=" + num + ", nombre='" + nombre +
                "', tipo='" + tipoEmpleado + "', activo=" + activo + "}";
    }
}