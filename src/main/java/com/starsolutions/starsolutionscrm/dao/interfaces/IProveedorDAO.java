package com.starsolutions.starsolutionscrm.dao.interfaces;

import com.starsolutions.starsolutionscrm.model.crm.Proveedor;
import java.sql.SQLException;
import java.util.List;

public interface IProveedorDAO {
    List<Proveedor> listarActivos() throws SQLException;
    Proveedor buscarPorId(int idProveedor) throws SQLException;
    boolean crear(Proveedor proveedor) throws SQLException;
    boolean actualizar(Proveedor proveedor) throws SQLException;
    boolean desactivar(int idProveedor) throws SQLException;
}