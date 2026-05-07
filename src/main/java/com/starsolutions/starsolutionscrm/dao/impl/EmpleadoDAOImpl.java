package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.IEmpleadoDAO;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.rrhh.Empleado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAOImpl implements IEmpleadoDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ----------------------------------------------------------------
    // LOGIN
    // ----------------------------------------------------------------
    @Override
    public Empleado login(int num, String contrasena) throws SQLException {
        String sql = "SELECT num, nombre, contrasena, productividad, eficiencia, " +
                "tipo_empleado, activo " +
                "FROM rh_empleado " +
                "WHERE num = ? AND contrasena = ? AND activo = 1";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, num);
            ps.setString(2, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null; // null = credenciales incorrectas
    }

    // ----------------------------------------------------------------
    // OBTENER TODOS
    // ----------------------------------------------------------------
    @Override
    public List<Empleado> obtenerTodos() throws SQLException {
        String sql = "SELECT num, nombre, contrasena, productividad, eficiencia, " +
                "tipo_empleado, activo " +
                "FROM rh_empleado " +
                "WHERE activo = 1 " +
                "ORDER BY nombre";

        List<Empleado> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // ----------------------------------------------------------------
    // OBTENER POR NÚMERO
    // ----------------------------------------------------------------
    @Override
    public Empleado obtenerPorNum(int num) throws SQLException {
        String sql = "SELECT num, nombre, contrasena, productividad, eficiencia, " +
                "tipo_empleado, activo " +
                "FROM rh_empleado " +
                "WHERE num = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, num);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    // ----------------------------------------------------------------
    // CREAR
    // ----------------------------------------------------------------
    @Override
    public boolean crear(Empleado empleado) throws SQLException {
        // INSERT principal en rh_empleado
        String sqlEmp = "INSERT INTO rh_empleado (nombre, contrasena, tipo_empleado) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement ps = getConn().prepareStatement(
                sqlEmp, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getContrasena());
            ps.setString(3, empleado.getTipoEmpleado());

            int filas = ps.executeUpdate();
            if (filas == 0)
                return false;

            // Obtener el num generado
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int numGenerado = keys.getInt(1);
                    empleado.setNum(numGenerado);
                    insertarEnSubtabla(numGenerado, empleado.getTipoEmpleado());
                }
            }
        }
        return true;
    }

    // ----------------------------------------------------------------
    // ACTUALIZAR
    // ----------------------------------------------------------------
    @Override
    public boolean actualizar(Empleado empleado) throws SQLException {
        String sql = "UPDATE rh_empleado " +
                "SET nombre = ?, tipo_empleado = ? " +
                "WHERE num = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getTipoEmpleado());
            ps.setInt(3, empleado.getNum());

            return ps.executeUpdate() > 0;
        }
    }

    // ----------------------------------------------------------------
    // DESACTIVAR (baja lógica)
    // ----------------------------------------------------------------
    @Override
    public boolean desactivar(int num) throws SQLException {
        String sql = "UPDATE rh_empleado SET activo = 0 WHERE num = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, num);
            return ps.executeUpdate() > 0;
        }
    }

    // ----------------------------------------------------------------
    // MÉTODOS PRIVADOS DE APOYO
    // ----------------------------------------------------------------

    // Convierte una fila del ResultSet en un objeto Empleado
    private Empleado mapear(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setNum(rs.getInt("num"));
        e.setNombre(rs.getString("nombre"));
        e.setContrasena(rs.getString("contrasena"));
        e.setProductividad(rs.getDouble("productividad"));
        e.setEficiencia(rs.getDouble("eficiencia"));
        e.setTipoEmpleado(rs.getString("tipo_empleado"));
        e.setActivo(rs.getBoolean("activo"));
        return e;
    }

    // Inserta en la subtabla correcta según el tipo de empleado
    private void insertarEnSubtabla(int num, String tipo) throws SQLException {
        String tabla = switch (tipo) {
            case "Ventas" -> "rh_empleado_ventas";
            case "RH" -> "rh_empleado_rh";
            case "Inventario" -> "rh_empleado_inventario";
            case "Produccion" -> "rh_empleado_produccion";
            default -> throw new SQLException("Tipo de empleado no reconocido: " + tipo);
        };

        String sql = "INSERT INTO " + tabla + " (num) VALUES (?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, num);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean actualizarIndicadores(int num, double productividad, double eficiencia) throws SQLException {
        String sql = "UPDATE rh_empleado SET productividad = ?, eficiencia = ? WHERE num = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDouble(1, productividad);
            ps.setDouble(2, eficiencia);
            ps.setInt(3, num);
            return ps.executeUpdate() > 0;
        }
    }

}