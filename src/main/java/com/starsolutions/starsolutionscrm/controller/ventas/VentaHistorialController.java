package com.starsolutions.starsolutionscrm.controller.ventas;

import com.starsolutions.starsolutionscrm.dao.impl.ClienteDAOImpl;
import com.starsolutions.starsolutionscrm.dao.impl.VentaDAOImpl;
import com.starsolutions.starsolutionscrm.model.crm.Cliente;
import com.starsolutions.starsolutionscrm.model.ventas.Venta;
import com.starsolutions.starsolutionscrm.util.AlertUtil;
import com.starsolutions.starsolutionscrm.util.SearchableComboBoxUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaHistorialController {

    @FXML
    private ComboBox<Cliente> cmbCliente;
    @FXML
    private DatePicker dpFechaInicio;
    @FXML
    private DatePicker dpFechaFin;
    @FXML
    private ComboBox<String> cbEstatus;
    @FXML
    private TableView<Venta> tablaHistorial;
    @FXML
    private TableColumn<Venta, Integer> colIdVenta;
    @FXML
    private TableColumn<Venta, LocalDate> colFecha;
    @FXML
    private TableColumn<Venta, String> colEstatus;
    @FXML
    private TableColumn<Venta, String> colCondicion;
    @FXML
    private TableColumn<Venta, BigDecimal> colTotal;

    private final VentaDAOImpl ventaDAO = new VentaDAOImpl();
    private final ClienteDAOImpl clienteDAO = new ClienteDAOImpl();
    private final ObservableList<Venta> listaVentas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cbEstatus.setItems(FXCollections.observableArrayList("", "Activa", "Liquidada", "Cancelada"));
        colIdVenta.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colEstatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));
        colCondicion.setCellValueFactory(new PropertyValueFactory<>("condicionPago"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        tablaHistorial.setItems(listaVentas);
        cargarClientes();
        cargarHistorial();
    }

    private void cargarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.listarActivos();
            
            // Add "Todos" as first option with a special marker
            Cliente todosClie = new Cliente();
            todosClie.setIdCliente(0);
            todosClie.setNombre("Todos");
            
            List<Cliente> clientesConTodos = new ArrayList<>();
            clientesConTodos.add(todosClie);
            clientesConTodos.addAll(clientes);
            
            SearchableComboBoxUtil.setupSearchableComboBox(
                cmbCliente,
                clientesConTodos,
                c -> c.getIdCliente() == 0 ? "Todos" : (c.getIdCliente() + " - " + c.getNombre())
            );
            
            cmbCliente.getSelectionModel().selectFirst();
        } catch (Exception e) {
            AlertUtil.error("Error", "No se pudieron cargar los clientes.");
            e.printStackTrace();
        }
    }

    @FXML
    public void onFiltrar() {
        try {
            Cliente clienteSeleccionado = cmbCliente.getValue();
            Integer idCliente = (clienteSeleccionado != null && clienteSeleccionado.getIdCliente() != 0) 
                ? clienteSeleccionado.getIdCliente() 
                : null;
            
            LocalDate desde = dpFechaInicio.getValue();
            LocalDate hasta = dpFechaFin.getValue();
            String estatus = cbEstatus.getValue();

            listaVentas.clear();
            listaVentas.addAll(ventaDAO.listarVentasPorFiltros(idCliente, desde, hasta, estatus));
        } catch (Exception e) {
            AlertUtil.error("Error", "No se pudo filtrar: " + e.getMessage());
        }
    }

    @FXML
    public void onLimpiarFiltros() {
        cmbCliente.getSelectionModel().selectFirst();
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
        cbEstatus.setValue(null);
        cargarHistorial();
    }

    @FXML
    public void cargarHistorial() {
        try {
            listaVentas.clear();
            listaVentas.addAll(ventaDAO.listarVentas());
            tablaHistorial.refresh();
        } catch (Exception e) {
            AlertUtil.error("Error", "No se pudo cargar el historial: " + e.getMessage());
        }
    }
}