package com.starsolutions.starsolutionscrm.controller.produccion;

import com.starsolutions.starsolutionscrm.facade.ProduccionFacade;
import com.starsolutions.starsolutionscrm.model.produccion.OrdenProduccion;
import com.starsolutions.starsolutionscrm.util.AlertUtil;
import com.starsolutions.starsolutionscrm.util.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class OrdenProduccionNuevaController {

    @FXML private TextField  txtIdProducto;
    @FXML private TextField  txtCantidadPlanificada;
    @FXML private DatePicker dpFechaEstimada;

    private final ProduccionFacade facade = new ProduccionFacade();
    private Runnable onGuardado = null;

    // ----------------------------------------------------------------
    // CALLBACK
    // ----------------------------------------------------------------
    public void setOnGuardado(Runnable onGuardado) {
        this.onGuardado = onGuardado;
    }

    // ----------------------------------------------------------------
    // GUARDAR
    // ----------------------------------------------------------------
    @FXML
    public void onGuardar() {
        // Validar campos obligatorios
        String txtProducto  = txtIdProducto.getText().trim();
        String txtCantidad  = txtCantidadPlanificada.getText().trim();

        if (txtProducto.isEmpty()) {
            AlertUtil.error("Error", "El ID de producto no puede estar vacío.");
            return;
        }
        if (txtCantidad.isEmpty()) {
            AlertUtil.error("Error", "La cantidad planificada no puede estar vacía.");
            return;
        }

        // Validar que sean números enteros
        int idProducto;
        int cantidadPlanificada;
        try {
            idProducto = Integer.parseInt(txtProducto);
        } catch (NumberFormatException e) {
            AlertUtil.error("Error", "El ID de producto debe ser un número entero.");
            return;
        }
        try {
            cantidadPlanificada = Integer.parseInt(txtCantidad);
        } catch (NumberFormatException e) {
            AlertUtil.error("Error", "La cantidad planificada debe ser un número entero.");
            return;
        }
        if (cantidadPlanificada <= 0) {
            AlertUtil.error("Error", "La cantidad planificada debe ser mayor a 0.");
            return;
        }

        // Obtener el empleado logueado desde la sesión
        int idEmpleado = SessionManager.getInstance().getEmpleadoActual().getNum();

        // Construir la orden
        OrdenProduccion orden = new OrdenProduccion();
        orden.setIdEmpleado(idEmpleado);
        orden.setIdProductoFinal(idProducto);
        orden.setCantidadPlanificada(cantidadPlanificada);
        orden.setFechaEstimadaFin(dpFechaEstimada.getValue()); // puede ser null

        try {
            boolean ok = facade.crearOrden(orden);
            if (ok) {
                AlertUtil.info("Éxito", "Orden creada con ID: " + orden.getIdOrdenProd());
                if (onGuardado != null) onGuardado.run();
                cerrarVentana();
            } else {
                AlertUtil.error("Error", "No se pudo crear la orden.");
            }
        } catch (SQLException e) {
            AlertUtil.error("Error de base de datos", e.getMessage());
        } catch (IllegalArgumentException e) {
            AlertUtil.error("Error de validación", e.getMessage());
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
        Stage stage = (Stage) txtIdProducto.getScene().getWindow();
        stage.close();
    }
}