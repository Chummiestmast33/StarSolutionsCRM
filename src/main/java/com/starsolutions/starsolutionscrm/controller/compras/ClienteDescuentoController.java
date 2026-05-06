package com.starsolutions.starsolutionscrm.controller.compras;

import com.starsolutions.starsolutionscrm.dao.impl.ClienteDAOImpl;
import com.starsolutions.starsolutionscrm.dao.impl.ClienteDescuentoDAOImpl;
import com.starsolutions.starsolutionscrm.model.crm.Cliente;
import com.starsolutions.starsolutionscrm.model.crm.ClienteDescuento;
import com.starsolutions.starsolutionscrm.util.AlertUtil;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.math.BigDecimal;

public class ClienteDescuentoController {

    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private TextField txtDescuento;
    @FXML private TextField txtDescripcion;

    private final ClienteDAOImpl clienteDAO = new ClienteDAOImpl();
    private final ClienteDescuentoDAOImpl descuentoDAO = new ClienteDescuentoDAOImpl();

    @FXML
    public void initialize() {
        cargarClientes();

        // Listener para cargar el descuento cuando se selecciona un cliente
        cmbCliente.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cargarDescuentoCliente(newVal.getIdCliente());
            }
        });
    }

    private void cargarClientes() {
        try {
            cmbCliente.setItems(FXCollections.observableArrayList(clienteDAO.listarActivos()));
            cmbCliente.setConverter(new StringConverter<Cliente>() {
                @Override
                public String toString(Cliente c) { return c == null ? "" : c.getNombre(); }
                @Override
                public Cliente fromString(String s) { return null; }
            });
        } catch (Exception e) {
            AlertUtil.error("Error", "No se pudieron cargar los clientes.");
        }
    }

    private void cargarDescuentoCliente(int idCliente) {
        try {
            ClienteDescuento cd = descuentoDAO.obtenerPorCliente(idCliente);
            if (cd != null) {
                txtDescuento.setText(cd.getDescuento().toString());
                txtDescripcion.setText(cd.getDescripcion());
            } else {
                txtDescuento.setText("0.00");
                txtDescripcion.clear();
            }
        } catch (Exception e) {
            AlertUtil.error("Error", "No se pudo cargar la informacion.");
        }
    }

    @FXML
    public void onGuardar() {
        Cliente cliente = cmbCliente.getValue();
        if (cliente == null) {
            AlertUtil.error("Aviso", "Selecciona un cliente.");
            return;
        }

        try {
            BigDecimal porcentaje = new BigDecimal(txtDescuento.getText().trim());
            if (porcentaje.compareTo(BigDecimal.ZERO) < 0 || porcentaje.compareTo(new BigDecimal("100")) > 0) {
                AlertUtil.error("Validacion", "El descuento debe estar entre 0 y 100.");
                return;
            }

            ClienteDescuento cd = new ClienteDescuento();
            cd.setIdCliente(cliente.getIdCliente());
            cd.setDescuento(porcentaje);
            cd.setDescripcion(txtDescripcion.getText().trim());

            if (descuentoDAO.guardar(cd)) {
                AlertUtil.info("Exito", "Descuento VIP guardado correctamente.");
            }
        } catch (NumberFormatException e) {
            AlertUtil.error("Error", "Ingresa un porcentaje de descuento valido.");
        } catch (Exception e) {
            AlertUtil.error("Error", e.getMessage());
        }
    }
}