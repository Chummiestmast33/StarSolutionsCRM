package com.starsolutions.starsolutionscrm.controller.compras;

import com.starsolutions.starsolutionscrm.facade.ComprasFacade;
import com.starsolutions.starsolutionscrm.model.compras.OrdenCompra;
import com.starsolutions.starsolutionscrm.util.AlertUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class OrdenCompraListaController {

	@FXML private TableView<OrdenCompra> tablaOrdenes;
	@FXML private TableColumn<OrdenCompra, Integer> colId;
	@FXML private TableColumn<OrdenCompra, Integer> colProveedor;
	@FXML private TableColumn<OrdenCompra, Integer> colEmpleado;
	@FXML private TableColumn<OrdenCompra, java.time.LocalDateTime> colFecha;
	@FXML private TableColumn<OrdenCompra, String> colEstado;
	@FXML private TableColumn<OrdenCompra, java.math.BigDecimal> colTotal;

	private final ComprasFacade facade = new ComprasFacade();
	private final ObservableList<OrdenCompra> lista = FXCollections.observableArrayList();

	@FXML
	public void initialize() {
		colId.setCellValueFactory(new PropertyValueFactory<>("idOrden"));
		colProveedor.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
		colEmpleado.setCellValueFactory(new PropertyValueFactory<>("idEmpleado"));
		colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
		colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
		colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

		tablaOrdenes.setItems(lista);
		cargarOrdenes();
	}

	@FXML
	public void onNueva() {
		abrirFormularioNueva();
	}

	@FXML
	public void onRecepcion() {
		OrdenCompra seleccionada = tablaOrdenes.getSelectionModel().getSelectedItem();
		if (seleccionada == null) {
			AlertUtil.info("Aviso", "Selecciona una orden para recibir.");
			return;
		}
		abrirRecepcion(seleccionada);
	}

	@FXML
	public void onPagado() {
		OrdenCompra seleccionada = tablaOrdenes.getSelectionModel().getSelectedItem();
		if (seleccionada == null) {
			AlertUtil.info("Aviso", "Selecciona una orden para marcar como pagada.");
			return;
		}
		try {
			if (facade.marcarPagada(seleccionada.getIdOrden())) {
				AlertUtil.info("Éxito", "Orden marcada como pagada.");
				cargarOrdenes();
			}
		} catch (SQLException e) {
			AlertUtil.error("Error", "No se pudo actualizar el estado: " + e.getMessage());
		}
	}

	@FXML
	public void onRefrescar() {
		cargarOrdenes();
	}

	private void cargarOrdenes() {
		try {
			lista.clear();
			lista.addAll(facade.listarOrdenesCompra());
			tablaOrdenes.refresh();
		} catch (SQLException e) {
			AlertUtil.error("Error", "No se pudieron cargar las órdenes: " + e.getMessage());
		}
	}

	private void abrirFormularioNueva() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/starsolutions/starsolutionscrm/fxml/cpm/orden-compra-nueva.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(loader.load()));
			stage.setTitle("Nueva Orden de Compra");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
			cargarOrdenes();
		} catch (IOException e) {
			AlertUtil.error("Error", "No se pudo abrir el formulario de orden.");
		}
	}

	private void abrirRecepcion(OrdenCompra orden) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/starsolutions/starsolutionscrm/fxml/cpm/orden-compra-recepcion.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(loader.load()));
			stage.setTitle("Recepción de Orden #" + orden.getIdOrden());
			stage.initModality(Modality.APPLICATION_MODAL);

			OrdenCompraRecepcionController controller = loader.getController();
			controller.setOrdenCompra(orden);
			controller.setOnGuardado(this::cargarOrdenes);

			stage.showAndWait();
		} catch (IOException e) {
			AlertUtil.error("Error", "No se pudo abrir la recepción de orden.");
		}
	}
}