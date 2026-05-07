package com.starsolutions.starsolutionscrm.dao.interfaces;

import com.starsolutions.starsolutionscrm.model.inventario.CategoriaProducto;
import java.sql.SQLException;
import java.util.List;

public interface ICategoriaDAO {
    List<CategoriaProducto> listarTodas() throws SQLException;
    boolean crear(CategoriaProducto categoria) throws SQLException;
    boolean actualizar(CategoriaProducto categoria) throws SQLException;
    boolean desactivar(int idCategoria) throws SQLException;
}