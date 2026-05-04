package com.starsolutions.starsolutionscrm.util;

import com.starsolutions.starsolutionscrm.model.rrhh.Empleado;

public class SessionManager {

    private static SessionManager instance;
    private Empleado empleadoActual;

    // Constructor privado — nadie puede hacer "new SessionManager()"
    private SessionManager() {}

    // Única forma de obtener la instancia
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Guardar el empleado al hacer login
    public void iniciarSesion(Empleado empleado) {
        this.empleadoActual = empleado;
    }

    // Borrar la sesión al cerrar sesión
    public void cerrarSesion() {
        this.empleadoActual = null;
    }

    // Obtener el empleado logueado
    public Empleado getEmpleadoActual() {
        return empleadoActual;
    }

    // Verificar si hay alguien logueado
    public boolean haySesionActiva() {
        return empleadoActual != null;
    }

    // Consultar el tipo del empleado logueado (útil para controlar acceso)
    public String getTipoEmpleado() {
        if (empleadoActual == null) return null;
        return empleadoActual.getTipoEmpleado();
    }
}