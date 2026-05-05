package com.starsolutions.starsolutionscrm.dao.interfaces;

import com.starsolutions.starsolutionscrm.model.inventario.MovimientoInventario;
import com.starsolutions.starsolutionscrm.model.inventario.Stock;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO para operaciones sobre Stock (tabla inv_stock) y Movimientos (tabla inv_movimiento).
 * Manejoa disponibilidad mixta: principal por ubicación (operativa de almacén),
 * secundaria por total de producto (vista global).
 */
public interface IStockDAO {

    /**
     * Verifica disponibilidad de un producto en una ubicación específica.
     * Búsqueda principal para operaciones de almacén.
     * @param idProducto ID del producto
     * @param ubicacion Ubicación (se normaliza: trim, case-insensitive)
     * @param cantidadRequerida Cantidad a validar
     * @return true si cantidad_actual >= cantidadRequerida, false si no hay suficiente o no existe
     */
    boolean verificarDisponibilidadPorUbicacion(int idProducto, String ubicacion, int cantidadRequerida) throws SQLException;

    /**
     * Verifica disponibilidad total de un producto (suma de todas las ubicaciones).
     * Consulta global/complementaria.
     * @param idProducto ID del producto
     * @param cantidadRequerida Cantidad a validar
     * @return true si SUM(cantidad_actual) >= cantidadRequerida, false si no hay suficiente
     */
    boolean verificarDisponibilidadTotalProducto(int idProducto, int cantidadRequerida) throws SQLException;

    /**
     * Registra un movimiento de inventario (entrada o salida) en inv_movimiento.
     * Para salidas (SALIDA): valida disponibilidad previa y genera erro si insuficiente.
     * Para entradas (ENTRADA): inserción directa sin validación previa.
     * @param movimiento Objeto MovimientoInventario con tipo, cantidad, idStock, etc.
     * @return int: ID del movimiento generado si fue exitoso, -1 si falló
     */
    int registrarMovimiento(MovimientoInventario movimiento) throws SQLException;

    /**
     * Obtiene el stock de un producto en una ubicación específica.
     * @param idProducto ID del producto
     * @param ubicacion Ubicación del stock
     * @return Stock si existe, null si no hay registro exacto
     */
    Stock obtenerStockPorUbicacion(int idProducto, String ubicacion) throws SQLException;

    /**
     * Lista todos los registros de stock de un producto (todas las ubicaciones).
     * @param idProducto ID del producto
     * @return Lista de Stock; vacía si no hay registros
     */
    List<Stock> listarStockPorProducto(int idProducto) throws SQLException;
}
