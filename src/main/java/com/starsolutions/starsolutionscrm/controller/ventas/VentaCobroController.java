package com.starsolutions.starsolutionscrm.controller.ventas;

import com.starsolutions.starsolutionscrm.dao.impl.VentaDAOImpl;
import com.starsolutions.starsolutionscrm.facade.VentasFacade;
import com.starsolutions.starsolutionscrm.model.ventas.Cobro;
import com.starsolutions.starsolutionscrm.model.ventas.Venta;
import com.starsolutions.starsolutionscrm.util.AlertUtil;
import com.starsolutions.starsolutionscrm.util.SearchableComboBoxUtil;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.util.List;

public class VentaCobroController {

    @FXML
    private ComboBox<Venta> cmbVenta;
    @FXML
    private TextField txtMonto;
    @FXML
    private Label lblSaldoPendiente;

    private final VentasFacade facade = new VentasFacade();
    private final VentaDAOImpl ventaDAO = new VentaDAOImpl();

    private BigDecimal saldoActual = BigDecimal.ZERO;

    @FXML
    public void initialize() {
        cargarVentas();

        cmbVenta.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                onBuscarVenta();
            } else {
                lblSaldoPendiente.setText("$ 0.00");
                saldoActual = BigDecimal.ZERO;
            }
        });
    }

    private void cargarVentas() {
        try {
            List<Venta> ventasPendientes = facade.listarVentasCreditoActivasPendientes();
            SearchableComboBoxUtil.setupSearchableComboBox(
                    cmbVenta,
                    ventasPendientes,
                    venta -> String.format("%d - %s - Total: $%.2f",
                            venta.getIdVenta(),
                            venta.getClienteNombre() != null ? venta.getClienteNombre()
                                    : ("Cliente " + venta.getIdCliente()),
                            venta.getTotal()));
        } catch (Exception e) {
            AlertUtil.error("Error", "No se pudieron cargar las ventas pendientes.");
            e.printStackTrace();
        }
    }

    @FXML
    public void onBuscarVenta() {
        try {
            Venta venta = cmbVenta.getValue();
            if (venta == null)
                return;

            saldoActual = ventaDAO.obtenerSaldoPendiente(venta.getIdVenta());
            lblSaldoPendiente.setText("$ " + saldoActual.toString());

            if (saldoActual.compareTo(BigDecimal.ZERO) <= 0) {
                AlertUtil.info("Aviso", "Esta venta ya esta liquidada o no existe.");
            }
        } catch (Exception e) {
            AlertUtil.error("Error", e.getMessage());
        }
    }

    @FXML
    public void onRegistrarCobro() {
        try {
            Venta venta = cmbVenta.getValue();
            if (venta == null) {
                AlertUtil.error("Error", "Seleccione una venta.");
                return;
            }

            String montoStr = txtMonto.getText().trim();
            if (montoStr.isEmpty()) {
                AlertUtil.error("Error", "Ingrese un monto.");
                return;
            }

            BigDecimal monto = new BigDecimal(montoStr);

            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                AlertUtil.error("Error", "El monto debe ser mayor a 0.");
                return;
            }

            Cobro cobro = new Cobro();
            cobro.setIdVenta(venta.getIdVenta());
            cobro.setIdCliente(venta.getIdCliente());
            cobro.setMonto(monto);

            facade.registrarCobroParcial(cobro);

            AlertUtil.info("Exito", "Cobro registrado correctamente.");
            txtMonto.clear();

            cargarVentas();
            cmbVenta.getSelectionModel().clearSelection();

        } catch (NumberFormatException e) {
            AlertUtil.error("Error", "Formato de monto inv�lido.");
        } catch (Exception e) {
            AlertUtil.error("Error al cobrar", e.getMessage());
        }
    }
}
