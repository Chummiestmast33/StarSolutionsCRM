package com.starsolutions.starsolutionscrm.controller.inventario;

import com.starsolutions.starsolutionscrm.facade.InventarioFacade;
import com.starsolutions.starsolutionscrm.model.inventario.Producto;
import com.starsolutions.starsolutionscrm.util.AlertUtil;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class ProductoFormController {

	@FXML private TextField txtId;
	@FXML private TextField txtNombre;
	@FXML private TextField txtDescripcion;
	@FXML private TextField txtPrecio;
	@FXML private TextField txtCategoria;
	@FXML private CheckBox chkActivo;

	private final InventarioFacade facade = new InventarioFacade();
	private Producto producto;
	private Runnable onGuardado;

	@FXML
	public void initialize() {
		if (chkActivo != null) {
			chkActivo.setSelected(true);
		}
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
		if (producto != null) {
			txtId.setText(String.valueOf(producto.getIdProducto()));
			txtNombre.setText(producto.getNombre());
			txtDescripcion.setText(producto.getDescripcion());
			txtPrecio.setText(producto.getPrecioUnitario() != null ? producto.getPrecioUnitario().toPlainString() : "");
			txtCategoria.setText(producto.getIdCategoria() != null ? String.valueOf(producto.getIdCategoria()) : "");
			chkActivo.setSelected(producto.isActivo());
		}
	}

	public void setOnGuardado(Runnable onGuardado) {
		this.onGuardado = onGuardado;
	}

	@FXML
	public void onGuardar() {
		try {
			String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
			String descripcion = txtDescripcion.getText() == null ? "" : txtDescripcion.getText().trim();
			String precioTxt = txtPrecio.getText() == null ? "" : txtPrecio.getText().trim();
			String categoriaTxt = txtCategoria.getText() == null ? "" : txtCategoria.getText().trim();

			if (nombre.isEmpty() || precioTxt.isEmpty() || categoriaTxt.isEmpty()) {
				AlertUtil.error("Validación", "Nombre, precio y categoría son obligatorios.");
				return;
			}

			Producto p = producto != null ? producto : new Producto();
			p.setNombre(nombre);
			p.setDescripcion(descripcion);
			p.setPrecioUnitario(new BigDecimal(precioTxt));
			p.setIdCategoria(Integer.parseInt(categoriaTxt));
			p.setActivo(chkActivo == null || chkActivo.isSelected());

			boolean ok;
			if (p.getIdProducto() == null) {
				ok = facade.altaProducto(p);
			} else {
				ok = facade.actualizarProducto(p);
			}

			if (ok) {
				AlertUtil.info("Éxito", "Producto guardado correctamente.");
				if (onGuardado != null) {
					onGuardado.run();
				}
				cerrar();
			}
		} catch (NumberFormatException e) {
			AlertUtil.error("Validación", "Precio y categoría deben ser numéricos.");
		} catch (Exception e) {
			AlertUtil.error("Error", "No se pudo guardar el producto: " + e.getMessage());
		}
	}

	@FXML
	public void onCancelar() {
		cerrar();
	}

	private void cerrar() {
		Stage stage = (Stage) txtNombre.getScene().getWindow();
		stage.close();
	}
}
