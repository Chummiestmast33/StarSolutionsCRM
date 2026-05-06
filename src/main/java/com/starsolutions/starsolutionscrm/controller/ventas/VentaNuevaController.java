package com.starsolutions.starsolutionscrm.controller.ventas;

import com.starsolutions.starsolutionscrm.dao.impl.ClienteDAOImpl;
import com.starsolutions.starsolutionscrm.facade.VentasFacade;
import com.starsolutions.starsolutionscrm.model.crm.Cliente;
import com.starsolutions.starsolutionscrm.model.inventario.Producto;
import com.starsolutions.starsolutionscrm.model.ventas.Venta;
import com.starsolutions.starsolutionscrm.model.ventas.VentaDetalle;
import com.starsolutions.starsolutionscrm.util.AlertUtil;
import com.starsolutions.starsolutionscrm.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.util.ArrayList;

public class VentaNuevaController {

    // Componentes de la interfaz FXML
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private ComboBox<String> cmbTipoPago;
    @FXML private TextField txtIdProducto;
    @FXML private TextField txtCantidad;

    // Tabla del carrito de compras
    @FXML private TableView<VentaDetalle> tablaDetalles;
    @FXML private TableColumn<VentaDetalle, Integer> colProducto;
    @FXML private TableColumn<VentaDetalle, Integer> colCantidad;
    @FXML private TableColumn<VentaDetalle, BigDecimal> colPrecio;
    @FXML private TableColumn<VentaDetalle, BigDecimal> colDescuento;
    @FXML private TableColumn<VentaDetalle, BigDecimal> colSubtotalLinea;

    // Etiquetas de totales
    @FXML private Label lblSubtotal;
    @FXML private Label lblDescuentos;
    @FXML private Label lblTotal;

    private final VentasFacade facade = new VentasFacade();
    private final ClienteDAOImpl clienteDAO = new ClienteDAOImpl();
    private final ObservableList<VentaDetalle> listaDetalles = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Enlazar columnas con atributos del modelo VentaDetalle
        colProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuentoAplicado"));
        colSubtotalLinea.setCellValueFactory(new PropertyValueFactory<>("importeTotalLinea"));

        tablaDetalles.setItems(listaDetalles);
        cargarClientes();

        // Cargar opciones de pago
        if (cmbTipoPago != null) {
            cmbTipoPago.setItems(FXCollections.observableArrayList("Contado", "Credito"));
            cmbTipoPago.getSelectionModel().selectFirst();
        }

        // Listener: Recalcular ticket automaticamente al cambiar de cliente
        cmbCliente.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            recalcularTotalesVisuales();
        });
    }

    private void cargarClientes() {
        try {
            ObservableList<Cliente> clientes = FXCollections.observableArrayList(clienteDAO.listarActivos());
            cmbCliente.setItems(clientes);
            cmbCliente.setConverter(new StringConverter<Cliente>() {
                @Override
                public String toString(Cliente c) { return c == null ? "" : c.getNombre(); }
                @Override
                public Cliente fromString(String string) { return null; }
            });
        } catch (Exception e) {
            AlertUtil.error("Error BD", "Error al cargar el catalogo de clientes.");
        }
    }

    @FXML
    public void onAgregarProducto() {
        String idTxt = txtIdProducto.getText().trim();
        String cantTxt = txtCantidad.getText().trim();

        if (idTxt.isEmpty() || cantTxt.isEmpty()) {
            AlertUtil.error("Validacion", "Ingresa el codigo del producto y la cantidad.");
            return;
        }

        try {
            int idProd = Integer.parseInt(idTxt);
            int cantidad = Integer.parseInt(cantTxt);

            if (cantidad <= 0) return;

            Producto p = facade.buscarProducto(idProd);

            if (p == null || !p.isActivo()) {
                AlertUtil.error("Error", "El producto no existe o esta inactivo.");
                return;
            }

            // Agregamos al carrito y recalculamos totales
            VentaDetalle detalle = new VentaDetalle(idProd, cantidad, p.getPrecioUnitario());
            listaDetalles.add(detalle);

            txtIdProducto.clear();
            txtCantidad.setText("1");
            recalcularTotalesVisuales();

        } catch (NumberFormatException e) {
            AlertUtil.error("Error", "Los campos requieren valores numericos enteros.");
        } catch (Exception e) {
            AlertUtil.error("Error del sistema", e.getMessage());
        }
    }

    @FXML
    public void onQuitarProducto() {
        VentaDetalle seleccionado = tablaDetalles.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            listaDetalles.remove(seleccionado);
            recalcularTotalesVisuales();
        } else {
            AlertUtil.info("Aviso", "Selecciona un producto de la tabla para quitarlo.");
        }
    }

    // Recalculo dinamico ejecutado al modificar productos o cambiar cliente
    private void recalcularTotalesVisuales() {
        if (cmbCliente.getValue() == null || listaDetalles.isEmpty()) {
            lblSubtotal.setText("0.00");
            lblDescuentos.setText("0.00");
            lblTotal.setText("0.00");
            tablaDetalles.refresh();
            return;
        }

        try {
            Venta vTemp = new Venta();
            // Ejecutamos la logica de descuentos centralizada en la fachada
            facade.calcularTicket(cmbCliente.getValue().getIdCliente(), listaDetalles, vTemp);

            // Actualizamos la vista
            lblSubtotal.setText(vTemp.getSubtotal().toString());
            lblDescuentos.setText(vTemp.getDescuentoAplicado().toString());
            lblTotal.setText(vTemp.getTotal().toString());
            tablaDetalles.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onCobrar() {
        if (listaDetalles.isEmpty() || cmbCliente.getValue() == null) {
            AlertUtil.error("Aviso", "Faltan datos obligatorios (Selecciona cliente y productos).");
            return;
        }

        if (!SessionManager.getInstance().haySesionActiva()) {
            AlertUtil.error("Acceso denegado", "No hay una sesion de empleado activa.");
            return;
        }

        // Construir objeto para mandar a procesar
        Venta nuevaVenta = new Venta();
        nuevaVenta.setIdCliente(cmbCliente.getValue().getIdCliente());
        nuevaVenta.setIdEmpleado(SessionManager.getInstance().getEmpleadoActual().getNum());

        if (cmbTipoPago != null && cmbTipoPago.getValue() != null) {
            nuevaVenta.setCondicionPago(cmbTipoPago.getValue());
        } else {
            nuevaVenta.setCondicionPago("Contado");
        }

        try {
            int idGenerado = facade.procesarNuevaVenta(nuevaVenta, new ArrayList<>(listaDetalles));
            AlertUtil.info("Ticket Confirmado", "Venta exitosa. Folio: #" + idGenerado + "\nEl inventario ha sido descontado.");

            // Limpiar terminal para el siguiente cliente
            listaDetalles.clear();
            cmbCliente.getSelectionModel().clearSelection();
            recalcularTotalesVisuales();

        } catch (Exception e) {
            AlertUtil.error("Error en Transaccion", e.getMessage());
        }
    }
}