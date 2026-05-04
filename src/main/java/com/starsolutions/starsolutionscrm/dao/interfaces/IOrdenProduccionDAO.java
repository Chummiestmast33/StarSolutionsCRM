package com.starsolutions.starsolutionscrm.dao.interfaces;

import com.starsolutions.starsolutionscrm.model.produccion.OrdenProduccion;

import java.sql.SQLException;
import java.util.List;

public interface IOrdenProduccionDAO {

    // Crear nueva orden (estado inicial = "En Proceso")
    boolean crear(OrdenProduccion orden) throws SQLException;

    // Completar una orden (estado = "Completada", registra fecha_real_fin y cantidad_producida)
    boolean completar(int idOrdenProd, int cantidadProducida) throws SQLException;

    // Cancelar una orden (estado = "Cancelada")
    boolean cancelar(int idOrdenProd) throws SQLException;

    // Obtener todas las órdenes
    List<OrdenProduccion> obtenerTodas() throws SQLException;

    // Obtener órdenes por estado (ej. solo "En Proceso")
    List<OrdenProduccion> obtenerPorEstado(String estado) throws SQLException;

    // Obtener una orden por id
    OrdenProduccion obtenerPorId(int idOrdenProd) throws SQLException;
}