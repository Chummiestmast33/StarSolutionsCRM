package com.starsolutions.starsolutionscrm.controller.base;

import com.starsolutions.starsolutionscrm.util.AlertUtil;
import com.starsolutions.starsolutionscrm.util.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private Label lblBienvenida;
    @FXML
    private Label lblRol;

    @FXML
    private VBox sideMenu;
    @FXML
    private StackPane contentArea;
    @FXML
    private Button btnMenuToggle;

    // Botones Generales
    @FXML
    private Button btnEmpleados;
    @FXML
    private Button btnNomina;
    @FXML
    private Button btnAsistencia;
    @FXML
    private Button btnProduccion;
    @FXML
    private Button btnProveedores;
    @FXML
    private Button btnProductos;
    @FXML
    private Button btnStock;
    @FXML
    private Button btnAjusteStock;
    @FXML
    private Button btnCompras;

    // Botones de CRM y Ventas
    @FXML
    private Button btnClientes;
    @FXML
    private Button btnClienteDescuento;
    @FXML
    private Button btnVentas;
    @FXML
    private Button btnHistorial;
    @FXML
    private Button btnCobros;
    @FXML
    private Button btnDevoluciones;
    @FXML
    private Button btnPromociones;

    private static final String BASE_FXML = "/com/starsolutions/starsolutionscrm/fxml/";
    private boolean menuExpanded = true;

    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        if (session.getEmpleadoActual() != null) {
            String nombre = session.getEmpleadoActual().getNombre();
            String tipo = session.getTipoEmpleado();

            lblBienvenida.setText(nombre);
            lblRol.setText(tipo);

            configurarAccesoPorRol(tipo);
        }
    }

    @FXML
    public void onToggleMenu() {
        menuExpanded = !menuExpanded;
        if (menuExpanded) {
            sideMenu.setPrefWidth(220);
            ocultarTextoBotones(false);
        } else {
            sideMenu.setPrefWidth(60);
            ocultarTextoBotones(true);
        }
    }

    private void ocultarTextoBotones(boolean ocultar) {
        btnEmpleados.setContentDisplay(
                ocultar ? javafx.scene.control.ContentDisplay.GRAPHIC_ONLY : javafx.scene.control.ContentDisplay.LEFT);
        btnNomina.setContentDisplay(
                ocultar ? javafx.scene.control.ContentDisplay.GRAPHIC_ONLY : javafx.scene.control.ContentDisplay.LEFT);
        btnAsistencia.setContentDisplay(
                ocultar ? javafx.scene.control.ContentDisplay.GRAPHIC_ONLY : javafx.scene.control.ContentDisplay.LEFT);
        btnProduccion.setContentDisplay(
                ocultar ? javafx.scene.control.ContentDisplay.GRAPHIC_ONLY : javafx.scene.control.ContentDisplay.LEFT);
        btnProveedores.setContentDisplay(
                ocultar ? javafx.scene.control.ContentDisplay.GRAPHIC_ONLY : javafx.scene.control.ContentDisplay.LEFT);
        btnProductos.setContentDisplay(
                ocultar ? javafx.scene.control.ContentDisplay.GRAPHIC_ONLY : javafx.scene.control.ContentDisplay.LEFT);
        btnStock.setContentDisplay(
                ocultar ? javafx.scene.control.ContentDisplay.GRAPHIC_ONLY : javafx.scene.control.ContentDisplay.LEFT);
        btnAjusteStock.setContentDisplay(
                ocultar ? javafx.scene.control.ContentDisplay.GRAPHIC_ONLY : javafx.scene.control.ContentDisplay.LEFT);
        btnCompras.setContentDisplay(
                ocultar ? javafx.scene.control.ContentDisplay.GRAPHIC_ONLY : javafx.scene.control.ContentDisplay.LEFT);

        btnClientes.setContentDisplay(
                ocultar ? javafx.scene.control.ContentDisplay.GRAPHIC_ONLY : javafx.scene.control.ContentDisplay.LEFT);
        btnClienteDescuento.setContentDisplay(
                ocultar ? javafx.scene.control.ContentDisplay.GRAPHIC_ONLY : javafx.scene.control.ContentDisplay.LEFT);
        btnVentas.setContentDisplay(
                ocultar ? javafx.scene.control.ContentDisplay.GRAPHIC_ONLY : javafx.scene.control.ContentDisplay.LEFT);
        btnHistorial.setContentDisplay(
                ocultar ? javafx.scene.control.ContentDisplay.GRAPHIC_ONLY : javafx.scene.control.ContentDisplay.LEFT);
        btnCobros.setContentDisplay(
                ocultar ? javafx.scene.control.ContentDisplay.GRAPHIC_ONLY : javafx.scene.control.ContentDisplay.LEFT);
        btnDevoluciones.setContentDisplay(
                ocultar ? javafx.scene.control.ContentDisplay.GRAPHIC_ONLY : javafx.scene.control.ContentDisplay.LEFT);
        btnPromociones.setContentDisplay(
                ocultar ? javafx.scene.control.ContentDisplay.GRAPHIC_ONLY : javafx.scene.control.ContentDisplay.LEFT);
    }

    // Metodo para ocultar/mostrar botones segun el area de trabajo
    private void configurarAccesoPorRol(String tipo) {
        // 1. Ocultar todos los botones primero (also set managed=false so layout
        // collapses)
        btnEmpleados.setVisible(false);
        btnEmpleados.setManaged(false);
        btnNomina.setVisible(false);
        btnNomina.setManaged(false);
        btnAsistencia.setVisible(false);
        btnAsistencia.setManaged(false);
        btnProduccion.setVisible(false);
        btnProduccion.setManaged(false);
        btnProveedores.setVisible(false);
        btnProveedores.setManaged(false);
        btnProductos.setVisible(false);
        btnProductos.setManaged(false);
        btnStock.setVisible(false);
        btnStock.setManaged(false);
        btnAjusteStock.setVisible(false);
        btnAjusteStock.setManaged(false);
        btnCompras.setVisible(false);
        btnCompras.setManaged(false);

        btnClientes.setVisible(false);
        btnClientes.setManaged(false);
        btnClienteDescuento.setVisible(false);
        btnClienteDescuento.setManaged(false);
        btnVentas.setVisible(false);
        btnVentas.setManaged(false);
        btnHistorial.setVisible(false);
        btnHistorial.setManaged(false);
        btnCobros.setVisible(false);
        btnCobros.setManaged(false);
        btnDevoluciones.setVisible(false);
        btnDevoluciones.setManaged(false);
        btnPromociones.setVisible(false);
        btnPromociones.setManaged(false);

        // 2. Todos los empleados pueden registrar asistencia
        btnAsistencia.setVisible(true);
        btnAsistencia.setManaged(true);

        // 3. Habilitar solo lo correspondiente al rol
        switch (tipo) {
            case "RH" -> {
                btnEmpleados.setVisible(true);
                btnEmpleados.setManaged(true);
                btnNomina.setVisible(true);
                btnNomina.setManaged(true);
            }
            case "Produccion" -> {
                btnProduccion.setVisible(true);
                btnProduccion.setManaged(true);
            }
            case "Inventario" -> {
                btnProveedores.setVisible(true);
                btnProveedores.setManaged(true);
                btnProductos.setVisible(true);
                btnProductos.setManaged(true);
                btnStock.setVisible(true);
                btnStock.setManaged(true);
                btnAjusteStock.setVisible(true);
                btnAjusteStock.setManaged(true);
                btnCompras.setVisible(true);
                btnCompras.setManaged(true);
            }
            case "Ventas" -> {
                btnClientes.setVisible(true);
                btnClientes.setManaged(true);
                btnClienteDescuento.setVisible(true);
                btnClienteDescuento.setManaged(true);
                btnVentas.setVisible(true);
                btnVentas.setManaged(true);
                btnHistorial.setVisible(true);
                btnHistorial.setManaged(true);
                btnCobros.setVisible(true);
                btnCobros.setManaged(true);
                btnDevoluciones.setVisible(true);
                btnDevoluciones.setManaged(true);
                btnPromociones.setVisible(true);
                btnPromociones.setManaged(true);
            }
        }
    }

    // ==========================================================
    // METODOS DE NAVEGACION
    // ==========================================================

    @FXML
    public void onEmpleados() {
        cargarVista("rrhh/empleado-lista.fxml");
    }

    @FXML
    public void onNomina() {
        cargarVista("rrhh/nomina-lista.fxml");
    }

    @FXML
    public void onAsistencia() {
        cargarVista("rrhh/empleado-desempeno.fxml");
    }

    @FXML
    public void onProduccion() {
        cargarVista("prd/orden-produccion-lista.fxml");
    }

    @FXML
    public void onProveedores() {
        cargarVista("crm/proveedor-lista.fxml");
    }

    @FXML
    public void onProductos() {
        cargarVista("inv/producto-lista.fxml");
    }

    @FXML
    public void onStock() {
        cargarVista("inv/stock-consulta.fxml");
    }

    @FXML
    public void onAjusteStock() {
        cargarVista("inv/stock-ajuste.fxml");
    }

    @FXML
    public void onCompras() {
        cargarVista("cpm/orden-compra-lista.fxml");
    }

    @FXML
    public void onClientes() {
        cargarVista("crm/cliente-lista.fxml");
    }

    @FXML
    public void onClienteDescuento() {
        cargarVista("crm/cliente-descuento.fxml");
    }

    @FXML
    public void onVentas() {
        cargarVista("ven/venta-nueva.fxml");
    }

    @FXML
    public void onHistorial() {
        cargarVista("ven/venta-historial.fxml");
    }

    @FXML
    public void onCobros() {
        cargarVista("ven/venta-cobro.fxml");
    }

    @FXML
    public void onDevoluciones() {
        cargarVista("ven/devolucion-form.fxml");
    }

    @FXML
    public void onPromociones() {
        cargarVista("ven/promocion-lista.fxml");
    }

    @FXML
    public void onIndicadores() {
        cargarVista("rrhh/indicadores.fxml");
    }

    @FXML
    public void onCategorias(){
        cargarVista("inv/categoria-lista.fxml");
    }


    @FXML
    public void onCerrarSesion() {
        SessionManager.getInstance().cerrarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(BASE_FXML + "base/login.fxml"));
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 600, 400));
            stage.setTitle("Star Solutions CRM - Login");
            stage.setResizable(false);
        } catch (IOException e) {
            AlertUtil.error("Error", "No se pudo cerrar la sesion.");
        }
    }

    // Metodo centralizado para cargar vistas en el area central
    private void cargarVista(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(BASE_FXML + rutaFxml));
            Node vista = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(vista);
        } catch (Exception e) {
            System.err.println("No se pudo cargar la vista: " + rutaFxml);
            e.printStackTrace();
        }
    }
}