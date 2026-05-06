package com.starsolutions.starsolutionscrm.dao.interfaces;

import com.starsolutions.starsolutionscrm.model.ventas.Promocion;
import java.sql.SQLException;
import java.util.List;

public interface IPromocionDAO {
    List<Promocion> listarActivas() throws SQLException;
    boolean crear(Promocion promocion) throws SQLException;
    boolean desactivar(int idPromocion) throws SQLException;
}

