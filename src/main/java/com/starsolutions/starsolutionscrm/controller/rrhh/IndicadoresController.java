package com.starsolutions.starsolutionscrm.controller.rrhh;

import com.starsolutions.starsolutionscrm.facade.RRHHFacade;
import com.starsolutions.starsolutionscrm.model.rrhh.Empleado;
import com.starsolutions.starsolutionscrm.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class IndicadoresController {

    @FXML private TableView<Empleado>             tablaIndicadores;
    @FXML private TableColumn<Empleado, Integer>  colNum;
    @FXML private TableColumn<Empleado, String>   colNombre;
    @FXML private TableColumn<Empleado, String>   colTipo;
    @FXML private TableColumn<Empleado, Double>   colProductividad;
    @FXML private TableColumn<Empleado, Double>   colEficiencia;
    @FXML private ComboBox<String>                cbTipoFiltro;
    @FXML private TextField                       txtNumEmpleado;
    @FXML private TextField                       txtProductividad;
    @FXML private TextField                       txtEficiencia;

    private final RRHHFacade facade = new RRHHFacade();
    private final ObservableList<Empleado> listaEmpleados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNum.setCellValueFactory(new PropertyValueFactory<>("num"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoEmpleado"));
        colProductividad.setCellValueFactory(new PropertyValueFactory<>("productividad"));
        colEficiencia.setCellValueFactory(new PropertyValueFactory<>("eficiencia"));

        cbTipoFiltro.setItems(FXCollections.observableArrayList(
            "", "Ventas", "RH", "Inventario", "Produccion"));

        tablaIndicadores.setItems(listaEmpleados);

        // Al seleccionar una fila, llenar los campos de edición
        tablaIndicadores.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, sel) -> {
                if (sel != null) {
                    txtNumEmpleado.setText(String.valueOf(sel.getNum()));
                    txtProductividad.setText(String.valueOf(sel.getProductividad()));
                    txtEficiencia.setText(String.valueOf(sel.getEficiencia()));
                }
            });

        cargarTodos();
    }

    private void cargarTodos() {
        try {
            List<Empleado> lista = facade.listarEmpleadosActivos();
            listaEmpleados.setAll(lista);
        } catch (Exception e) {
            AlertUtil.error("Error", "No se pudo cargar la lista: " + e.getMessage());
        }
    }

    @FXML
    public void onFiltrar() {
        String tipo = cbTipoFiltro.getValue();
        try {
            List<Empleado> lista = facade.listarEmpleadosActivos();
            if (tipo != null && !tipo.isBlank()) {
                lista = lista.stream()
                        .filter(e -> tipo.equals(e.getTipoEmpleado()))
                        .toList();
            }
            listaEmpleados.setAll(lista);
        } catch (Exception e) {
            AlertUtil.error("Error", "No se pudo filtrar: " + e.getMessage());
        }
    }

    @FXML
    public void onGuardar() {
        try {
            int num = Integer.parseInt(txtNumEmpleado.getText().trim());
            double prod = Double.parseDouble(txtProductividad.getText().trim());
            double efic = Double.parseDouble(txtEficiencia.getText().trim());

            if (prod < 0 || prod > 100 || efic < 0 || efic > 100) {
                AlertUtil.error("Valor inválido", "Los porcentajes deben estar entre 0 y 100.");
                return;
            }

            boolean ok = facade.actualizarIndicadores(num, prod, efic);
            if (ok) {
                AlertUtil.info("Guardado", "Indicadores actualizados correctamente.");
                cargarTodos();
            }
        } catch (NumberFormatException e) {
            AlertUtil.error("Error", "Verifica que los valores sean numéricos.");
        } catch (Exception e) {
            AlertUtil.error("Error", e.getMessage());
        }
    }
}