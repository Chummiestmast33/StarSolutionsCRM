package com.starsolutions.starsolutionscrm.controller.rrhh;

import com.starsolutions.starsolutionscrm.facade.RRHHFacade;
import com.starsolutions.starsolutionscrm.model.rrhh.Empleado;
import com.starsolutions.starsolutionscrm.util.AlertUtil;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class EmpleadoFormController {

    @FXML private TextField  txtNombre;
    @FXML private TextField  txtContrasena;
    @FXML private ComboBox<String> cmbTipo;

    private final RRHHFacade facade = new RRHHFacade();
    private Empleado empleadoEditar = null;      // null = modo nuevo
    private Runnable onGuardado     = null;      // callback para refrescar la lista

    // ----------------------------------------------------------------
    // INICIALIZAR
    // ----------------------------------------------------------------
    @FXML
    public void initialize() {
        cmbTipo.setItems(FXCollections.observableArrayList(
                "Ventas", "RH", "Inventario", "Produccion"
        ));
    }

    // ----------------------------------------------------------------
    // MÉTODOS LLAMADOS DESDE EmpleadoListaController
    // ----------------------------------------------------------------

    // Si empleado != null → modo edición, prellenar campos
    public void setEmpleado(Empleado empleado) {
        this.empleadoEditar = empleado;

        if (empleado != null) {
            txtNombre.setText(empleado.getNombre());
            txtContrasena.setText(empleado.getContrasena());
            cmbTipo.setValue(empleado.getTipoEmpleado());

            // En edición no se puede cambiar el tipo (afecta subtablas)
            cmbTipo.setDisable(true);
            txtContrasena.setDisable(true);
        }
    }

    // Callback que se ejecuta tras guardar exitosamente
    public void setOnGuardado(Runnable onGuardado) {
        this.onGuardado = onGuardado;
    }

    // ----------------------------------------------------------------
    // GUARDAR
    // ----------------------------------------------------------------
    @FXML
    public void onGuardar() {
        // Validaciones
        String nombre     = txtNombre.getText().trim();
        String contrasena = txtContrasena.getText().trim();
        String tipo       = cmbTipo.getValue();

        if (nombre.isEmpty()) {
            AlertUtil.error("Error", "El nombre no puede estar vacío.");
            return;
        }
        if (empleadoEditar == null && contrasena.isEmpty()) {
            AlertUtil.error("Error", "La contraseña no puede estar vacía.");
            return;
        }
        if (empleadoEditar == null && tipo == null) {
            AlertUtil.error("Error", "Selecciona un tipo de empleado.");
            return;
        }

        try {
            if (empleadoEditar == null) {
                // MODO NUEVO
                Empleado nuevo = new Empleado();
                nuevo.setNombre(nombre);
                nuevo.setContrasena(contrasena);
                nuevo.setTipoEmpleado(tipo);

                boolean ok = facade.crearEmpleado(nuevo);
                if (ok) {
                    AlertUtil.info("Éxito", "Empleado creado con número: " + nuevo.getNum());
                } else {
                    AlertUtil.error("Error", "No se pudo crear el empleado.");
                    return;
                }
            } else {
                // MODO EDICIÓN
                empleadoEditar.setNombre(nombre);

                boolean ok = facade.actualizarEmpleado(empleadoEditar);
                if (ok) {
                    AlertUtil.info("Éxito", "Empleado actualizado correctamente.");
                } else {
                    AlertUtil.error("Error", "No se pudo actualizar el empleado.");
                    return;
                }
            }

            // Notificar a la lista para que se refresque
            if (onGuardado != null) onGuardado.run();

            cerrarVentana();

        } catch (SQLException e) {
            AlertUtil.error("Error de base de datos", e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // CANCELAR
    // ----------------------------------------------------------------
    @FXML
    public void onCancelar() {
        cerrarVentana();
    }

    // ----------------------------------------------------------------
    // MÉTODO PRIVADO
    // ----------------------------------------------------------------
    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
}