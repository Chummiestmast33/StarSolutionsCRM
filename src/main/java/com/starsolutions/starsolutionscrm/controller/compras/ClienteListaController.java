package com.starsolutions.starsolutionscrm.controller.compras;

import com.starsolutions.starsolutionscrm.dao.impl.ClienteDAOImpl;
import com.starsolutions.starsolutionscrm.dao.interfaces.IClienteDAO;
import com.starsolutions.starsolutionscrm.model.crm.Cliente;
import com.starsolutions.starsolutionscrm.util.AlertUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ClienteListaController {

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colRfc;
    @FXML private TableColumn<Cliente, String> colDireccion;
    @FXML private TableColumn<Cliente, Boolean> colActivo;

    private final IClienteDAO clienteDAO = new ClienteDAOImpl();
    private final ObservableList<Cliente> lista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colRfc.setCellValueFactory(new PropertyValueFactory<>("rfc"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        tablaClientes.setItems(lista);
        cargarClientes();
    }

    private void cargarClientes() {
        try {
            lista.clear();
            lista.addAll(clienteDAO.listarActivos());
            tablaClientes.refresh();

        } catch (SQLException e) {
            AlertUtil.error("Error", "No se pudieron cargar los clientes: " + e.getMessage());
        }
    }

    @FXML
    public void onNuevo() {
        abrirFormulario(null);
    }

    @FXML
    public void onEditar() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertUtil.info("Aviso", "Selecciona un cliente para editar");
            return;
        }
        abrirFormulario(seleccionado);
    }

    @FXML
    public void onDesactivar() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertUtil.info("Aviso", "Selecciona un cliente para desactivar");
            return;
        }

        boolean confirmar = AlertUtil.confirmar("Confirmar", "¿Desactivar cliente " + seleccionado.getNombre() + "?");
        if (confirmar) {
            try {
                boolean ok = clienteDAO.desactivar(seleccionado.getIdCliente());
                if (ok) {
                    AlertUtil.info("Exito", "Cliente desactivado");
                    cargarClientes();
                }
            } catch (SQLException e) {
                AlertUtil.error("Error", "Error al desactivar: " + e.getMessage());
            }
        }
    }

    @FXML
    public void onRefrescar() {
        cargarClientes();
        System.out.println("Refrescado papu");
    }
    private void abrirFormulario(Cliente cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/starsolutions/starsolutionscrm/fxml/crm/cliente-form.fxml")
            );
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(cliente == null ? "Nuevo Cliente" : "Editar Cliente");
            stage.initModality(Modality.APPLICATION_MODAL);

            // Pasamos los datos al formulario
            ClienteFormController controller = loader.getController();
            controller.setCliente(cliente);
            controller.setOnGuardado(this::cargarClientes);

            stage.showAndWait();
        } catch (IOException e) {
            AlertUtil.error("Error", "No se pudo abrir el formulario");
        }
    }
}