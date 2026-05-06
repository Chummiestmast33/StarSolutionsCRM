package com.starsolutions.starsolutionscrm.controller.rrhh;

import com.starsolutions.starsolutionscrm.facade.RRHHFacade;
import com.starsolutions.starsolutionscrm.model.rrhh.Asistencia;
import com.starsolutions.starsolutionscrm.util.AlertUtil;
import com.starsolutions.starsolutionscrm.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.util.List;

public class EmpleadoDesempenoController {

    // ----------------------------------------------------------------
    // FXML — Panel superior (solo visible para RH)
    // ----------------------------------------------------------------
    @FXML private HBox panelFiltroRH;
    @FXML private TextField txtFiltroEmpleado;

    // ----------------------------------------------------------------
    // FXML — Estado del día
    // ----------------------------------------------------------------
    @FXML private Label lblEstadoHoy;
    @FXML private Button btnEntrada;
    @FXML private Button btnSalida;

    // ----------------------------------------------------------------
    // FXML — Tabla de historial
    // ----------------------------------------------------------------
    @FXML private TableView<Asistencia>       tablaAsistencia;
    @FXML private TableColumn<Asistencia, Integer>   colId;
    @FXML private TableColumn<Asistencia, Integer>   colEmpleado;
    @FXML private TableColumn<Asistencia, String>    colFecha;
    @FXML private TableColumn<Asistencia, String>    colEntrada;
    @FXML private TableColumn<Asistencia, String>    colSalida;

    // ----------------------------------------------------------------
    // Estado interno
    // ----------------------------------------------------------------
    private final RRHHFacade facade = new RRHHFacade();
    private int idEmpleadoActivo;   // empleado cuya asistencia se muestra
    private boolean esRH;

    // ----------------------------------------------------------------
    // INITIALIZE
    // ----------------------------------------------------------------
    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        idEmpleadoActivo = session.getEmpleadoActual().getNum();
        esRH = "RH".equals(session.getTipoEmpleado());

        configurarColumnas();

        // El panel de búsqueda solo es visible para RH
        panelFiltroRH.setVisible(esRH);
        panelFiltroRH.setManaged(esRH);

        // La columna de empleado solo tiene sentido en la vista de RH
        colEmpleado.setVisible(esRH);

        cargarVista(idEmpleadoActivo);
    }

    // ----------------------------------------------------------------
    // ACCIONES — Botones de registro
    // ----------------------------------------------------------------

    @FXML
    public void onRegistrarEntrada() {
        try {
            boolean ok = facade.registrarEntrada(idEmpleadoActivo);
            if (ok) {
                AlertUtil.info("Entrada registrada", "Se registró tu entrada correctamente.");
            } else {
                AlertUtil.error("Sin cambios", "No se pudo registrar la entrada. ¿Ya la registraste hoy?");
            }
            cargarVista(idEmpleadoActivo);
        } catch (SQLException e) {
            AlertUtil.error("Error de base de datos", "No se pudo registrar la entrada: " + e.getMessage());
        }
    }

    @FXML
    public void onRegistrarSalida() {
        try {
            boolean ok = facade.registrarSalida(idEmpleadoActivo);
            if (ok) {
                AlertUtil.info("Salida registrada", "Se registró tu salida correctamente.");
            } else {
                AlertUtil.error("Sin cambios", "No se pudo registrar la salida. ¿Registraste tu entrada primero?");
            }
            cargarVista(idEmpleadoActivo);
        } catch (SQLException e) {
            AlertUtil.error("Error de base de datos", "No se pudo registrar la salida: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // ACCIONES — Filtro RH
    // ----------------------------------------------------------------

    @FXML
    public void onBuscarEmpleado() {
        String texto = txtFiltroEmpleado.getText().trim();
        if (texto.isEmpty()) {
            // Si borra el campo, vuelve a mostrar la vista propia
            idEmpleadoActivo = SessionManager.getInstance().getEmpleadoActual().getNum();
            cargarVista(idEmpleadoActivo);
            return;
        }
        try {
            int num = Integer.parseInt(texto);
            idEmpleadoActivo = num;
            cargarVista(idEmpleadoActivo);
        } catch (NumberFormatException e) {
            AlertUtil.error("Número inválido", "Ingresa un número de empleado válido.");
        }
    }

    @FXML
    public void onRefrescar() {
        cargarVista(idEmpleadoActivo);
    }

    // ----------------------------------------------------------------
    // LÓGICA INTERNA
    // ----------------------------------------------------------------

    /**
     * Carga el estado de hoy y el historial completo del empleado indicado.
     * Actualiza los botones según el estado actual del registro.
     */
    private void cargarVista(int idEmpleado) {
        actualizarEstadoHoy(idEmpleado);
        cargarHistorial(idEmpleado);
    }

    private void actualizarEstadoHoy(int idEmpleado) {
        try {
            Asistencia hoy = facade.obtenerAsistenciaHoy(idEmpleado);

            if (hoy == null) {
                // No hay registro hoy
                lblEstadoHoy.setText("Sin registro hoy");
                btnEntrada.setDisable(false);
                btnSalida.setDisable(true);

            } else if (hoy.getHoraSalida() == null) {
                // Entró pero no salió
                lblEstadoHoy.setText("Entrada: " + hoy.getHoraEntrada() + "  |  Salida: pendiente");
                btnEntrada.setDisable(true);
                btnSalida.setDisable(false);

            } else {
                // Jornada completa
                lblEstadoHoy.setText("Entrada: " + hoy.getHoraEntrada() + "  |  Salida: " + hoy.getHoraSalida());
                btnEntrada.setDisable(true);
                btnSalida.setDisable(true);
            }

            // RH consultando a otro empleado no debe poder registrar por él
            if (esRH && idEmpleado != SessionManager.getInstance().getEmpleadoActual().getNum()) {
                btnEntrada.setDisable(true);
                btnSalida.setDisable(true);
            }

        } catch (SQLException e) {
            lblEstadoHoy.setText("Error al consultar");
            AlertUtil.error("Error", "No se pudo obtener el estado de hoy: " + e.getMessage());
        }
    }

    private void cargarHistorial(int idEmpleado) {
        try {
            List<Asistencia> lista = facade.obtenerAsistenciaPorEmpleado(idEmpleado);
            tablaAsistencia.setItems(FXCollections.observableArrayList(lista));
        } catch (SQLException e) {
            AlertUtil.error("Error", "No se pudo cargar el historial: " + e.getMessage());
        }
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idAsistencia"));
        colEmpleado.setCellValueFactory(new PropertyValueFactory<>("idEmpleado"));

        // Fecha formateada
        colFecha.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getFecha() != null
                                ? data.getValue().getFecha().toString()
                                : ""));

        // Hora entrada — puede ser null
        colEntrada.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getHoraEntrada() != null
                                ? data.getValue().getHoraEntrada().toString()
                                : "—"));

        // Hora salida — puede ser null
        colSalida.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getHoraSalida() != null
                                ? data.getValue().getHoraSalida().toString()
                                : "—"));
    }
}