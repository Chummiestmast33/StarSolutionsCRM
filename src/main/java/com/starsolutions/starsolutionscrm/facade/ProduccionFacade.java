package com.starsolutions.starsolutionscrm.facade;

import com.starsolutions.starsolutionscrm.dao.impl.OrdenProduccionDAOImpl;
import com.starsolutions.starsolutionscrm.dao.interfaces.IOrdenProduccionDAO;
import com.starsolutions.starsolutionscrm.model.produccion.OrdenProduccion;

import java.sql.SQLException;
import java.util.List;

public class ProduccionFacade {

    private final IOrdenProduccionDAO ordenProduccionDAO = new OrdenProduccionDAOImpl();

    // ----------------------------------------------------------------
    // CREAR ORDEN
    // ----------------------------------------------------------------
    public boolean crearOrden(OrdenProduccion orden) throws SQLException {
        // Validar que la cantidad planificada sea mayor a 0
        if (orden.getCantidadPlanificada() <= 0) {
            throw new IllegalArgumentException("La cantidad planificada debe ser mayor a 0.");
        }
        return ordenProduccionDAO.crear(orden);
    }

    // ----------------------------------------------------------------
    // COMPLETAR ORDEN
    // ----------------------------------------------------------------
    public boolean completarOrden(int idOrdenProd, int cantidadProducida) throws SQLException {
        // Validar que la cantidad producida sea mayor a 0
        if (cantidadProducida <= 0) {
            throw new IllegalArgumentException("La cantidad producida debe ser mayor a 0.");
        }

        // Verificar que la orden exista y esté En Proceso
        OrdenProduccion orden = ordenProduccionDAO.obtenerPorId(idOrdenProd);
        if (orden == null) {
            throw new IllegalArgumentException("No existe una orden con id: " + idOrdenProd);
        }
        if (!OrdenProduccion.ESTADO_EN_PROCESO.equals(orden.getEstado())) {
            throw new IllegalStateException("Solo se pueden completar órdenes En Proceso.");
        }

        return ordenProduccionDAO.completar(idOrdenProd, cantidadProducida);
    }

    // ----------------------------------------------------------------
    // CANCELAR ORDEN
    // ----------------------------------------------------------------
    public boolean cancelarOrden(int idOrdenProd) throws SQLException {
        // Verificar que la orden exista y esté En Proceso
        OrdenProduccion orden = ordenProduccionDAO.obtenerPorId(idOrdenProd);
        if (orden == null) {
            throw new IllegalArgumentException("No existe una orden con id: " + idOrdenProd);
        }
        if (!OrdenProduccion.ESTADO_EN_PROCESO.equals(orden.getEstado())) {
            throw new IllegalStateException("Solo se pueden cancelar órdenes En Proceso.");
        }

        return ordenProduccionDAO.cancelar(idOrdenProd);
    }

    // ----------------------------------------------------------------
    // CONSULTAS
    // ----------------------------------------------------------------
    public List<OrdenProduccion> obtenerTodasLasOrdenes() throws SQLException {
        return ordenProduccionDAO.obtenerTodas();
    }

    public List<OrdenProduccion> obtenerOrdenesPorEstado(String estado) throws SQLException {
        return ordenProduccionDAO.obtenerPorEstado(estado);
    }

    public List<OrdenProduccion> obtenerOrdenesEnProceso() throws SQLException {
        return ordenProduccionDAO.obtenerPorEstado(OrdenProduccion.ESTADO_EN_PROCESO);
    }

    public OrdenProduccion obtenerOrdenPorId(int idOrdenProd) throws SQLException {
        return ordenProduccionDAO.obtenerPorId(idOrdenProd);
    }
}