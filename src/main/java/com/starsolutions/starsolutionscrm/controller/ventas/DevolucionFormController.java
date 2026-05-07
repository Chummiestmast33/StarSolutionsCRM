package com.starsolutions.starsolutionscrm.controller.ventas;

import com.starsolutions.starsolutionscrm.dao.impl.DevolucionDAOImpl;
import com.starsolutions.starsolutionscrm.facade.InventarioFacade;
import com.starsolutions.starsolutionscrm.facade.VentasFacade;
import com.starsolutions.starsolutionscrm.model.inventario.MovimientoInventario;
import com.starsolutions.starsolutionscrm.model.inventario.Stock;
import com.starsolutions.starsolutionscrm.model.ventas.Devolucion;
import com.starsolutions.starsolutionscrm.model.ventas.Venta;
import com.starsolutions.starsolutionscrm.util.AlertUtil;
import com.starsolutions.starsolutionscrm.util.SearchableComboBoxUtil;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.List;

public class DevolucionFormController {

    @FXML private ComboBox<Venta> cmbVenta;
    @FXML private TextField txtIdProducto;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtMontoDevuelto;
    @FXML private TextField txtMotivo;

    private final DevolucionDAOImpl devolucionDAO = new DevolucionDAOImpl();
    private final VentasFacade ventasFacade = new VentasFacade();
    private final InventarioFacade inventarioFacade = new InventarioFacade();

    @FXML
    public void initialize() {
        cargarVentas();
    }

    private void cargarVentas() {
        try {
            List<Venta> ventasActivas = ventasFacade.listarVentasParaDevolucion();
            SearchableComboBoxUtil.setupSearchableComboBox(
                cmbVenta,
                ventasActivas,
                venta -> String.format("%d - %s - Total: $%.2f",
                    venta.getIdVenta(),
                    venta.getFecha() != null ? venta.getFecha().toString() : "N/A",
                    venta.getTotal())
            );
        } catch (Exception e) {
            AlertUtil.error("Error", "No se pudieron cargar las ventas.");
            e.printStackTrace();
        }
    }

    @FXML
    public void onProcesarDevolucion() {
        try {
            Venta venta = cmbVenta.getValue();
            if (venta == null) {
                AlertUtil.error("Error", "Seleccione una venta.");
                return;
            }

            int idProducto = Integer.parseInt(txtIdProducto.getText().trim());
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            BigDecimal monto = new BigDecimal(txtMontoDevuelto.getText().trim());
            String motivo = txtMotivo.getText().trim();

            if (motivo.isEmpty()) {
                AlertUtil.error("Validacion", "Debes ingresar un motivo para la devolucion.");
                return;
            }

            if (cantidad <= 0 || monto.compareTo(BigDecimal.ZERO) <= 0) {
                AlertUtil.error("Error", "Cantidad y monto deben ser mayores a 0.");
                return;
            }

            Devolucion dev = new Devolucion();
            dev.setIdVenta(venta.getIdVenta());
            dev.setIdProducto(idProducto);
            dev.setCantidad(cantidad);
            dev.setMontoDevuelto(monto);
            dev.setMotivo(motivo);

            devolucionDAO.registrarDevolucion(dev);

            List<Stock> stocks = inventarioFacade.listarStockPorProducto(idProducto);
            if (!stocks.isEmpty()) {
                Stock stockPrincipal = stocks.get(0);

                MovimientoInventario mov = new MovimientoInventario();
                mov.setIdStock(stockPrincipal.getIdStock());
                mov.setTipo("ENTRADA");
                mov.setCantidad(cantidad);
                mov.setReferencia("Devolucion Venta: " + venta.getIdVenta());

                inventarioFacade.registrarMovimiento(mov);
            }

            AlertUtil.info("Exito", "Devolucion registrada y producto devuelto al inventario.");
            cerrarVentana();

        } catch (NumberFormatException e) {
            AlertUtil.error("Error", "Verifique que los numeros sean validos.");
        } catch (Exception e) {
            AlertUtil.error("Error", "Error al procesar la devolucion: " + e.getMessage());
        }
    }

    @FXML
    public void onCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) cmbVenta.getScene().getWindow();
        stage.close();
    }
}
