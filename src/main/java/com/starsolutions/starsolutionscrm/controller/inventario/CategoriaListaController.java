package com.starsolutions.starsolutionscrm.controller.inventario;

import com.starsolutions.starsolutionscrm.facade.InventarioFacade;
import com.starsolutions.starsolutionscrm.model.inventario.CategoriaProducto;
import com.starsolutions.starsolutionscrm.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CategoriaListaController {

    @FXML private TableView<CategoriaProducto>            tablaCategoria;
    @FXML private TableColumn<CategoriaProducto, Integer> colId;
    @FXML private TableColumn<CategoriaProducto, String>  colNombre;
    @FXML private TableColumn<CategoriaProducto, String>  colDescripcion;
    @FXML private TableColumn<CategoriaProducto, Boolean> colActivo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;

    private final InventarioFacade facade = new InventarioFacade();
    private final ObservableList<CategoriaProducto> lista = FXCollections.observableArrayList();
    private CategoriaProducto seleccionada = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        tablaCategoria.setItems(lista);
        tablaCategoria.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, sel) -> seleccionada = sel);

        cargar();
    }

    private void cargar() {
        try {
            lista.setAll(facade.listarCategorias());
        } catch (Exception e) {
            AlertUtil.error("Error", e.getMessage());
        }
    }

    @FXML public void onNueva() {
        txtNombre.clear();
        txtDescripcion.clear();
        seleccionada = null;
        tablaCategoria.getSelectionModel().clearSelection();
    }

    @FXML public void onEditar() {
        if (seleccionada == null) { AlertUtil.error("Sin selección", "Selecciona una categoría."); return; }
        txtNombre.setText(seleccionada.getNombre());
        txtDescripcion.setText(seleccionada.getDescripcion());
    }

    @FXML public void onGuardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isBlank()) { AlertUtil.error("Error", "El nombre es obligatorio."); return; }

        try {
            if (seleccionada == null) {
                // ALTA
                CategoriaProducto nueva = new CategoriaProducto(null, nombre, txtDescripcion.getText().trim(), true);
                facade.crearCategoria(nueva);
                AlertUtil.info("Alta", "Categoría creada correctamente.");
            } else {
                // MODIFICACIÓN
                seleccionada.setNombre(nombre);
                seleccionada.setDescripcion(txtDescripcion.getText().trim());
                facade.actualizarCategoria(seleccionada);
                AlertUtil.info("Actualizado", "Categoría actualizada correctamente.");
            }
            seleccionada = null;
            txtNombre.clear();
            txtDescripcion.clear();
            cargar();
        } catch (Exception e) {
            AlertUtil.error("Error", e.getMessage());
        }
    }

    @FXML public void onDesactivar() {
        if (seleccionada == null) { AlertUtil.error("Sin selección", "Selecciona una categoría."); return; }
        try {
            facade.desactivarCategoria(seleccionada.getIdCategoria());
            AlertUtil.info("Desactivada", "Categoría desactivada.");
            cargar();
        } catch (Exception e) {
            AlertUtil.error("Error", e.getMessage());
        }
    }
}