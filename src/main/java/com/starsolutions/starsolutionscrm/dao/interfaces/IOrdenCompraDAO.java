package com.starsolutions.starsolutionscrm.dao.interfaces;

import com.starsolutions.starsolutionscrm.model.compras.DetalleOrdenCompra;
import com.starsolutions.starsolutionscrm.model.compras.OrdenCompra;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IOrdenCompraDAO {

	boolean alta(OrdenCompra ordenCompra) throws SQLException;

	List<OrdenCompra> listar() throws SQLException;

	OrdenCompra obtenerPorId(int idOrden) throws SQLException;

	List<DetalleOrdenCompra> listarDetalles(int idOrden) throws SQLException;

	boolean registrarRecepcion(int idOrden, Map<Integer, Integer> cantidadesRecibidas, Integer idEmpleadoInventario, String referencia) throws SQLException;

	boolean marcarPagada(int idOrden) throws SQLException;

	boolean actualizarEstado(int idOrden, String estado) throws SQLException;
}
