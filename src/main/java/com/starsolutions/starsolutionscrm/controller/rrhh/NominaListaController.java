package com.starsolutions.starsolutionscrm.controller.rrhh;

import com.starsolutions.starsolutionscrm.facade.RRHHFacade;
import com.starsolutions.starsolutionscrm.model.rrhh.Nomina;
import com.starsolutions.starsolutionscrm.util.AlertUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;

public class NominaListaController {

    @FXML private TableView<Nomina>               tablaNomina;
    @FXML private TableColumn<Nomina, Integer>    colId;
    @FXML private TableColumn<Nomina, Integer>    colEmpleado;
    @FXML private TableColumn<Nomina, Double>     colSalarioBase;
    @FXML private TableColumn<Nomina, Double>     colDeducciones;
    @FXML private TableColumn<Nomina, Double>     colNeto;
    @FXML private TableColumn<Nomina, LocalDate>  colPeriodo;

    @FXML private TextField    txtFiltroEmpleado;
    @FXML private DatePicker   dpFiltroMes;

    private final RRHHFacade facade = new RRHHFacade();
    private final ObservableList<Nomina> lista = FXCollections.observableArrayList();

    // ----------------------------------------------------------------
    // INICIALIZAR
    // ----------------------------------------------------------------
    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idNomina"));
        colEmpleado.setCellValueFactory(new PropertyValueFactory<>("idEmpleado"));
        colSalarioBase.setCellValueFactory(new PropertyValueFactory<>("salarioBase"));
        colDeducciones.setCellValueFactory(new PropertyValueFactory<>("deducciones"));
        colNeto.setCellValueFactory(new PropertyValueFactory<>("neto"));
        colPeriodo.setCellValueFactory(new PropertyValueFactory<>("periodo"));

        tablaNomina.setItems(lista);

        // Cargar nómina del mes actual por defecto
        dpFiltroMes.setValue(LocalDate.now().withDayOfMonth(1));
        cargarPorPeriodo();
    }

    // ----------------------------------------------------------------
    // FILTRAR POR EMPLEADO
    // ----------------------------------------------------------------
    @FXML
    public void onFiltrarPorEmpleado() {
        String texto = txtFiltroEmpleado.getText().trim();
        if (texto.isEmpty()) {
            AlertUtil.info("Aviso", "Ingresa el número de empleado.");
            return;
        }

        int idEmpleado;
        try {
            idEmpleado = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            AlertUtil.error("Error", "El número de empleado debe ser entero.");
            return;
        }

        try {
            lista.setAll(facade.obtenerNominaPorEmpleado(idEmpleado));
            if (lista.isEmpty()) {
                AlertUtil.info("Sin resultados", "No se encontró nómina para ese empleado.");
            }
        } catch (SQLException e) {
            AlertUtil.error("Error", "Error al consultar: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // FILTRAR POR PERÍODO
    // ----------------------------------------------------------------
    @FXML
    public void onFiltrarPorPeriodo() {
        cargarPorPeriodo();
    }

    // ----------------------------------------------------------------
    // REFRESCAR
    // ----------------------------------------------------------------
    @FXML
    public void onRefrescar() {
        txtFiltroEmpleado.clear();
        dpFiltroMes.setValue(LocalDate.now().withDayOfMonth(1));
        cargarPorPeriodo();
    }

    // ----------------------------------------------------------------
    // MÉTODO PRIVADO
    // ----------------------------------------------------------------
    private void cargarPorPeriodo() {
        LocalDate periodo = dpFiltroMes.getValue();
        if (periodo == null) {
            AlertUtil.info("Aviso", "Selecciona un período.");
            return;
        }

        try {
            lista.setAll(facade.obtenerNominaPorPeriodo(periodo));
            if (lista.isEmpty()) {
                AlertUtil.info("Sin resultados", "No hay nómina registrada para ese período.");
            }
        } catch (SQLException e) {
            AlertUtil.error("Error", "Error al consultar: " + e.getMessage());
        }
    }
}