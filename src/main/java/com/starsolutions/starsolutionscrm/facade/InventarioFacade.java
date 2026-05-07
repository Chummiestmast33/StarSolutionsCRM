package com.starsolutions.starsolutionscrm.facade;

import com.starsolutions.starsolutionscrm.dao.impl.CategoriaDAOImpl;
import com.starsolutions.starsolutionscrm.dao.impl.MateriaPrimaDAOImpl;
import com.starsolutions.starsolutionscrm.dao.impl.ProductoDAOImpl;
import com.starsolutions.starsolutionscrm.dao.impl.StockDAOImpl;
import com.starsolutions.starsolutionscrm.dao.interfaces.ResultadoEliminacion;
import com.starsolutions.starsolutionscrm.model.inventario.CategoriaProducto;
import com.starsolutions.starsolutionscrm.model.inventario.MateriaPrima;
import com.starsolutions.starsolutionscrm.model.inventario.MovimientoInventario;
import com.starsolutions.starsolutionscrm.model.inventario.Producto;
import com.starsolutions.starsolutionscrm.model.inventario.Stock;

import java.sql.SQLException;
import java.util.List;

/**
 * Fachada simple para operaciones de inventario que delega a los DAOs.
 * Esta clase facilita el uso desde controladores/UI.
 */
public class InventarioFacade {

	private final CategoriaDAOImpl categoriaDAO = new CategoriaDAOImpl();
	private final ProductoDAOImpl productoDAO = new ProductoDAOImpl();
	private final StockDAOImpl stockDAO = new StockDAOImpl();
	private final MateriaPrimaDAOImpl materiaDAO = new MateriaPrimaDAOImpl();

	// ---------------------- Producto ---------------------------------
	public Producto buscarProducto(int idProducto) throws SQLException {
		return productoDAO.obtenerPorId(idProducto);
	}

	public List<Producto> buscarProductosPorNombre(String nombre) throws SQLException {
		return productoDAO.buscarPorNombre(nombre);
	}

	public List<Producto> buscarProductoPorNombre(String nombre) throws SQLException {
		return buscarProductosPorNombre(nombre);
	}

	public List<Producto> buscarProductosPorCategoria(int idCategoria) throws SQLException {
		return productoDAO.buscarPorCategoria(idCategoria);
	}

	public boolean altaProducto(Producto producto) throws SQLException {
		return productoDAO.alta(producto);
	}

	public boolean actualizarProducto(Producto producto) throws SQLException {
		return productoDAO.actualizar(producto);
	}

	public boolean desactivarProducto(int idProducto) throws SQLException {
		return productoDAO.desactivar(idProducto);
	}

	public ResultadoEliminacion eliminarProducto(int idProducto) throws SQLException {
		return productoDAO.eliminar(idProducto);
	}

	public Producto obtenerProductoPorId(int idProducto) throws SQLException {
		return productoDAO.obtenerPorId(idProducto);
	}

	public List<Producto> listarProductos() throws SQLException {
		return productoDAO.listarActivos();
	}

	public List<Producto> listarProductosActivos() throws SQLException {
		return productoDAO.listarActivos();
	}

	// ---------------------- Stock -----------------------------------
	public boolean verificarDisponibilidadPorUbicacion(int idProducto, String ubicacion, int cantidad)
			throws SQLException {
		return stockDAO.verificarDisponibilidadPorUbicacion(idProducto, ubicacion, cantidad);
	}

	public boolean verificarDisponibilidadTotalProducto(int idProducto, int cantidad) throws SQLException {
		return stockDAO.verificarDisponibilidadTotalProducto(idProducto, cantidad);
	}

	public int registrarMovimiento(MovimientoInventario movimiento) throws SQLException {
		return stockDAO.registrarMovimiento(movimiento);
	}

	public int ajustarInventario(MovimientoInventario movimiento) throws SQLException {
		return registrarMovimiento(movimiento);
	}

	public List<Stock> consultarStock(int idProducto) throws SQLException {
		return stockDAO.listarStockPorProducto(idProducto);
	}

	public Stock consultarStockPorUbicacion(int idProducto, String ubicacion) throws SQLException {
		return stockDAO.obtenerStockPorUbicacion(idProducto, ubicacion);
	}

	public Stock obtenerStockPorUbicacion(int idProducto, String ubicacion) throws SQLException {
		return stockDAO.obtenerStockPorUbicacion(idProducto, ubicacion);
	}

	public List<Stock> listarStockPorProducto(int idProducto) throws SQLException {
		return stockDAO.listarStockPorProducto(idProducto);
	}

	// ---------------------- Materia Prima ---------------------------
	public List<MateriaPrima> buscarMateriaPrimaPorNombre(String nombre) throws SQLException {
		return materiaDAO.buscarPorNombre(nombre);
	}

	public boolean altaMateriaPrima(MateriaPrima mp) throws SQLException {
		return materiaDAO.alta(mp);
	}

	public boolean desactivarMateriaPrima(int idMateria) throws SQLException {
		return materiaDAO.desactivar(idMateria);
	}

	public ResultadoEliminacion eliminarMateriaPrima(int idMateria) throws SQLException {
		return materiaDAO.eliminar(idMateria);
	}

	public MateriaPrima obtenerMateriaPrimaPorId(int idMateria) throws SQLException {
		return materiaDAO.obtenerPorId(idMateria);
	}

	public List<MateriaPrima> listarMateriasPrimasActivas() throws SQLException {
		return materiaDAO.listarActivas();
	}
	
	public List<CategoriaProducto> listarCategorias() throws SQLException {
		return categoriaDAO.listarTodas();
	}

	public boolean crearCategoria(CategoriaProducto c) throws SQLException {
		return categoriaDAO.crear(c);
	}

	public boolean actualizarCategoria(CategoriaProducto c) throws SQLException {
		return categoriaDAO.actualizar(c);
	}

	public boolean desactivarCategoria(int id) throws SQLException {
		return categoriaDAO.desactivar(id);
	}
}
