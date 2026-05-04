package com.starsolutions.starsolutionscrm.controller.produccion;

import com.starsolutions.starsolutionscrm.facade.ProduccionFacade;
import com.starsolutions.starsolutionscrm.model.produccion.OrdenProduccion;
import com.starsolutions.starsolutionscrm.util.AlertUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class OrdenProduccionListaController {

    @FXML private TableView<OrdenProduccion>               tablaOrdenes;
    @FXML private TableColumn<OrdenProduccion, Integer>    colId;
    @FXML private TableColumn<OrdenProduccion, Integer>    colEmpleado;
    @FXML private TableColumn<OrdenProduccion, Integer>    colProducto;
    @FXML private TableColumn<OrdenProduccion, Integer>    colPlanificado;
    @FXML private TableColumn<OrdenProduccion, Integer>    colProducido;
    @FXML private TableColumn<OrdenProduccion, LocalDateTime> colFechaInicio;
    @FXML private TableColumn<OrdenProduccion, LocalDate>  colFechaEstimada;
    @FXML private TableColumn<OrdenProduccion, String>     colEstado;

    @FXML private ComboBox<String> cmbFiltroEstado;

    private final ProduccionFacade facade = new ProduccionFacade();
    private final ObservableList<OrdenProduccion> lista = FXCollections.observableArrayList();

    private static final String BASE_FXML =
            "/com/starsolutions/starsolutionscrm/fxml/prd/";

    // ----------------------------------------------------------------
    // INICIALIZAR
    // ----------------------------------------------------------------
    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idOrdenProd"));
        colEmpleado.setCellValueFactory(new PropertyValueFactory<>("idEmpleado"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("idProductoFinal"));
        colPlanificado.setCellValueFactory(new PropertyValueFactory<>("cantidadPlanificada"));
        colProducido.setCellValueFactory(new PropertyValueFactory<>("cantidadProducida"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaEstimada.setCellValueFactory(new PropertyValueFactory<>("fechaEstimadaFin"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cmbFiltroEstado.setItems(FXCollections.observableArrayList(
                "Todas",
                OrdenProduccion.ESTADO_EN_PROCESO,
                OrdenProduccion.ESTADO_COMPLETADA,
                OrdenProduccion.ESTADO_CANCELADA
        ));
        cmbFiltroEstado.setValue(OrdenProduccion.ESTADO_EN_PROCESO);

        tablaOrdenes.setItems(lista);
        cargarOrdenes();
    }

    private void cargarOrdenes() {
        try {
            String filtro = cmbFiltroEstado.getValue();
            if (filtro == null || filtro.equals("Todas")) {
                lista.setAll(facade.obtenerTodasLasOrdenes());
            } else {
                lista.setAll(facade.obtenerOrdenesPorEstado(filtro));
            }
        } catch (SQLException e) {
            AlertUtil.error("Error", "No se pudieron cargar las órdenes: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // FILTRAR POR ESTADO
    // ----------------------------------------------------------------
    @FXML
    public void onFiltrar() {
        cargarOrdenes();
    }

    // ----------------------------------------------------------------
    // NUEVA ORDEN
    // ----------------------------------------------------------------
    @FXML
    public void onNueva() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(BASE_FXML + "orden-produccion-nueva.fxml")
            );
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Nueva Orden de Producción");
            stage.initModality(Modality.APPLICATION_MODAL);

            OrdenProduccionNuevaController controller = loader.getController();
            controller.setOnGuardado(this::cargarOrdenes);

            stage.showAndWait();
        } catch (IOException e) {
            AlertUtil.error("Error", "No se pudo abrir el formulario.");
        }
    }

    // ----------------------------------------------------------------
    // COMPLETAR ORDEN
    // ----------------------------------------------------------------
    @FXML
    public void onCompletar() {
        OrdenProduccion seleccionada = tablaOrdenes.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            AlertUtil.info("Aviso", "Selecciona una orden para completar.");
            return;
        }
        if (!OrdenProduccion.ESTADO_EN_PROCESO.equals(seleccionada.getEstado())) {
            AlertUtil.info("Aviso", "Solo se pueden completar órdenes En Proceso.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(BASE_FXML + "orden-produccion-completar.fxml")
            );
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Completar Orden #" + seleccionada.getIdOrdenProd());
            stage.initModality(Modality.APPLICATION_MODAL);

            OrdenProduccionCompletarController controller = loader.getController();
            controller.setOrden(seleccionada);
            controller.setOnGuardado(this::cargarOrdenes);

            stage.showAndWait();
        } catch (IOException e) {
            AlertUtil.error("Error", "No se pudo abrir el formulario.");
        }
    }

    // ----------------------------------------------------------------
    // CANCELAR ORDEN
    // ----------------------------------------------------------------
    @FXML
    public void onCancelar() {
        OrdenProduccion seleccionada = tablaOrdenes.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            AlertUtil.info("Aviso", "Selecciona una orden para cancelar.");
            return;
        }
        if (!OrdenProduccion.ESTADO_EN_PROCESO.equals(seleccionada.getEstado())) {
            AlertUtil.info("Aviso", "Solo se pueden cancelar órdenes En Proceso.");
            return;
        }

        boolean confirmar = AlertUtil.confirmar(
                "Confirmar",
                "¿Cancelar la orden #" + seleccionada.getIdOrdenProd() + "?"
        );
        if (!confirmar) return;

        try {
            boolean ok = facade.cancelarOrden(seleccionada.getIdOrdenProd());
            if (ok) {
                AlertUtil.info("Éxito", "Orden cancelada correctamente.");
                cargarOrdenes();
            } else {
                AlertUtil.error("Error", "No se pudo cancelar la orden.");
            }
        } catch (SQLException | IllegalStateException | IllegalArgumentException e) {
            AlertUtil.error("Error", e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // REFRESCAR
    // ----------------------------------------------------------------
    @FXML
    public void onRefrescar() {
        cargarOrdenes();
    }
}