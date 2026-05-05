package com.starsolutions.starsolutionscrm.controller.ventas;

import com.starsolutions.starsolutionscrm.dao.impl.ClienteDAOImpl;
import com.starsolutions.starsolutionscrm.dao.interfaces.IClienteDAO;
import com.starsolutions.starsolutionscrm.facade.VentasFacade;
import com.starsolutions.starsolutionscrm.model.ventas.Venta;
import com.starsolutions.starsolutionscrm.model.ventas.VentaDetalle;
import com.starsolutions.starsolutionscrm.model.crm.Cliente;
import com.starsolutions.starsolutionscrm.util.AlertUtil;
import com.starsolutions.starsolutionscrm.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;

public class VentaNuevaController {

    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private TextField txtIdProducto;
    @FXML private TextField txtCantidad;

    @FXML private TableView<VentaDetalle> tablaDetalles;
    @FXML private TableColumn<VentaDetalle, Integer> colProducto;
    @FXML private TableColumn<VentaDetalle, Integer> colCantidad;
    @FXML private TableColumn<VentaDetalle, BigDecimal> colPrecio;
    @FXML private TableColumn<VentaDetalle, BigDecimal> colDescuento;
    @FXML private TableColumn<VentaDetalle, BigDecimal> colSubtotalLinea;

    @FXML private Label lblSubtotal;
    @FXML private Label lblDescuentos;
    @FXML private Label lblTotal;

    private final VentasFacade facade = new VentasFacade();
    private final IClienteDAO clienteDAO = new ClienteDAOImpl();
    private final ObservableList<VentaDetalle> listaDetalles = FXCollections.observableArrayList();

    // Inicializar componentes
    @FXML
    public void initialize() {
        colProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuentoAplicado"));
        colSubtotalLinea.setCellValueFactory(new PropertyValueFactory<>("importeTotalLinea"));

        tablaDetalles.setItems(listaDetalles);

        // Cargar clientes en el ComboBox
        cargarClientesEnComboBox();
    }

    // Metodo para llenar el ComboBox desde BD
    private void cargarClientesEnComboBox() {
        try {
            // Obtenemos los clientes activos de la BD
            ObservableList<Cliente> clientes = FXCollections.observableArrayList(clienteDAO.listarActivos());
            cmbCliente.setItems(clientes);

            // Le decimos al ComboBox que solo muestre el nombre del cliente
            cmbCliente.setConverter(new StringConverter<Cliente>() {
                @Override
                public String toString(Cliente c) {
                    return c == null ? "" : c.getNombre();
                }

                @Override
                public Cliente fromString(String string) {
                    return null; // No lo necesitamos para buscar escribiendo por ahora
                }
            });

        } catch (SQLException e) {
            AlertUtil.error("Error", "No se pudieron cargar los clientes en el selector: " + e.getMessage());
        }
    }

    // Agregar producto a la tabla
    @FXML
    public void onAgregarProducto() {
        String idTxt = txtIdProducto.getText().trim();
        String cantTxt = txtCantidad.getText().trim();

        if (idTxt.isEmpty() || cantTxt.isEmpty()) {
            AlertUtil.error("Error", "Ingresa un ID de producto y la cantidad");
            return;
        }

        try {
            int idProd = Integer.parseInt(idTxt);
            int cantidad = Integer.parseInt(cantTxt);

            if (cantidad <= 0) {
                AlertUtil.error("Error", "La cantidad debe ser mayor a 0");
                return;
            }

            // Simulacion de busqueda de precio del producto (se hara con DAO despues)
            BigDecimal precioSimulado = new BigDecimal("100.00");

            VentaDetalle detalle = new VentaDetalle(idProd, cantidad, precioSimulado);
            listaDetalles.add(detalle);

            txtIdProducto.clear();
            txtCantidad.setText("1");

            // Actualizar vista
            recalcularTotalesVisuales();

        } catch (NumberFormatException e) {
            AlertUtil.error("Error", "El ID y la cantidad deben ser numeros enteros");
        }
    }

    // Quitar producto de la tabla
    @FXML
    public void onQuitarProducto() {
        VentaDetalle seleccionado = tablaDetalles.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            listaDetalles.remove(seleccionado);
            recalcularTotalesVisuales();
        } else {
            AlertUtil.info("Aviso", "Selecciona un producto de la tabla para quitarlo");
        }
    }

    // Boton de cobrar
    @FXML
    public void onCobrar() {
        if (listaDetalles.isEmpty()) {
            AlertUtil.error("Error", "No hay productos en el ticket");
            return;
        }

        // Validar que se haya seleccionado un cliente
        Cliente clienteSeleccionado = cmbCliente.getValue();
        if (clienteSeleccionado == null) {
            AlertUtil.error("Error", "Debes seleccionar un cliente para realizar la venta");
            return;
        }

        // Obtener el empleado que inicio sesion
        if (!SessionManager.getInstance().haySesionActiva()) {
            AlertUtil.error("Error", "No hay una sesion activa");
            return;
        }
        int idEmpleado = SessionManager.getInstance().getEmpleadoActual().getNum();

        // Armar el objeto Venta
        Venta nuevaVenta = new Venta();
        nuevaVenta.setIdCliente(clienteSeleccionado.getIdCliente());
        nuevaVenta.setIdEmpleado(idEmpleado);

        try {
            // Mandar a la fachada que procese la logica pesada
            int idGenerado = facade.procesarNuevaVenta(nuevaVenta, new ArrayList<>(listaDetalles));

            AlertUtil.info("Venta Exitosa", "Se registro la venta con el ID #" + idGenerado);

            // Limpiar pantalla
            listaDetalles.clear();
            cmbCliente.getSelectionModel().clearSelection();
            recalcularTotalesVisuales();

        } catch (Exception e) {
            AlertUtil.error("Error al registrar venta", e.getMessage());
        }
    }

    // Metodo privado para actualizar los labels de la derecha
    private void recalcularTotalesVisuales() {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (VentaDetalle det : listaDetalles) {
            subtotal = subtotal.add(det.getImporteTotalLinea());
        }

        lblSubtotal.setText(subtotal.toString());
        lblTotal.setText(subtotal.toString());
    }
}