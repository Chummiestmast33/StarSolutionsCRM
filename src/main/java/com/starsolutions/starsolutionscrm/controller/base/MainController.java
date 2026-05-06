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

    // Botones Generales
    @FXML private Button btnEmpleados;
    @FXML private Button btnNomina;
    @FXML private Button btnAsistencia;
    @FXML private Button btnProduccion;
    @FXML private Button btnProveedores;
    @FXML private Button btnProductos;
    @FXML private Button btnStock;
    @FXML private Button btnAjusteStock;
    @FXML private Button btnCompras;

    // Botones de CRM y Ventas
    @FXML private Button btnClientes;
    @FXML private Button btnClienteDescuento;
    @FXML private Button btnVentas;
    @FXML private Button btnHistorial;
    @FXML private Button btnCobros;
    @FXML private Button btnDevoluciones;
    @FXML private Button btnPromociones;

    private static final String BASE_FXML = "/com/starsolutions/starsolutionscrm/fxml/";

    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        String nombre = session.getEmpleadoActual().getNombre();
        String tipo   = session.getTipoEmpleado();

        lblBienvenida.setText("Bienvenido, " + nombre);
        lblRol.setText("Rol: " + tipo);

        // Control de acceso al menú lateral
        configurarAccesoPorRol(tipo);
    }

    // Metodo para ocultar/mostrar botones segun el area de trabajo
    private void configurarAccesoPorRol(String tipo) {
        // 1. Ocultar todos los botones primero
        btnEmpleados.setVisible(false);
        btnNomina.setVisible(false);
        btnAsistencia.setVisible(false);
        btnProduccion.setVisible(false);
        btnProveedores.setVisible(false);
        btnProductos.setVisible(false);
        btnStock.setVisible(false);
        btnAjusteStock.setVisible(false);
        btnCompras.setVisible(false);

        btnClientes.setVisible(false);
        btnClienteDescuento.setVisible(false);
        btnVentas.setVisible(false);
        btnHistorial.setVisible(false);
        btnCobros.setVisible(false);
        btnDevoluciones.setVisible(false);
        btnPromociones.setVisible(false);

        // 2. Todos los empleados pueden registrar asistencia
        btnAsistencia.setVisible(true);

        // 3. Habilitar solo lo correspondiente al rol
        switch (tipo) {
            case "RH" -> {
                btnEmpleados.setVisible(true);
                btnNomina.setVisible(true);
            }
            case "Produccion" -> {
                btnProduccion.setVisible(true);
            }
            case "Inventario" -> {
                btnProveedores.setVisible(true);
                btnProductos.setVisible(true);
                btnStock.setVisible(true);
                btnAjusteStock.setVisible(true);
                btnCompras.setVisible(true);
            }
            case "Ventas" -> {
                btnClientes.setVisible(true);
                btnClienteDescuento.setVisible(true);
                btnVentas.setVisible(true);
                btnHistorial.setVisible(true);
                btnCobros.setVisible(true);
                btnDevoluciones.setVisible(true);
                btnPromociones.setVisible(true);
            }
        }
    }

    // ==========================================================
    // METODOS DE NAVEGACION
    // ==========================================================

    @FXML
    public void onEmpleados() { abrirVentana("rrhh/empleado-lista.fxml", "Empleados"); }

    @FXML
    public void onNomina() { abrirVentana("rrhh/nomina-lista.fxml", "Nomina"); }

    @FXML
    public void onAsistencia() { abrirVentana("rrhh/empleado-desempeno.fxml", "Asistencia"); }

    @FXML
    public void onProduccion() { abrirVentana("prd/orden-produccion-lista.fxml", "Produccion"); }

    @FXML
    public void onProveedores() { abrirVentana("crm/proveedor-lista.fxml", "Proveedores"); }

    @FXML
    public void onProductos() { abrirVentana("inv/producto-lista.fxml", "Productos"); }

    @FXML
    public void onStock() { abrirVentana("inv/stock-consulta.fxml", "Consulta de Stock"); }

    @FXML
    public void onAjusteStock() { abrirVentana("inv/stock-ajuste.fxml", "Ajuste de Inventario"); }

    @FXML
    public void onCompras() { abrirVentana("cpm/orden-compra-lista.fxml", "Compras"); }

    @FXML
    public void onClientes() { abrirVentana("crm/cliente-lista.fxml", "Clientes"); }

    @FXML
    public void onClienteDescuento() { abrirVentana("crm/cliente-descuento.fxml", "Descuentos VIP"); }

    @FXML
    public void onVentas() { abrirVentana("ven/venta-nueva.fxml", "Terminal de Ventas"); }

    @FXML
    public void onHistorial() { abrirVentana("ven/venta-historial.fxml", "Historial de Ventas"); }

    @FXML
    public void onCobros() { abrirVentana("ven/venta-cobro.fxml", "Registrar Cobros"); }

    @FXML
    public void onDevoluciones() { abrirVentana("ven/devolucion-form.fxml", "Devoluciones"); }

    @FXML
    public void onPromociones() { abrirVentana("ven/promocion-lista.fxml", "Promociones y Ofertas"); }

    // ==========================================================
    // SESION Y APERTURA DE VENTANAS
    // ==========================================================

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

    // Metodo centralizado para abrir cualquier pantalla
    private void abrirVentana(String rutaFxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(BASE_FXML + rutaFxml));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(titulo);
            stage.show();
        } catch (Exception e) {
            AlertUtil.error("Error de Interfaz", "Archivo no encontrado: " + rutaFxml);
            e.printStackTrace();
        }
    }
}