package com.starsolutions.starsolutionscrm.controller.inventario;

import com.starsolutions.starsolutionscrm.facade.InventarioFacade;
import com.starsolutions.starsolutionscrm.model.inventario.Stock;
import com.starsolutions.starsolutionscrm.util.AlertUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class StockConsultaController {

	@FXML private TextField txtIdProducto;
	@FXML private TableView<Stock> tablaStock;
	@FXML private TableColumn<Stock, Integer> colIdStock;
	@FXML private TableColumn<Stock, Integer> colProducto;
	@FXML private TableColumn<Stock, Integer> colCantidad;
	@FXML private TableColumn<Stock, Integer> colMinimo;
	@FXML private TableColumn<Stock, Integer> colMaximo;
	@FXML private TableColumn<Stock, String> colUbicacion;
	@FXML private Label lblEstado;

	private final InventarioFacade facade = new InventarioFacade();
	private final ObservableList<Stock> lista = FXCollections.observableArrayList();

	@FXML
	public void initialize() {
		colIdStock.setCellValueFactory(new PropertyValueFactory<>("idStock"));
		colProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
		colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadActual"));
		colMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
		colMaximo.setCellValueFactory(new PropertyValueFactory<>("stockMaximo"));
		colUbicacion.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));

		tablaStock.setItems(lista);
		tablaStock.setRowFactory(tv -> new TableRow<>() {
			@Override
			protected void updateItem(Stock item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setStyle("");
				} else if (item.getCantidadActual() <= item.getStockMinimo()) {
					setStyle("-fx-background-color: #ffebee;");
				} else {
					setStyle("");
				}
			}
		});
	}

	@FXML
	public void onConsultar() {
		String texto = txtIdProducto.getText() == null ? "" : txtIdProducto.getText().trim();
		if (texto.isEmpty()) {
			AlertUtil.error("Validación", "Ingresa el ID del producto.");
			return;
		}

		try {
			int idProducto = Integer.parseInt(texto);
			cargarStock(idProducto);
		} catch (NumberFormatException e) {
			AlertUtil.error("Validación", "El ID del producto debe ser numérico.");
		}
	}

	@FXML
	public void onRefrescar() {
		String texto = txtIdProducto.getText() == null ? "" : txtIdProducto.getText().trim();
		if (!texto.isEmpty()) {
			onConsultar();
		}
	}

	private void cargarStock(int idProducto) {
		try {
			lista.clear();
			lista.addAll(facade.consultarStock(idProducto));
			tablaStock.refresh();

			boolean alerta = lista.stream().anyMatch(s -> s.getCantidadActual() <= s.getStockMinimo());
			lblEstado.setText(alerta ? "ALERTA: stock bajo" : "Stock en rango normal");
			lblEstado.setStyle(alerta
					? "-fx-background-color: #c62828; -fx-text-fill: white; -fx-padding: 6 10 6 10; -fx-font-weight: bold;"
					: "-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-padding: 6 10 6 10; -fx-font-weight: bold;");
		} catch (SQLException e) {
			AlertUtil.error("Error", "No se pudo consultar el stock: " + e.getMessage());
		}
	}
}
