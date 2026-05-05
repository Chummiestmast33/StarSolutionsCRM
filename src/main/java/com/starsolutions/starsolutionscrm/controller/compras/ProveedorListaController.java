package com.starsolutions.starsolutionscrm.controller.compras;

import com.starsolutions.starsolutionscrm.dao.impl.ProveedorDAOImpl;
import com.starsolutions.starsolutionscrm.dao.interfaces.IProveedorDAO;
import com.starsolutions.starsolutionscrm.model.crm.Proveedor;
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

public class ProveedorListaController {

    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, Integer> colId;
    @FXML private TableColumn<Proveedor, String> colNombre;
    @FXML private TableColumn<Proveedor, String> colRfc;
    @FXML private TableColumn<Proveedor, String> colDireccion;
    @FXML private TableColumn<Proveedor, Boolean> colActivo;

    private final IProveedorDAO proveedorDAO = new ProveedorDAOImpl();
    private final ObservableList<Proveedor> lista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colRfc.setCellValueFactory(new PropertyValueFactory<>("rfc"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        tablaProveedores.setItems(lista);
        cargarProveedores();
    }

    // Cargar desde BD y refrescar tabla
    private void cargarProveedores() {
        try {
            lista.clear();
            lista.addAll(proveedorDAO.listarActivos());
            tablaProveedores.refresh();
        } catch (SQLException e) {
            AlertUtil.error("Error", "No se pudieron cargar los proveedores: " + e.getMessage());
        }
    }

    @FXML
    public void onNuevo() {
        abrirFormulario(null);
    }

    @FXML
    public void onEditar() {
        Proveedor seleccionado = tablaProveedores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertUtil.info("Aviso", "Selecciona un proveedor para editar");
            return;
        }
        abrirFormulario(seleccionado);
    }

    @FXML
    public void onDesactivar() {
        Proveedor seleccionado = tablaProveedores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertUtil.info("Aviso", "Selecciona un proveedor para desactivar");
            return;
        }

        boolean confirmar = AlertUtil.confirmar("Confirmar", "¿Desactivar proveedor " + seleccionado.getNombre() + "?");
        if (confirmar) {
            try {
                boolean ok = proveedorDAO.desactivar(seleccionado.getIdProveedor());
                if (ok) {
                    AlertUtil.info("Exito", "Proveedor desactivado");
                    cargarProveedores();
                }
            } catch (SQLException e) {
                AlertUtil.error("Error", "Error al desactivar: " + e.getMessage());
            }
        }
    }

    @FXML
    public void onRefrescar() {
        cargarProveedores();
    }

    private void abrirFormulario(Proveedor proveedor) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/starsolutions/starsolutionscrm/fxml/crm/proveedor-form.fxml")
            );
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(proveedor == null ? "Nuevo Proveedor" : "Editar Proveedor");
            stage.initModality(Modality.APPLICATION_MODAL);

            // Inyectar datos al controlador del formulario
            ProveedorFormController controller = loader.getController();
            controller.setProveedor(proveedor);
            controller.setOnGuardado(this::cargarProveedores);

            stage.showAndWait();
        } catch (IOException e) {
            AlertUtil.error("Error", "No se pudo abrir el formulario");
        }
    }
}