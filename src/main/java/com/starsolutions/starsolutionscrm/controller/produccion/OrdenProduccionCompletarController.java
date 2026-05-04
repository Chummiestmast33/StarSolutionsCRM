package com.starsolutions.starsolutionscrm.controller.produccion;

import com.starsolutions.starsolutionscrm.facade.ProduccionFacade;
import com.starsolutions.starsolutionscrm.model.produccion.OrdenProduccion;
import com.starsolutions.starsolutionscrm.util.AlertUtil;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class OrdenProduccionCompletarController {

    @FXML private Label    lblIdOrden;
    @FXML private Label    lblProducto;
    @FXML private Label    lblPlanificado;
    @FXML private TextField txtCantidadProducida;

    private final ProduccionFacade facade = new ProduccionFacade();
    private OrdenProduccion ordenActual = null;
    private Runnable onGuardado = null;

    // ----------------------------------------------------------------
    // MÉTODOS LLAMADOS DESDE OrdenProduccionListaController
    // ----------------------------------------------------------------
    public void setOrden(OrdenProduccion orden) {
        this.ordenActual = orden;

        // Mostrar datos de la orden en los labels
        lblIdOrden.setText("Orden #" + orden.getIdOrdenProd());
        lblProducto.setText("Producto ID: " + orden.getIdProductoFinal());
        lblPlanificado.setText("Cantidad planificada: " + orden.getCantidadPlanificada());
    }

    public void setOnGuardado(Runnable onGuardado) {
        this.onGuardado = onGuardado;
    }

    // ----------------------------------------------------------------
    // COMPLETAR
    // ----------------------------------------------------------------
    @FXML
    public void onCompletar() {
        String txtCantidad = txtCantidadProducida.getText().trim();

        if (txtCantidad.isEmpty()) {
            AlertUtil.error("Error", "Ingresa la cantidad producida.");
            return;
        }

        int cantidadProducida;
        try {
            cantidadProducida = Integer.parseInt(txtCantidad);
        } catch (NumberFormatException e) {
            AlertUtil.error("Error", "La cantidad producida debe ser un número entero.");
            return;
        }
        if (cantidadProducida <= 0) {
            AlertUtil.error("Error", "La cantidad producida debe ser mayor a 0.");
            return;
        }

        // Advertir si la cantidad producida es menor a la planificada
        if (cantidadProducida < ordenActual.getCantidadPlanificada()) {
            boolean confirmar = AlertUtil.confirmar(
                    "Atención",
                    "La cantidad producida (" + cantidadProducida + ") es menor " +
                            "a la planificada (" + ordenActual.getCantidadPlanificada() + ").\n" +
                            "¿Deseas completar la orden de todas formas?"
            );
            if (!confirmar) return;
        }

        try {
            boolean ok = facade.completarOrden(ordenActual.getIdOrdenProd(), cantidadProducida);
            if (ok) {
                AlertUtil.info("Éxito", "Orden #" + ordenActual.getIdOrdenProd() + " completada.");
                if (onGuardado != null) onGuardado.run();
                cerrarVentana();
            } else {
                AlertUtil.error("Error", "No se pudo completar la orden.");
            }
        } catch (SQLException e) {
            AlertUtil.error("Error de base de datos", e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
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
        Stage stage = (Stage) txtCantidadProducida.getScene().getWindow();
        stage.close();
    }
}