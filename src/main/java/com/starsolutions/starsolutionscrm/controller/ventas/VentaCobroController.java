package com.starsolutions.starsolutionscrm.controller.ventas;

import com.starsolutions.starsolutionscrm.dao.impl.VentaDAOImpl;
import com.starsolutions.starsolutionscrm.facade.VentasFacade;
import com.starsolutions.starsolutionscrm.model.ventas.Cobro;
import com.starsolutions.starsolutionscrm.util.AlertUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.math.BigDecimal;

public class VentaCobroController {

    @FXML private TextField txtIdVenta;
    @FXML private TextField txtIdCliente;
    @FXML private TextField txtMonto;
    @FXML private Label lblSaldoPendiente;

    private final VentasFacade facade = new VentasFacade();
    private final VentaDAOImpl ventaDAO = new VentaDAOImpl();

    private BigDecimal saldoActual = BigDecimal.ZERO;

    // Metodo para buscar cuanto debe el cliente de una venta especifica
    @FXML
    public void onBuscarVenta() {
        try {
            int idVenta = Integer.parseInt(txtIdVenta.getText().trim());
            saldoActual = ventaDAO.obtenerSaldoPendiente(idVenta);

            lblSaldoPendiente.setText("$ " + saldoActual.toString());

            if (saldoActual.compareTo(BigDecimal.ZERO) <= 0) {
                AlertUtil.info("Aviso", "Esta venta ya esta liquidada o no existe.");
            }
        } catch (NumberFormatException e) {
            AlertUtil.error("Error", "El ID de la venta debe ser un numero.");
        } catch (Exception e) {
            AlertUtil.error("Error", e.getMessage());
        }
    }

    // Metodo para registrar el abono o liquidacion
    @FXML
    public void onRegistrarCobro() {
        try {
            int idVenta = Integer.parseInt(txtIdVenta.getText().trim());
            int idCliente = Integer.parseInt(txtIdCliente.getText().trim());
            BigDecimal monto = new BigDecimal(txtMonto.getText().trim());

            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                AlertUtil.error("Error", "El monto debe ser mayor a 0.");
                return;
            }

            // Armar el objeto cobro
            Cobro cobro = new Cobro();
            cobro.setIdVenta(idVenta);
            cobro.setIdCliente(idCliente);
            cobro.setMonto(monto);

            // Mandar a la fachada que registre el cobro y cambie el estatus a Liquidada si aplica
            facade.registrarCobroParcial(cobro);

            AlertUtil.info("Exito", "Cobro registrado correctamente.");
            txtMonto.clear();
            onBuscarVenta(); // Refrescar el saldo en pantalla

        } catch (Exception e) {
            AlertUtil.error("Error al cobrar", e.getMessage());
        }
    }
}