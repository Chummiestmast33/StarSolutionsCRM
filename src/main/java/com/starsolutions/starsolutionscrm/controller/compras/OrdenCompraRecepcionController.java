package com.starsolutions.starsolutionscrm.controller.compras;

import com.starsolutions.starsolutionscrm.facade.ComprasFacade;
import com.starsolutions.starsolutionscrm.model.compras.DetalleOrdenCompra;
import com.starsolutions.starsolutionscrm.model.compras.OrdenCompra;
import com.starsolutions.starsolutionscrm.util.AlertUtil;
import com.starsolutions.starsolutionscrm.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class OrdenCompraRecepcionController {

	@FXML private Label lblOrden;
	@FXML private Label lblEstado;
	@FXML private TableView<DetalleOrdenCompra> tablaDetalle;
	@FXML private TableColumn<DetalleOrdenCompra, Integer> colProducto;
	@FXML private TableColumn<DetalleOrdenCompra, Integer> colPedida;
	@FXML private TableColumn<DetalleOrdenCompra, Integer> colRecibida;
	@FXML private TableColumn<DetalleOrdenCompra, Integer> colPendiente;
	@FXML private TableColumn<DetalleOrdenCompra, BigDecimal> colPrecio;
	@FXML private TextField txtCantidadRecibida;
	@FXML private TextField txtReferencia;

	private final ComprasFacade facade = new ComprasFacade();
	private final ObservableList<DetalleOrdenCompra> detalles = FXCollections.observableArrayList();
	private OrdenCompra ordenCompra;
	private Runnable onGuardado;

	@FXML
	public void initialize() {
		colProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
		colPedida.setCellValueFactory(new PropertyValueFactory<>("cantidadPedida"));
		colRecibida.setCellValueFactory(new PropertyValueFactory<>("cantidadRecibida"));
		colPendiente.setCellValueFactory(new PropertyValueFactory<>("cantidadPendiente"));
		colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));

		tablaDetalle.setItems(detalles);
	}

	public void setOrdenCompra(OrdenCompra ordenCompra) {
		this.ordenCompra = ordenCompra;
		if (ordenCompra != null) {
			try {
				OrdenCompra completa = facade.obtenerOrdenCompra(ordenCompra.getIdOrden());
				if (completa != null) {
					this.ordenCompra = completa;
					detalles.setAll(completa.getDetalles());
					lblOrden.setText("Orden #" + completa.getIdOrden() + " - Proveedor " + completa.getIdProveedor());
					lblEstado.setText("Estado: " + completa.getEstado());
				}
			} catch (Exception e) {
				AlertUtil.error("Error", "No se pudo cargar la orden: " + e.getMessage());
			}
		}
	}

	public void setOnGuardado(Runnable onGuardado) {
		this.onGuardado = onGuardado;
	}

	@FXML
	public void onRegistrarRecepcion() {
		DetalleOrdenCompra seleccionado = tablaDetalle.getSelectionModel().getSelectedItem();
		if (seleccionado == null) {
			AlertUtil.info("Aviso", "Selecciona una línea para recibir.");
			return;
		}

		try {
			int cantidad = Integer.parseInt(valor(txtCantidadRecibida));
			if (cantidad <= 0) {
				AlertUtil.error("Validación", "La cantidad recibida debe ser mayor a cero.");
				return;
			}

			Map<Integer, Integer> mapa = new HashMap<>();
			mapa.put(seleccionado.getIdProducto(), cantidad);

			Integer idEmpleado = SessionManager.getInstance().haySesionActiva()
					? SessionManager.getInstance().getEmpleadoActual().getNum()
					: null;
			String referencia = valor(txtReferencia);

			if (facade.registrarRecepcion(ordenCompra.getIdOrden(), mapa, idEmpleado, referencia)) {
				AlertUtil.info("Éxito", "Recepción registrada.");
				setOrdenCompra(ordenCompra);
				if (onGuardado != null) {
					onGuardado.run();
				}
			}
		} catch (NumberFormatException e) {
			AlertUtil.error("Validación", "La cantidad recibida debe ser numérica.");
		} catch (Exception e) {
			AlertUtil.error("Error", "No se pudo registrar la recepción: " + e.getMessage());
		}
	}

	@FXML
	public void onMarcarPagada() {
		try {
			if (facade.marcarPagada(ordenCompra.getIdOrden())) {
				AlertUtil.info("Éxito", "Orden marcada como pagada.");
				setOrdenCompra(ordenCompra);
				if (onGuardado != null) {
					onGuardado.run();
				}
			}
		} catch (Exception e) {
			AlertUtil.error("Error", "No se pudo marcar como pagada: " + e.getMessage());
		}
	}

	@FXML
	public void onCerrar() {
		((javafx.stage.Stage) lblOrden.getScene().getWindow()).close();
	}

	private String valor(TextField field) {
		return field.getText() == null ? "" : field.getText().trim();
	}
}