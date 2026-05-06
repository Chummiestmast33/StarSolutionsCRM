package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.IOrdenCompraDAO;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.compras.DetalleOrdenCompra;
import com.starsolutions.starsolutionscrm.model.compras.OrdenCompra;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrdenCompraDAOImpl implements IOrdenCompraDAO {

	private Connection getConn() throws SQLException {
		return DatabaseConnection.getInstance().getConnection();
	}

	@Override
	public boolean alta(OrdenCompra ordenCompra) throws SQLException {
		if (ordenCompra == null || ordenCompra.getIdProveedor() == null || ordenCompra.getIdEmpleado() == null) {
			throw new SQLException("La orden de compra requiere proveedor y empleado");
		}

		Connection conn = getConn();
		boolean autoCommit = conn.getAutoCommit();
		conn.setAutoCommit(false);
		try {
			ordenCompra.calcularTotal();
			String sqlOrden = "INSERT INTO cmp_orden_compra (id_proveedor, id_empleado, fecha, estado, total) VALUES (?, ?, ?, ?, ?)";
			try (PreparedStatement ps = conn.prepareStatement(sqlOrden, Statement.RETURN_GENERATED_KEYS)) {
				ps.setInt(1, ordenCompra.getIdProveedor());
				ps.setInt(2, ordenCompra.getIdEmpleado());
				ps.setTimestamp(3, Timestamp.valueOf(ordenCompra.getFecha() != null ? ordenCompra.getFecha() : LocalDateTime.now()));
				ps.setString(4, ordenCompra.getEstado() != null ? ordenCompra.getEstado() : OrdenCompra.ESTADO_PENDIENTE);
				ps.setBigDecimal(5, ordenCompra.getTotal() != null ? ordenCompra.getTotal() : BigDecimal.ZERO);
				if (ps.executeUpdate() == 0) {
					throw new SQLException("No se pudo crear la orden de compra");
				}
				try (ResultSet keys = ps.getGeneratedKeys()) {
					if (keys.next()) {
						ordenCompra.setIdOrden(keys.getInt(1));
					}
				}
			}

			String sqlDetalle = "INSERT INTO cmp_detalle_orden (id_orden, id_producto, cantidad_pedida, cantidad_recibida, precio_unitario) VALUES (?, ?, ?, ?, ?)";
			try (PreparedStatement ps = conn.prepareStatement(sqlDetalle)) {
				for (DetalleOrdenCompra detalle : ordenCompra.getDetalles()) {
					detalle.setIdOrden(ordenCompra.getIdOrden());
					ps.setInt(1, ordenCompra.getIdOrden());
					ps.setInt(2, detalle.getIdProducto());
					ps.setInt(3, detalle.getCantidadPedida());
					ps.setInt(4, Math.max(detalle.getCantidadRecibida(), 0));
					ps.setBigDecimal(5, detalle.getPrecioUnitario());
					ps.addBatch();
				}
				ps.executeBatch();
			}

			conn.commit();
			return true;
		} catch (SQLException ex) {
			conn.rollback();
			throw ex;
		} finally {
			conn.setAutoCommit(autoCommit);
		}
	}

	@Override
	public List<OrdenCompra> listar() throws SQLException {
		String sql = "SELECT id_orden, id_proveedor, id_empleado, fecha, estado, total FROM cmp_orden_compra ORDER BY fecha DESC, id_orden DESC";
		List<OrdenCompra> lista = new ArrayList<>();
		try (PreparedStatement ps = getConn().prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				lista.add(mapearOrden(rs));
			}
		}
		return lista;
	}

	@Override
	public OrdenCompra obtenerPorId(int idOrden) throws SQLException {
		String sql = "SELECT id_orden, id_proveedor, id_empleado, fecha, estado, total FROM cmp_orden_compra WHERE id_orden = ?";
		try (PreparedStatement ps = getConn().prepareStatement(sql)) {
			ps.setInt(1, idOrden);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					OrdenCompra orden = mapearOrden(rs);
					orden.setDetalles(listarDetalles(idOrden));
					return orden;
				}
			}
		}
		return null;
	}

	@Override
	public List<DetalleOrdenCompra> listarDetalles(int idOrden) throws SQLException {
		String sql = "SELECT id_orden, id_producto, cantidad_pedida, cantidad_recibida, precio_unitario FROM cmp_detalle_orden WHERE id_orden = ? ORDER BY id_producto";
		List<DetalleOrdenCompra> lista = new ArrayList<>();
		try (PreparedStatement ps = getConn().prepareStatement(sql)) {
			ps.setInt(1, idOrden);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					lista.add(mapearDetalle(rs));
				}
			}
		}
		return lista;
	}

	@Override
	public boolean registrarRecepcion(int idOrden, Map<Integer, Integer> cantidadesRecibidas, Integer idEmpleadoInventario, String referencia) throws SQLException {
		if (cantidadesRecibidas == null || cantidadesRecibidas.isEmpty()) {
			throw new SQLException("Debes indicar al menos un producto recibido");
		}

		OrdenCompra orden = obtenerPorId(idOrden);
		if (orden == null) {
			throw new SQLException("La orden de compra no existe");
		}
		if (OrdenCompra.ESTADO_PAGADO.equalsIgnoreCase(orden.getEstado()) || OrdenCompra.ESTADO_CANCELADO.equalsIgnoreCase(orden.getEstado())) {
			throw new SQLException("La orden no admite recepcion en su estado actual");
		}

		Connection conn = getConn();
		boolean autoCommit = conn.getAutoCommit();
		conn.setAutoCommit(false);
		try {
			Map<Integer, DetalleOrdenCompra> detalles = cargarDetallesComoMapa(conn, idOrden);
			for (Map.Entry<Integer, Integer> entry : cantidadesRecibidas.entrySet()) {
				Integer idProducto = entry.getKey();
				int cantidad = entry.getValue() == null ? 0 : entry.getValue();
				if (cantidad <= 0) {
					throw new SQLException("La cantidad recibida debe ser mayor a cero");
				}

				DetalleOrdenCompra detalle = detalles.get(idProducto);
				if (detalle == null) {
					throw new SQLException("El producto " + idProducto + " no pertenece a la orden");
				}

				int pendiente = detalle.getCantidadPendiente();
				if (cantidad > pendiente) {
					throw new SQLException("La cantidad recibida supera el pendiente para el producto " + idProducto);
				}

				int idStock = obtenerODarAltaStockProducto(conn, idProducto);
				registrarMovimientoEntrada(conn, idStock, cantidad, referencia != null ? referencia : "OC-" + idOrden, idEmpleadoInventario);
				actualizarCantidadRecibida(conn, idOrden, idProducto, cantidad);
			}

			String estado = calcularEstadoRecepcion(conn, idOrden);
			if (!OrdenCompra.ESTADO_PAGADO.equalsIgnoreCase(orden.getEstado())) {
				actualizarEstado(conn, idOrden, estado);
			}

			conn.commit();
			return true;
		} catch (SQLException ex) {
			conn.rollback();
			throw ex;
		} finally {
			conn.setAutoCommit(autoCommit);
		}
	}

	@Override
	public boolean marcarPagada(int idOrden) throws SQLException {
		return actualizarEstado(idOrden, OrdenCompra.ESTADO_PAGADO);
	}

	@Override
	public boolean actualizarEstado(int idOrden, String estado) throws SQLException {
		String sql = "UPDATE cmp_orden_compra SET estado = ? WHERE id_orden = ?";
		try (PreparedStatement ps = getConn().prepareStatement(sql)) {
			ps.setString(1, estado);
			ps.setInt(2, idOrden);
			return ps.executeUpdate() > 0;
		}
	}

	private void actualizarEstado(Connection conn, int idOrden, String estado) throws SQLException {
		String sql = "UPDATE cmp_orden_compra SET estado = ? WHERE id_orden = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, estado);
			ps.setInt(2, idOrden);
			ps.executeUpdate();
		}
	}

	private OrdenCompra mapearOrden(ResultSet rs) throws SQLException {
		OrdenCompra orden = new OrdenCompra();
		orden.setIdOrden(rs.getInt("id_orden"));
		orden.setIdProveedor(rs.getInt("id_proveedor"));
		orden.setIdEmpleado(rs.getInt("id_empleado"));
		Timestamp ts = rs.getTimestamp("fecha");
		orden.setFecha(ts != null ? ts.toLocalDateTime() : null);
		orden.setEstado(rs.getString("estado"));
		orden.setTotal(rs.getBigDecimal("total"));
		return orden;
	}

	private DetalleOrdenCompra mapearDetalle(ResultSet rs) throws SQLException {
		DetalleOrdenCompra detalle = new DetalleOrdenCompra();
		detalle.setIdOrden(rs.getInt("id_orden"));
		detalle.setIdProducto(rs.getInt("id_producto"));
		detalle.setCantidadPedida(rs.getInt("cantidad_pedida"));
		detalle.setCantidadRecibida(rs.getInt("cantidad_recibida"));
		detalle.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
		return detalle;
	}

	private Map<Integer, DetalleOrdenCompra> cargarDetallesComoMapa(Connection conn, int idOrden) throws SQLException {
		String sql = "SELECT id_orden, id_producto, cantidad_pedida, cantidad_recibida, precio_unitario FROM cmp_detalle_orden WHERE id_orden = ?";
		Map<Integer, DetalleOrdenCompra> mapa = new LinkedHashMap<>();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, idOrden);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					DetalleOrdenCompra detalle = mapearDetalle(rs);
					mapa.put(detalle.getIdProducto(), detalle);
				}
			}
		}
		return mapa;
	}

	private int obtenerODarAltaStockProducto(Connection conn, int idProducto) throws SQLException {
		String buscar = "SELECT id_stock FROM inv_stock WHERE id_producto = ? ORDER BY id_stock LIMIT 1";
		try (PreparedStatement ps = conn.prepareStatement(buscar)) {
			ps.setInt(1, idProducto);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		}

		String insertar = "INSERT INTO inv_stock (id_producto, cantidad_actual, stock_minimo, stock_maximo, ubicacion) VALUES (?, 0, 0, NULL, NULL)";
		try (PreparedStatement ps = conn.prepareStatement(insertar, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, idProducto);
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				if (keys.next()) {
					return keys.getInt(1);
				}
			}
		}
		throw new SQLException("No se pudo obtener o crear el stock del producto " + idProducto);
	}

	private void registrarMovimientoEntrada(Connection conn, int idStock, int cantidad, String referencia, Integer idEmpleadoInventario) throws SQLException {
		String sql = "INSERT INTO inv_movimiento (id_stock, id_emp_inv, tipo, cantidad, fecha, referencia) VALUES (?, ?, 'ENTRADA', ?, CURRENT_DATE, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, idStock);
			if (idEmpleadoInventario != null) {
				ps.setInt(2, idEmpleadoInventario);
			} else {
				ps.setNull(2, java.sql.Types.INTEGER);
			}
			ps.setInt(3, cantidad);
			ps.setString(4, referencia);
			ps.executeUpdate();
		}
	}

	private void actualizarCantidadRecibida(Connection conn, int idOrden, int idProducto, int cantidad) throws SQLException {
		String sql = "UPDATE cmp_detalle_orden SET cantidad_recibida = cantidad_recibida + ? WHERE id_orden = ? AND id_producto = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, cantidad);
			ps.setInt(2, idOrden);
			ps.setInt(3, idProducto);
			ps.executeUpdate();
		}
	}

	private String calcularEstadoRecepcion(Connection conn, int idOrden) throws SQLException {
		String sql = "SELECT SUM(cantidad_pedida) AS pedida, SUM(cantidad_recibida) AS recibida, SUM(CASE WHEN cantidad_recibida > 0 THEN 1 ELSE 0 END) AS con_mov FROM cmp_detalle_orden WHERE id_orden = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, idOrden);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					int pedida = rs.getInt("pedida");
					int recibida = rs.getInt("recibida");
					int conMov = rs.getInt("con_mov");
					if (pedida > 0 && recibida >= pedida) {
						return OrdenCompra.ESTADO_RECIBIDO;
					}
					return conMov > 0 ? OrdenCompra.ESTADO_RECIBIDO_PARCIAL : OrdenCompra.ESTADO_PENDIENTE;
				}
			}
		}
		return OrdenCompra.ESTADO_PENDIENTE;
	}
}
