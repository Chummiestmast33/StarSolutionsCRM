package com.starsolutions.starsolutionscrm.controller.compras;

import com.starsolutions.starsolutionscrm.dao.impl.ClienteDAOImpl;
import com.starsolutions.starsolutionscrm.dao.interfaces.IClienteDAO;
import com.starsolutions.starsolutionscrm.model.crm.Cliente;
import com.starsolutions.starsolutionscrm.util.AlertUtil;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ClienteFormController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtRfc;
    @FXML private TextField txtDireccion;

    private final IClienteDAO clienteDAO = new ClienteDAOImpl();
    private Cliente clienteActual = null;
    private Runnable onGuardado = null;

    // Recibe el cliente a editar o null si es nuevo
    public void setCliente(Cliente cliente) {
        this.clienteActual = cliente;
        if (cliente != null) {
            txtNombre.setText(cliente.getNombre());
            txtRfc.setText(cliente.getRfc() != null ? cliente.getRfc() : "");
            txtDireccion.setText(cliente.getDireccion() != null ? cliente.getDireccion() : "");
        }
    }

    // Recibe el metodo para refrescar la tabla
    public void setOnGuardado(Runnable onGuardado) {
        this.onGuardado = onGuardado;
    }

    @FXML
    public void onGuardar() {
        String nombre = txtNombre.getText().trim();
        String rfc = txtRfc.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (nombre.isEmpty()) {
            AlertUtil.error("Error", "El nombre es obligatorio");
            return;
        }

        try {
            boolean exito;

            if (clienteActual == null) {
                // Nuevo cliente
                Cliente nuevo = new Cliente(nombre, direccion.isEmpty() ? null : direccion, rfc.isEmpty() ? null : rfc);
                exito = clienteDAO.crear(nuevo);
            } else {
                // Editar cliente
                clienteActual.setNombre(nombre);
                clienteActual.setRfc(rfc.isEmpty() ? null : rfc);
                clienteActual.setDireccion(direccion.isEmpty() ? null : direccion);
                exito = clienteDAO.actualizar(clienteActual);
            }

            if (exito) {
                AlertUtil.info("Exito", "Cliente guardado correctamente");
                if (onGuardado != null) onGuardado.run();
                cerrarVentana();
            } else {
                AlertUtil.error("Error", "No se pudo guardar el cliente en BD");
            }

        } catch (SQLException e) {
            AlertUtil.error("Error BD", e.getMessage());
        }
    }

    @FXML
    public void onCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
}