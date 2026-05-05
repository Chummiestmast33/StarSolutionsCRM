package com.starsolutions.starsolutionscrm.dao.interfaces;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface IPromocionDAO {
    // Retorna porcentaje de descuento
    BigDecimal buscarDescuentoAplicable(int idCliente, int idProducto) throws SQLException;
}