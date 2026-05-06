package com.starsolutions.starsolutionscrm.controller.compras;

import com.starsolutions.starsolutionscrm.dao.impl.ProveedorDAOImpl;
import com.starsolutions.starsolutionscrm.facade.ComprasFacade;
import com.starsolutions.starsolutionscrm.facade.InventarioFacade;
import com.starsolutions.starsolutionscrm.model.compras.DetalleOrdenCompra;
import com.starsolutions.starsolutionscrm.model.compras.OrdenCompra;
import com.starsolutions.starsolutionscrm.model.crm.Proveedor;
import com.starsolutions.starsolutionscrm.model.inventario.Producto;
import com.starsolutions.starsolutionscrm.util.AlertUtil;
import com.starsolutions.starsolutionscrm.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrdenCompraNuevaController {

	@FXML private ComboBox<Proveedor> cmbProveedor;
	@FXML private TextField txtIdProducto;
	@FXML private TextField txtCantidad;
	@FXML private TextField txtPrecio;
	@FXML private TableView<DetalleOrdenCompra> tablaDetalle;
	@FXML private TableColumn<DetalleOrdenCompra, Integer> colProducto;
	@FXML private TableColumn<DetalleOrdenCompra, Integer> colCantidad;
	@FXML private TableColumn<DetalleOrdenCompra, Integer> colRecibida;
	@FXML private TableColumn<DetalleOrdenCompra, BigDecimal> colPrecio;
	@FXML private TableColumn<DetalleOrdenCompra, BigDecimal> colSubtotal;
	@FXML private Label lblTotal;

	private final ComprasFacade comprasFacade = new ComprasFacade();
	private final InventarioFacade inventarioFacade = new InventarioFacade();
	private final ProveedorDAOImpl proveedorDAO = new ProveedorDAOImpl();
	private final ObservableList<DetalleOrdenCompra> detalles = FXCollections.observableArrayList();

	@FXML
	public void initialize() {
		colProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
		colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadPedida"));
		colRecibida.setCellValueFactory(new PropertyValueFactory<>("cantidadRecibida"));
		colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
		colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

		tablaDetalle.setItems(detalles);
		cargarProveedores();
		if (txtCantidad != null) {
			txtCantidad.setText("1");
		}
	}

	@FXML
	public void onAgregarDetalle() {
		try {
			int idProducto = Integer.parseInt(valor(txtIdProducto));
			int cantidad = Integer.parseInt(valor(txtCantidad));
			BigDecimal precio = new BigDecimal(valor(txtPrecio));

			if (cantidad <= 0) {
				AlertUtil.error("Validación", "La cantidad debe ser mayor a cero.");
				return;
			}

			Producto producto = inventarioFacade.buscarProducto(idProducto);
			if (producto == null || !producto.isActivo()) {
				AlertUtil.error("Validación", "El producto no existe o está inactivo.");
				return;
			}

			DetalleOrdenCompra existente = detalles.stream()
					.filter(d -> d.getIdProducto() != null && d.getIdProducto() == idProducto)
					.findFirst()
					.orElse(null);

			if (existente == null) {
				DetalleOrdenCompra detalle = new DetalleOrdenCompra();
				detalle.setIdProducto(idProducto);
				detalle.setCantidadPedida(cantidad);
				detalle.setPrecioUnitario(precio);
				detalles.add(detalle);
			} else {
				existente.setCantidadPedida(existente.getCantidadPedida() + cantidad);
				existente.setPrecioUnitario(precio);
			}

			refrescarTotales();
			txtIdProducto.clear();
			txtCantidad.setText("1");
			txtPrecio.clear();
		} catch (NumberFormatException e) {
			AlertUtil.error("Validación", "Producto, cantidad y precio deben ser numéricos.");
		} catch (Exception e) {
			AlertUtil.error("Error", "No se pudo agregar el detalle: " + e.getMessage());
		}
	}

	@FXML
	public void onQuitarDetalle() {
		DetalleOrdenCompra seleccionado = tablaDetalle.getSelectionModel().getSelectedItem();
		if (seleccionado != null) {
			detalles.remove(seleccionado);
			refrescarTotales();
		}
	}

	@FXML
	public void onGuardar() {
		try {
			if (cmbProveedor.getValue() == null) {
				AlertUtil.error("Validación", "Selecciona un proveedor.");
				return;
			}
			if (detalles.isEmpty()) {
				AlertUtil.error("Validación", "Agrega al menos un detalle.");
				return;
			}
			if (!SessionManager.getInstance().haySesionActiva()) {
				AlertUtil.error("Validación", "No hay sesión activa.");
				return;
			}

			OrdenCompra orden = new OrdenCompra();
			orden.setIdProveedor(cmbProveedor.getValue().getIdProveedor());
			orden.setIdEmpleado(SessionManager.getInstance().getEmpleadoActual().getNum());
			orden.setDetalles(new ArrayList<>(detalles));
			orden.calcularTotal();

			if (comprasFacade.altaOrdenCompra(orden)) {
				AlertUtil.info("Éxito", "Orden creada con folio #" + orden.getIdOrden());
				cerrar();
			}
		} catch (Exception e) {
			AlertUtil.error("Error", "No se pudo guardar la orden: " + e.getMessage());
		}
	}

	@FXML
	public void onCancelar() {
		cerrar();
	}

	private void cargarProveedores() {
		try {
			cmbProveedor.setItems(FXCollections.observableArrayList(proveedorDAO.listarActivos()));
		} catch (Exception e) {
			AlertUtil.error("Error", "No se pudieron cargar proveedores: " + e.getMessage());
		}
	}

	private void refrescarTotales() {
		BigDecimal total = BigDecimal.ZERO;
		for (DetalleOrdenCompra detalle : detalles) {
			total = total.add(detalle.getSubtotal());
		}
		lblTotal.setText(total.toPlainString());
		tablaDetalle.refresh();
	}

	private String valor(TextField field) {
		return field.getText() == null ? "" : field.getText().trim();
	}

	private void cerrar() {
		Stage stage = (Stage) lblTotal.getScene().getWindow();
		stage.close();
	}
}