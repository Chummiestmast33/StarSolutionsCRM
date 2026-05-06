package com.starsolutions.starsolutionscrm.dao.interfaces;

import com.starsolutions.starsolutionscrm.model.ventas.Cobro;
import com.starsolutions.starsolutionscrm.model.ventas.Venta;
import com.starsolutions.starsolutionscrm.model.ventas.VentaDetalle;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface IVentaDAO {
    // Metodo para insertar la venta y sus productos
    int crearVentaConDetalles(Venta venta, List<VentaDetalle> detalles) throws SQLException;

    // Metodo para consultar el saldo pendiente
    BigDecimal obtenerSaldoPendiente(int idVenta) throws SQLException;

    // Metodo para registrar un abono
    boolean registrarCobro(Cobro cobro) throws SQLException;

    // Metodo para actualizar el estatus de la venta
    boolean actualizarEstatus(int idVenta, String estatus) throws SQLException;

    // NUEVO: Metodo para obtener todo el historial
    List<Venta> listarVentas() throws SQLException;
}