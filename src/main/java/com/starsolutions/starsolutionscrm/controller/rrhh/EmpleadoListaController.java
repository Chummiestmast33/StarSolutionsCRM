package com.starsolutions.starsolutionscrm.controller.rrhh;

import com.starsolutions.starsolutionscrm.facade.RRHHFacade;
import com.starsolutions.starsolutionscrm.model.rrhh.Empleado;
import com.starsolutions.starsolutionscrm.util.AlertUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class EmpleadoListaController {

    @FXML private TableView<Empleado>            tablaEmpleados;
    @FXML private TableColumn<Empleado, Integer> colNum;
    @FXML private TableColumn<Empleado, String>  colNombre;
    @FXML private TableColumn<Empleado, String>  colTipo;
    @FXML private TableColumn<Empleado, Boolean> colActivo;

    private final RRHHFacade facade = new RRHHFacade();
    private final ObservableList<Empleado> lista = FXCollections.observableArrayList();

    private static final String BASE_FXML =
            "/com/starsolutions/starsolutionscrm/fxml/rrhh/";

    // ----------------------------------------------------------------
    // INICIALIZAR
    // ----------------------------------------------------------------
    @FXML
    public void initialize() {
        colNum.setCellValueFactory(new PropertyValueFactory<>("num"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoEmpleado"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        tablaEmpleados.setItems(lista);
        cargarEmpleados();
    }

    private void cargarEmpleados() {
        try {
            lista.setAll(facade.obtenerEmpleados());
        } catch (SQLException e) {
            AlertUtil.error("Error", "No se pudieron cargar los empleados: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // NUEVO EMPLEADO
    // ----------------------------------------------------------------
    @FXML
    public void onNuevo() {
        abrirFormulario(null);
    }

    // ----------------------------------------------------------------
    // EDITAR EMPLEADO
    // ----------------------------------------------------------------
    @FXML
    public void onEditar() {
        Empleado seleccionado = tablaEmpleados.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertUtil.info("Aviso", "Selecciona un empleado para editar.");
            return;
        }
        abrirFormulario(seleccionado);
    }

    // ----------------------------------------------------------------
    // DESACTIVAR EMPLEADO
    // ----------------------------------------------------------------
    @FXML
    public void onDesactivar() {
        Empleado seleccionado = tablaEmpleados.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertUtil.info("Aviso", "Selecciona un empleado para desactivar.");
            return;
        }

        boolean confirmar = AlertUtil.confirmar(
                "Confirmar",
                "¿Desactivar al empleado " + seleccionado.getNombre() + "?"
        );
        if (!confirmar) return;

        try {
            boolean ok = facade.desactivarEmpleado(seleccionado.getNum());
            if (ok) {
                AlertUtil.info("Éxito", "Empleado desactivado correctamente.");
                cargarEmpleados();
            } else {
                AlertUtil.error("Error", "No se pudo desactivar el empleado.");
            }
        } catch (SQLException e) {
            AlertUtil.error("Error", "Error al desactivar: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // REFRESCAR
    // ----------------------------------------------------------------
    @FXML
    public void onRefrescar() {
        cargarEmpleados();
    }

    // ----------------------------------------------------------------
    // MÉTODO PRIVADO — abrir formulario
    // ----------------------------------------------------------------
    private void abrirFormulario(Empleado empleado) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(BASE_FXML + "empleado-form.fxml")
            );
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(empleado == null ? "Nuevo Empleado" : "Editar Empleado");
            stage.initModality(Modality.APPLICATION_MODAL);

            // Pasar el empleado al formulario si es edición
            EmpleadoFormController controller = loader.getController();
            controller.setEmpleado(empleado);
            controller.setOnGuardado(this::cargarEmpleados);

            stage.showAndWait();
        } catch (IOException e) {
            AlertUtil.error("Error", "No se pudo abrir el formulario.");
        }
    }
}