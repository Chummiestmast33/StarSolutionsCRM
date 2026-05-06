package com.starsolutions.starsolutionscrm.controller.ventas;

import com.starsolutions.starsolutionscrm.dao.impl.PromocionDAOImpl;
import com.starsolutions.starsolutionscrm.model.ventas.Promocion;
import com.starsolutions.starsolutionscrm.util.AlertUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PromocionListaController {

    // Tabla
    @FXML private TableView<Promocion> tablaPromociones;
    @FXML private TableColumn<Promocion, Integer> colIdProducto;
    @FXML private TableColumn<Promocion, String> colNombre;
    @FXML private TableColumn<Promocion, BigDecimal> colDescuento;
    @FXML private TableColumn<Promocion, LocalDate> colInicio;
    @FXML private TableColumn<Promocion, LocalDate> colFin;

    // Formulario de creacion
    @FXML private TextField txtIdProducto;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescuento;
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFin;

    private final PromocionDAOImpl promocionDAO = new PromocionDAOImpl();
    private final ObservableList<Promocion> listaPromociones = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("porcentajeDesc"));
        colInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));

        tablaPromociones.setItems(listaPromociones);
        cargarPromociones();
    }

    private void cargarPromociones() {
        try {
            listaPromociones.clear();
            listaPromociones.addAll(promocionDAO.listarActivas());
            tablaPromociones.refresh();
        } catch (Exception e) {
            AlertUtil.error("Error", "Error al cargar promociones: " + e.getMessage());
        }
    }

    @FXML
    public void onGuardar() {
        try {
            int idProd = Integer.parseInt(txtIdProducto.getText().trim());
            BigDecimal desc = new BigDecimal(txtDescuento.getText().trim());
            String nombre = txtNombre.getText().trim();

            if (nombre.isEmpty()) {
                AlertUtil.error("Aviso", "El nombre de la promocion es obligatorio.");
                return;
            }

            Promocion p = new Promocion();
            p.setIdProducto(idProd);
            p.setNombre(nombre);
            p.setPorcentajeDesc(desc);
            p.setFechaInicio(dpInicio.getValue());
            p.setFechaFin(dpFin.getValue());

            if (promocionDAO.crear(p)) {
                AlertUtil.info("Exito", "Promocion registrada.");
                limpiarCampos();
                cargarPromociones();
            }
        } catch (NumberFormatException e) {
            AlertUtil.error("Error", "ID de producto y descuento deben ser numeros.");
        } catch (Exception e) {
            AlertUtil.error("Error", e.getMessage());
        }
    }

    @FXML
    public void onDesactivar() {
        Promocion p = tablaPromociones.getSelectionModel().getSelectedItem();
        if (p == null) {
            AlertUtil.info("Aviso", "Selecciona una promocion de la tabla.");
            return;
        }

        try {
            if (promocionDAO.desactivar(p.getIdPromocion())) {
                AlertUtil.info("Exito", "Promocion desactivada.");
                cargarPromociones();
            }
        } catch (Exception e) {
            AlertUtil.error("Error", "No se pudo desactivar la promocion.");
        }
    }

    private void limpiarCampos() {
        txtIdProducto.clear();
        txtNombre.clear();
        txtDescuento.clear();
        dpInicio.setValue(null);
        dpFin.setValue(null);
    }
}