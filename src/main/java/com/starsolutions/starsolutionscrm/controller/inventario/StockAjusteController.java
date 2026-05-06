package com.starsolutions.starsolutionscrm.controller.inventario;

import com.starsolutions.starsolutionscrm.facade.InventarioFacade;
import com.starsolutions.starsolutionscrm.model.inventario.MovimientoInventario;
import com.starsolutions.starsolutionscrm.util.AlertUtil;
import com.starsolutions.starsolutionscrm.util.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class StockAjusteController {

	@FXML private TextField txtIdProducto;
	@FXML private TextField txtUbicacion;
	@FXML private TextField txtCantidad;
	@FXML private TextField txtReferencia;
	@FXML private ComboBox<String> cmbTipo;

	private final InventarioFacade facade = new InventarioFacade();

	@FXML
	public void initialize() {
		if (cmbTipo != null) {
			cmbTipo.getItems().setAll("ENTRADA", "SALIDA");
			cmbTipo.getSelectionModel().selectFirst();
		}
	}

	@FXML
	public void onGuardar() {
		try {
			int idProducto = Integer.parseInt(valor(txtIdProducto));
			int cantidad = Integer.parseInt(valor(txtCantidad));
			String tipo = cmbTipo.getValue();

			if (cantidad <= 0) {
				AlertUtil.error("Validación", "La cantidad debe ser mayor a cero.");
				return;
			}
			if (tipo == null || tipo.isBlank()) {
				AlertUtil.error("Validación", "Selecciona el tipo de movimiento.");
				return;
			}

			MovimientoInventario movimiento = new MovimientoInventario();
			movimiento.setIdStock(obtenerIdStock(idProducto, valor(txtUbicacion)));
			movimiento.setIdEmpleadoInventario(SessionManager.getInstance().haySesionActiva()
					? SessionManager.getInstance().getEmpleadoActual().getNum()
					: null);
			movimiento.setTipo(tipo);
			movimiento.setCantidad(cantidad);
			movimiento.setFecha(LocalDate.now());
			movimiento.setReferencia(valor(txtReferencia));

			int idMovimiento = facade.ajustarInventario(movimiento);
			if (idMovimiento > 0) {
				AlertUtil.info("Éxito", "Movimiento registrado con folio #" + idMovimiento);
				limpiar();
			}
		} catch (NumberFormatException e) {
			AlertUtil.error("Validación", "Producto y cantidad deben ser numéricos.");
		} catch (Exception e) {
			AlertUtil.error("Error", "No se pudo registrar el ajuste: " + e.getMessage());
		}
	}

	@FXML
	public void onCancelar() {
		limpiar();
	}

	private int obtenerIdStock(int idProducto, String ubicacion) throws Exception {
		if (ubicacion != null && !ubicacion.isBlank()) {
			var stock = facade.consultarStockPorUbicacion(idProducto, ubicacion);
			if (stock != null) {
				return stock.getIdStock();
			}
		}

		var stocks = facade.consultarStock(idProducto);
		if (!stocks.isEmpty()) {
			return stocks.get(0).getIdStock();
		}

		throw new IllegalArgumentException("No existe stock para el producto indicado");
	}

	private String valor(TextField field) {
		return field.getText() == null ? "" : field.getText().trim();
	}

	private void limpiar() {
		if (txtIdProducto != null) txtIdProducto.clear();
		if (txtUbicacion != null) txtUbicacion.clear();
		if (txtCantidad != null) txtCantidad.clear();
		if (txtReferencia != null) txtReferencia.clear();
	}
}