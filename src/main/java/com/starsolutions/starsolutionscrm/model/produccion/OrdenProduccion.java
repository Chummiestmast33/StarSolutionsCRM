package com.starsolutions.starsolutionscrm.model.produccion;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class OrdenProduccion {

    // Estados válidos (según CHECK en BD)
    public static final String ESTADO_EN_PROCESO = "En Proceso";
    public static final String ESTADO_COMPLETADA = "Completada";
    public static final String ESTADO_CANCELADA  = "Cancelada";

    private int           idOrdenProd;
    private int           idEmpleado;          // FK → rh_empleado_produccion
    private int           idProductoFinal;     // FK → inv_producto (solo lectura)
    private int           cantidadPlanificada;
    private int           cantidadProducida;   // DEFAULT 0 en BD
    private LocalDateTime fechaInicio;         // DATETIME DEFAULT CURRENT_TIMESTAMP
    private LocalDate     fechaEstimadaFin;    // DATE NULL
    private LocalDateTime fechaRealFin;        // DATETIME NULL
    private String        estado;

    // Constructor vacío
    public OrdenProduccion() {}

    // Constructor completo
    public OrdenProduccion(int idOrdenProd, int idEmpleado, int idProductoFinal,
                           int cantidadPlanificada, int cantidadProducida,
                           LocalDateTime fechaInicio, LocalDate fechaEstimadaFin,
                           LocalDateTime fechaRealFin, String estado) {
        this.idOrdenProd          = idOrdenProd;
        this.idEmpleado           = idEmpleado;
        this.idProductoFinal      = idProductoFinal;
        this.cantidadPlanificada  = cantidadPlanificada;
        this.cantidadProducida    = cantidadProducida;
        this.fechaInicio          = fechaInicio;
        this.fechaEstimadaFin     = fechaEstimadaFin;
        this.fechaRealFin         = fechaRealFin;
        this.estado               = estado;
    }

    // Getters
    public int           getIdOrdenProd()         { return idOrdenProd; }
    public int           getIdEmpleado()           { return idEmpleado; }
    public int           getIdProductoFinal()      { return idProductoFinal; }
    public int           getCantidadPlanificada()  { return cantidadPlanificada; }
    public int           getCantidadProducida()    { return cantidadProducida; }
    public LocalDateTime getFechaInicio()          { return fechaInicio; }
    public LocalDate     getFechaEstimadaFin()     { return fechaEstimadaFin; }
    public LocalDateTime getFechaRealFin()         { return fechaRealFin; }
    public String        getEstado()               { return estado; }

    // Setters
    public void setIdOrdenProd(int idOrdenProd)                  { this.idOrdenProd         = idOrdenProd; }
    public void setIdEmpleado(int idEmpleado)                    { this.idEmpleado          = idEmpleado; }
    public void setIdProductoFinal(int idProductoFinal)          { this.idProductoFinal     = idProductoFinal; }
    public void setCantidadPlanificada(int cantidadPlanificada)  { this.cantidadPlanificada = cantidadPlanificada; }
    public void setCantidadProducida(int cantidadProducida)      { this.cantidadProducida   = cantidadProducida; }
    public void setFechaInicio(LocalDateTime fechaInicio)        { this.fechaInicio         = fechaInicio; }
    public void setFechaEstimadaFin(LocalDate fechaEstimadaFin)  { this.fechaEstimadaFin    = fechaEstimadaFin; }
    public void setFechaRealFin(LocalDateTime fechaRealFin)      { this.fechaRealFin        = fechaRealFin; }
    public void setEstado(String estado)                         { this.estado              = estado; }

    @Override
    public String toString() {
        return "OrdenProduccion{id=" + idOrdenProd +
                ", empleado=" + idEmpleado +
                ", producto=" + idProductoFinal +
                ", planificado=" + cantidadPlanificada +
                ", producido=" + cantidadProducida +
                ", estado='" + estado + "'}";
    }
}