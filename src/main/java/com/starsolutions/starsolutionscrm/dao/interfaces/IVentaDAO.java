package com.starsolutions.starsolutionscrm.dao.interfaces;

import com.starsolutions.starsolutionscrm.model.ventas.Venta;
import com.starsolutions.starsolutionscrm.model.ventas.VentaDetalle;
import java.sql.SQLException;
import java.util.List;

public interface IVentaDAO {
    int registrarVentaCompleta(Venta venta, List<VentaDetalle> detalles) throws SQLException;
}