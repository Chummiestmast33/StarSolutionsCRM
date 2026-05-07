package com.starsolutions.starsolutionscrm.controller.ventas;

import com.starsolutions.starsolutionscrm.dao.impl.VentaDAOImpl;
import com.starsolutions.starsolutionscrm.model.ventas.Venta;
import com.starsolutions.starsolutionscrm.util.AlertUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VentaHistorialController {

    @FXML
    private TextField txtIdCliente;
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
    private final ObservableList<Venta> listaVentas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cbEstatus.setItems(FXCollections.observableArrayList("", "Activa", "Liquidada", "Cancelada"));
        // Enlazar las columnas de la tabla con los atributos de la clase Venta
        colIdVenta.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colEstatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));
        colCondicion.setCellValueFactory(new PropertyValueFactory<>("condicionPago"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        tablaHistorial.setItems(listaVentas);
        cargarHistorial();
    }

    @FXML
    public void onFiltrar() {
        try {
            Integer idCliente = txtIdCliente.getText().isBlank() ? null
                    : Integer.parseInt(txtIdCliente.getText().trim());
            LocalDate desde = dpFechaInicio.getValue();
            LocalDate hasta = dpFechaFin.getValue();
            String estatus = cbEstatus.getValue();

            listaVentas.clear();
            listaVentas.addAll(ventaDAO.listarVentasPorFiltros(idCliente, desde, hasta, estatus));
        } catch (NumberFormatException e) {
            AlertUtil.error("Error", "El ID de cliente debe ser un número.");
        } catch (Exception e) {
            AlertUtil.error("Error", "No se pudo filtrar: " + e.getMessage());
        }
    }

    @FXML
    public void onLimpiarFiltros() {
        txtIdCliente.clear();
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