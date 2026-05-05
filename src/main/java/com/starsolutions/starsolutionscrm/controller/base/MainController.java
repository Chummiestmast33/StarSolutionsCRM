package com.starsolutions.starsolutionscrm.controller.base;

import com.starsolutions.starsolutionscrm.util.AlertUtil;
import com.starsolutions.starsolutionscrm.util.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML private Label lblBienvenida;
    @FXML private Label lblRol;

    // Botones de RRHH y Produccion
    @FXML private Button btnEmpleados;
    @FXML private Button btnNomina;
    @FXML private Button btnAsistencia;
    @FXML private Button btnProduccion;

    // Botones de CRM y Ventas (Nuevos)
    @FXML private Button btnClientes;
    @FXML private Button btnProveedores;
    @FXML private Button btnVentas;

    private static final String BASE_FXML = "/com/starsolutions/starsolutionscrm/fxml/";

    // Inicializar
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

    // Configurar visibilidad segun el tipo de empleado
    private void configurarAccesoPorRol(String tipo) {
        // Por defecto ocultar todo
        btnEmpleados.setVisible(false);
        btnNomina.setVisible(false);
        btnAsistencia.setVisible(false);
        btnProduccion.setVisible(false);
        btnClientes.setVisible(false);
        btnProveedores.setVisible(false);
        btnVentas.setVisible(false);

        // Asistencia la ven todos por defecto
        btnAsistencia.setVisible(true);

        switch (tipo) {
            case "RH" -> {
                btnEmpleados.setVisible(true);
                btnNomina.setVisible(true);
            }
            case "Produccion" -> {
                btnProduccion.setVisible(true);
            }
            case "Ventas" -> {
                btnClientes.setVisible(true);
                btnVentas.setVisible(true);
            }
            case "Inventario" -> {
                btnProveedores.setVisible(true);
                // Aqui luego tu companero 2 agregara sus botones de stock y compras
            }
        }
    }

    // Navegacion RRHH
    @FXML
    public void onEmpleados() {
        abrirVentana("rrhh/empleado-lista.fxml", "Empleados");
    }

    @FXML
    public void onNomina() {
        abrirVentana("rrhh/nomina-lista.fxml", "Nomina");
    }

    @FXML
    public void onAsistencia() {
        abrirVentana("rrhh/empleado-desempeno.fxml", "Asistencia");
    }

    // Navegacion Produccion
    @FXML
    public void onProduccion() {
        abrirVentana("prd/orden-produccion-lista.fxml", "Produccion");
    }

    // Navegacion CRM
    @FXML
    public void onClientes() {
        abrirVentana("crm/cliente-lista.fxml", "Directorio de Clientes");
    }

    @FXML
    public void onProveedores() {
        abrirVentana("crm/proveedor-lista.fxml", "Catalogo de Proveedores");
    }

    // Navegacion Ventas
    @FXML
    public void onVentas() {
        abrirVentana("ven/venta-nueva.fxml", "Terminal de Ventas");
    }

    // Cerrar sesion
    @FXML
    public void onCerrarSesion() {
        SessionManager.getInstance().cerrarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(BASE_FXML + "base/login.fxml")
            );
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 600, 400));
            stage.setTitle("Star Solutions CRM - Login");
            stage.setResizable(false);
        } catch (IOException e) {
            AlertUtil.error("Error", "No se pudo cerrar la sesion correctamente");
        }
    }

    // Metodo para abrir ventanas hijas
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
            AlertUtil.error("Error de Navegacion", "No se encontro el archivo: " + rutaFxml);
            e.printStackTrace();
        }
    }
}