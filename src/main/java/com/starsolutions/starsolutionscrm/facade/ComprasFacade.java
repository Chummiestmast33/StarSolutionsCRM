package com.starsolutions.starsolutionscrm.facade;

import com.starsolutions.starsolutionscrm.dao.impl.OrdenCompraDAOImpl;
import com.starsolutions.starsolutionscrm.dao.interfaces.IOrdenCompraDAO;
import com.starsolutions.starsolutionscrm.model.compras.DetalleOrdenCompra;
import com.starsolutions.starsolutionscrm.model.compras.OrdenCompra;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ComprasFacade {

	private final IOrdenCompraDAO ordenCompraDAO = new OrdenCompraDAOImpl();

	public boolean altaOrdenCompra(OrdenCompra ordenCompra) throws SQLException {
		return ordenCompraDAO.alta(ordenCompra);
	}

	public List<OrdenCompra> listarOrdenesCompra() throws SQLException {
		return ordenCompraDAO.listar();
	}

	public OrdenCompra obtenerOrdenCompra(int idOrden) throws SQLException {
		return ordenCompraDAO.obtenerPorId(idOrden);
	}

	public List<DetalleOrdenCompra> listarDetallesOrdenCompra(int idOrden) throws SQLException {
		return ordenCompraDAO.listarDetalles(idOrden);
	}

	public boolean registrarRecepcion(int idOrden, Map<Integer, Integer> cantidadesRecibidas, Integer idEmpleadoInventario, String referencia) throws SQLException {
		return ordenCompraDAO.registrarRecepcion(idOrden, cantidadesRecibidas, idEmpleadoInventario, referencia);
	}

	public boolean marcarPagada(int idOrden) throws SQLException {
		return ordenCompraDAO.marcarPagada(idOrden);
	}

	public boolean actualizarEstado(int idOrden, String estado) throws SQLException {
		return ordenCompraDAO.actualizarEstado(idOrden, estado);
	}
}
