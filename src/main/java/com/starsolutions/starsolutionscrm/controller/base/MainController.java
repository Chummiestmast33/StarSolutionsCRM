package com.starsolutions.starsolutionscrm.controller.base;

import com.starsolutions.starsolutionscrm.util.AlertUtil;
import com.starsolutions.starsolutionscrm.util.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML private Label lblBienvenida;
    @FXML private Label lblRol;

    // Botones de menú — visibles según rol
    @FXML private javafx.scene.control.Button btnEmpleados;
    @FXML private javafx.scene.control.Button btnNomina;
    @FXML private javafx.scene.control.Button btnAsistencia;
    @FXML private javafx.scene.control.Button btnProduccion;

    private static final String BASE_FXML = "/com/starsolutions/starsolutionscrm/fxml/";

    // ----------------------------------------------------------------
    // INICIALIZAR
    // ----------------------------------------------------------------
    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        String nombre = session.getEmpleadoActual().getNombre();
        String tipo   = session.getTipoEmpleado();

        lblBienvenida.setText("Bienvenido, " + nombre);
        lblRol.setText("Rol: " + tipo);

        // Control de acceso por rol
        configurarAccesoPorRol(tipo);
    }

    private void configurarAccesoPorRol(String tipo) {
        // Por defecto ocultar todo
        btnEmpleados.setVisible(false);
        btnNomina.setVisible(false);
        btnAsistencia.setVisible(false);
        btnProduccion.setVisible(false);

        switch (tipo) {
            case "RH" -> {
                btnEmpleados.setVisible(true);
                btnNomina.setVisible(true);
                btnAsistencia.setVisible(true);
            }
            case "Produccion" -> {
                btnProduccion.setVisible(true);
                btnAsistencia.setVisible(true);
            }
            case "Ventas", "Inventario" -> {
                // Solo asistencia propia
                btnAsistencia.setVisible(true);
            }
        }
    }

    // ----------------------------------------------------------------
    // NAVEGACIÓN
    // ----------------------------------------------------------------
    @FXML
    public void onEmpleados() {
        abrirVentana("rrhh/empleado-lista.fxml", "Empleados");
    }

    @FXML
    public void onNomina() {
        abrirVentana("rrhh/nomina-lista.fxml", "Nómina");
    }

    @FXML
    public void onAsistencia() {
        abrirVentana("rrhh/empleado-desempeno.fxml", "Asistencia");
    }

    @FXML
    public void onProduccion() {
        abrirVentana("prd/orden-produccion-lista.fxml", "Producción");
    }

    @FXML
    public void onCerrarSesion() {
        SessionManager.getInstance().cerrarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(BASE_FXML + "base/login.fxml")
            );
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 600, 400));
            stage.setTitle("Star Solutions CRM — Login");
            stage.setResizable(false);
        } catch (IOException e) {
            AlertUtil.error("Error", "No se pudo cerrar la sesión correctamente.");
        }
    }

    // ----------------------------------------------------------------
    // MÉTODO PRIVADO — abrir ventana
    // ----------------------------------------------------------------
    private void abrirVentana(String rutaFxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(BASE_FXML + rutaFxml)
            );
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(titulo);
            stage.show();
        } catch (IOException e) {
            AlertUtil.error("Error", "No se pudo abrir: " + titulo);
        }
    }
}