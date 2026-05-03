package com.starsolutions.starsolutionscrm.model.rrhh;

import java.time.LocalDate;

public class Nomina {

    private int       idNomina;
    private int       idEmpleado;
    private double    salarioBase;
    private double    deducciones;
    private double    neto;        // GENERATED en BD — solo lectura, nunca insertar
    private LocalDate periodo;

    // Constructor vacío
    public Nomina() {}

    // Constructor completo (útil al mapear desde ResultSet)
    public Nomina(int idNomina, int idEmpleado,
                  double salarioBase, double deducciones,
                  double neto, LocalDate periodo) {
        this.idNomina    = idNomina;
        this.idEmpleado  = idEmpleado;
        this.salarioBase = salarioBase;
        this.deducciones = deducciones;
        this.neto        = neto;
        this.periodo     = periodo;
    }

    // Getters
    public int       getIdNomina()    { return idNomina; }
    public int       getIdEmpleado()  { return idEmpleado; }
    public double    getSalarioBase() { return salarioBase; }
    public double    getDeducciones() { return deducciones; }
    public double    getNeto()        { return neto; }
    public LocalDate getPeriodo()     { return periodo; }

    // Setters
    public void setIdNomina(int idNomina)       { this.idNomina    = idNomina; }
    public void setIdEmpleado(int idEmpleado)   { this.idEmpleado  = idEmpleado; }
    public void setSalarioBase(double v)        { this.salarioBase = v; }
    public void setDeducciones(double v)        { this.deducciones = v; }
    public void setNeto(double neto)            { this.neto        = neto; } // solo para mapear RS
    public void setPeriodo(LocalDate periodo)   { this.periodo     = periodo; }

    @Override
    public String toString() {
        return "Nomina{id=" + idNomina +
                ", empleado=" + idEmpleado +
                ", base=" + salarioBase +
                ", deducciones=" + deducciones +
                ", neto=" + neto +
                ", periodo=" + periodo + "}";
    }
}