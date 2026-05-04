package com.starsolutions.starsolutionscrm.model.rrhh;

import java.time.LocalDate;
import java.time.LocalTime;

public class Asistencia {

    private int       idAsistencia;
    private int       idEmpleado;
    private LocalDate fecha;
    private LocalTime horaEntrada;  // NULL hasta que se registra entrada
    private LocalTime horaSalida;   // NULL hasta que se registra salida

    // Constructor vacío
    public Asistencia() {}

    // Constructor completo
    public Asistencia(int idAsistencia, int idEmpleado,
                      LocalDate fecha,
                      LocalTime horaEntrada, LocalTime horaSalida) {
        this.idAsistencia = idAsistencia;
        this.idEmpleado   = idEmpleado;
        this.fecha        = fecha;
        this.horaEntrada  = horaEntrada;
        this.horaSalida   = horaSalida;
    }

    // Getters
    public int       getIdAsistencia() { return idAsistencia; }
    public int       getIdEmpleado()   { return idEmpleado; }
    public LocalDate getFecha()        { return fecha; }
    public LocalTime getHoraEntrada()  { return horaEntrada; }
    public LocalTime getHoraSalida()   { return horaSalida; }

    // Setters
    public void setIdAsistencia(int idAsistencia)     { this.idAsistencia = idAsistencia; }
    public void setIdEmpleado(int idEmpleado)         { this.idEmpleado   = idEmpleado; }
    public void setFecha(LocalDate fecha)             { this.fecha        = fecha; }
    public void setHoraEntrada(LocalTime horaEntrada) { this.horaEntrada  = horaEntrada; }
    public void setHoraSalida(LocalTime horaSalida)   { this.horaSalida   = horaSalida; }

    @Override
    public String toString() {
        return "Asistencia{id=" + idAsistencia +
                ", empleado=" + idEmpleado +
                ", fecha=" + fecha +
                ", entrada=" + horaEntrada +
                ", salida=" + horaSalida + "}";
    }
}