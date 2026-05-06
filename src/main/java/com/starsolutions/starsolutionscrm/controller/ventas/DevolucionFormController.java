package com.starsolutions.starsolutionscrm.controller.ventas;

import com.starsolutions.starsolutionscrm.dao.impl.DevolucionDAOImpl;
import com.starsolutions.starsolutionscrm.facade.InventarioFacade;
import com.starsolutions.starsolutionscrm.model.inventario.MovimientoInventario;
import com.starsolutions.starsolutionscrm.model.inventario.Stock;
import com.starsolutions.starsolutionscrm.model.ventas.Devolucion;
import com.starsolutions.starsolutionscrm.util.AlertUtil;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.List;

public class DevolucionFormController {

    @FXML private TextField txtIdVenta;
    @FXML private TextField txtIdProducto;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtMontoDevuelto;
    @FXML private TextField txtMotivo;

    private final DevolucionDAOImpl devolucionDAO = new DevolucionDAOImpl();
    private final InventarioFacade inventarioFacade = new InventarioFacade();

    // Metodo para procesar la devolucion de un articulo
    @FXML
    public void onProcesarDevolucion() {
        try {
            int idVenta = Integer.parseInt(txtIdVenta.getText().trim());
            int idProducto = Integer.parseInt(txtIdProducto.getText().trim());
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            BigDecimal monto = new BigDecimal(txtMontoDevuelto.getText().trim());
            String motivo = txtMotivo.getText().trim();

            if (motivo.isEmpty()) {
                AlertUtil.error("Validacion", "Debes ingresar un motivo para la devolucion.");
                return;
            }

            // 1. Guardar registro en ven_devolucion
            Devolucion dev = new Devolucion();
            dev.setIdVenta(idVenta);
            dev.setIdProducto(idProducto);
            dev.setCantidad(cantidad);
            dev.setMontoDevuelto(monto);
            dev.setMotivo(motivo);

            devolucionDAO.registrarDevolucion(dev);

            // 2. Reingresar producto al inventario fisico (Tipo ENTRADA)
            List<Stock> stocks = inventarioFacade.listarStockPorProducto(idProducto);
            if (!stocks.isEmpty()) {
                Stock stockPrincipal = stocks.get(0);

                MovimientoInventario mov = new MovimientoInventario();
                mov.setIdStock(stockPrincipal.getIdStock());
                mov.setTipo("ENTRADA");
                mov.setCantidad(cantidad);
                mov.setReferencia("Devolucion Venta: " + idVenta);
                // No mandamos ID de empleado para evitar error fk_mov_emp

                inventarioFacade.registrarMovimiento(mov);
            }

            AlertUtil.info("Exito", "Devolucion registrada y producto devuelto al inventario.");
            cerrarVentana();

        } catch (Exception e) {
            AlertUtil.error("Error", "Error al procesar la devolucion: " + e.getMessage());
        }
    }

    @FXML
    public void onCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtIdVenta.getScene().getWindow();
        stage.close();
    }
}