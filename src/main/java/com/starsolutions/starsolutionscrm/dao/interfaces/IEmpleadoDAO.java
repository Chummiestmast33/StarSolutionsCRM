package com.starsolutions.starsolutionscrm.dao.interfaces;

import com.starsolutions.starsolutionscrm.model.rrhh.Empleado;
import java.sql.SQLException;
import java.util.List;

public interface IEmpleadoDAO {

    // Login — busca empleado por número y contraseña
    Empleado login(int num, String contrasena) throws SQLException;

    // Obtener todos los empleados activos
    List<Empleado> obtenerTodos() throws SQLException;

    // Obtener un empleado por su número
    Empleado obtenerPorNum(int num) throws SQLException;

    // Crear nuevo empleado (INSERT en rh_empleado + subtabla de tipo)
    boolean crear(Empleado empleado) throws SQLException;

    // Actualizar datos básicos (nombre, tipo, activo)
    boolean actualizar(Empleado empleado) throws SQLException;

    // Baja lógica — solo pone activo = 0
    boolean desactivar(int num) throws SQLException;

    boolean actualizarIndicadores(int num, double productividad, double eficiencia) throws SQLException;
}