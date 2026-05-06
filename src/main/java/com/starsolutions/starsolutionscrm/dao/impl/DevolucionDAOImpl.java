package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.IDevolucionDAO;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.ventas.Devolucion;

import java.sql.*;

public class DevolucionDAOImpl implements IDevolucionDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // Metodo para insertar una devolucion en la base de datos
    @Override
    public boolean registrarDevolucion(Devolucion devolucion) throws SQLException {
        String sql = "INSERT INTO ven_devolucion (id_venta, id_producto, cantidad, monto_devuelto, motivo, fecha) " +
                "VALUES (?, ?, ?, ?, ?, NOW())";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, devolucion.getIdVenta());
            ps.setInt(2, devolucion.getIdProducto());
            ps.setInt(3, devolucion.getCantidad());
            ps.setBigDecimal(4, devolucion.getMontoDevuelto());
            ps.setString(5, devolucion.getMotivo());

            return ps.executeUpdate() > 0;
        }
    }
}