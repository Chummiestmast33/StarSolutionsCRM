package com.starsolutions.starsolutionscrm.controller.compras;

import com.starsolutions.starsolutionscrm.dao.impl.ProveedorDAOImpl;
import com.starsolutions.starsolutionscrm.dao.interfaces.IProveedorDAO;
import com.starsolutions.starsolutionscrm.model.crm.Proveedor;
import com.starsolutions.starsolutionscrm.util.AlertUtil;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ProveedorFormController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtRfc;
    @FXML private TextField txtDireccion;

    private final IProveedorDAO proveedorDAO = new ProveedorDAOImpl();
    private Proveedor proveedorActual = null;
    private Runnable onGuardado = null;

    // Recibe el proveedor a editar o null si es uno nuevo
    public void setProveedor(Proveedor proveedor) {
        this.proveedorActual = proveedor;
        if (proveedor != null) {
            txtNombre.setText(proveedor.getNombre());
            txtRfc.setText(proveedor.getRfc() != null ? proveedor.getRfc() : "");
            txtDireccion.setText(proveedor.getDireccion() != null ? proveedor.getDireccion() : "");
        }
    }

    // Callback para refrescar la tabla al terminar
    public void setOnGuardado(Runnable onGuardado) {
        this.onGuardado = onGuardado;
    }

    @FXML
    public void onGuardar() {
        String nombre = txtNombre.getText().trim();
        String rfc = txtRfc.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (nombre.isEmpty()) {
            AlertUtil.error("Error", "El nombre de la empresa es obligatorio");
            return;
        }

        try {
            boolean exito;

            if (proveedorActual == null) {
                // Modo: Crear nuevo
                Proveedor nuevo = new Proveedor();
                nuevo.setNombre(nombre);
                nuevo.setDireccion(direccion.isEmpty() ? null : direccion);
                nuevo.setRfc(rfc.isEmpty() ? null : rfc);
                exito = proveedorDAO.crear(nuevo);
            } else {
                // Modo: Editar existente
                proveedorActual.setNombre(nombre);
                proveedorActual.setRfc(rfc.isEmpty() ? null : rfc);
                proveedorActual.setDireccion(direccion.isEmpty() ? null : direccion);
                exito = proveedorDAO.actualizar(proveedorActual);
            }

            if (exito) {
                AlertUtil.info("Exito", "Proveedor guardado correctamente");
                if (onGuardado != null) onGuardado.run();
                cerrarVentana();
            } else {
                AlertUtil.error("Error", "No se pudo guardar el proveedor en BD");
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