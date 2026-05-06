package com.starsolutions.starsolutionscrm.dao.interfaces;

import com.starsolutions.starsolutionscrm.model.ventas.Devolucion;
import java.sql.SQLException;

public interface IDevolucionDAO {
    boolean registrarDevolucion(Devolucion devolucion) throws SQLException;
}