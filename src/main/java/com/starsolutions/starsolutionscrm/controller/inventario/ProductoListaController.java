package com.starsolutions.starsolutionscrm.controller.inventario;

import com.starsolutions.starsolutionscrm.facade.InventarioFacade;
import com.starsolutions.starsolutionscrm.model.inventario.Producto;
import com.starsolutions.starsolutionscrm.util.AlertUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ProductoListaController {

	@FXML private TextField txtBuscar;
	@FXML private TableView<Producto> tablaProductos;
	@FXML private TableColumn<Producto, Integer> colId;
	@FXML private TableColumn<Producto, String> colNombre;
	@FXML private TableColumn<Producto, String> colDescripcion;
	@FXML private TableColumn<Producto, java.math.BigDecimal> colPrecio;
	@FXML private TableColumn<Producto, Integer> colCategoria;
	@FXML private TableColumn<Producto, Boolean> colActivo;

	private final InventarioFacade facade = new InventarioFacade();
	private final ObservableList<Producto> lista = FXCollections.observableArrayList();

	@FXML
	public void initialize() {
		colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
		colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
		colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
		colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
		colCategoria.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));
		colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

		tablaProductos.setItems(lista);
		cargarProductos();
	}

	@FXML
	public void onRefrescar() {
		cargarProductos();
	}

	@FXML
	public void onBuscar() {
		String texto = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim();
		if (texto.isEmpty()) {
			cargarProductos();
			return;
		}

		try {
			lista.clear();
			lista.addAll(facade.buscarProductoPorNombre(texto));
			tablaProductos.refresh();
		} catch (SQLException e) {
			AlertUtil.error("Error", "No se pudieron buscar productos: " + e.getMessage());
		}
	}

	@FXML
	public void onNuevo() {
		abrirFormulario(null);
	}

	@FXML
	public void onEditar() {
		Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
		if (seleccionado == null) {
			AlertUtil.info("Aviso", "Selecciona un producto para editar.");
			return;
		}
		abrirFormulario(seleccionado);
	}

	@FXML
	public void onDesactivar() {
		Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
		if (seleccionado == null) {
			AlertUtil.info("Aviso", "Selecciona un producto para desactivar.");
			return;
		}

		if (!AlertUtil.confirmar("Confirmar", "¿Desactivar producto " + seleccionado.getNombre() + "?")) {
			return;
		}

		try {
			if (facade.desactivarProducto(seleccionado.getIdProducto())) {
				AlertUtil.info("Éxito", "Producto desactivado.");
				cargarProductos();
			}
		} catch (SQLException e) {
			AlertUtil.error("Error", "No se pudo desactivar el producto: " + e.getMessage());
		}
	}

	private void cargarProductos() {
		try {
			lista.clear();
			lista.addAll(facade.listarProductosActivos());
			tablaProductos.refresh();
		} catch (SQLException e) {
			AlertUtil.error("Error", "No se pudieron cargar los productos: " + e.getMessage());
		}
	}

	private void abrirFormulario(Producto producto) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/starsolutions/starsolutionscrm/fxml/inv/producto-form.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(loader.load()));
			stage.setTitle(producto == null ? "Nuevo Producto" : "Editar Producto");
			stage.initModality(Modality.APPLICATION_MODAL);

			ProductoFormController controller = loader.getController();
			controller.setProducto(producto);
			controller.setOnGuardado(this::cargarProductos);

			stage.showAndWait();
		} catch (IOException e) {
			AlertUtil.error("Error", "No se pudo abrir el formulario de producto.");
		}
	}
}
