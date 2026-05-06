package com.starsolutions.starsolutionscrm.model.compras;

import java.math.BigDecimal;
import java.util.Objects;

public class DetalleOrdenCompra {

	private Integer idOrden;
	private Integer idProducto;
	private int cantidadPedida;
	private int cantidadRecibida;
	private BigDecimal precioUnitario;

	public DetalleOrdenCompra() {
		this.precioUnitario = BigDecimal.ZERO;
	}

	public Integer getIdOrden() {
		return idOrden;
	}

	public void setIdOrden(Integer idOrden) {
		this.idOrden = idOrden;
	}

	public Integer getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(Integer idProducto) {
		this.idProducto = idProducto;
	}

	public int getCantidadPedida() {
		return cantidadPedida;
	}

	public void setCantidadPedida(int cantidadPedida) {
		this.cantidadPedida = cantidadPedida;
	}

	public int getCantidadRecibida() {
		return cantidadRecibida;
	}

	public void setCantidadRecibida(int cantidadRecibida) {
		this.cantidadRecibida = cantidadRecibida;
	}

	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario != null ? precioUnitario : BigDecimal.ZERO;
	}

	public int getCantidadPendiente() {
		return Math.max(cantidadPedida - cantidadRecibida, 0);
	}

	public BigDecimal getSubtotal() {
		return precioUnitario.multiply(BigDecimal.valueOf(cantidadPedida));
	}

	public boolean estaCompleto() {
		return cantidadRecibida >= cantidadPedida;
	}

	@Override
	public String toString() {
		return "DetalleOrdenCompra{" +
				"idOrden=" + idOrden +
				", idProducto=" + idProducto +
				", cantidadPedida=" + cantidadPedida +
				", cantidadRecibida=" + cantidadRecibida +
				", precioUnitario=" + precioUnitario +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DetalleOrdenCompra that = (DetalleOrdenCompra) o;
		return Objects.equals(idOrden, that.idOrden) && Objects.equals(idProducto, that.idProducto);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idOrden, idProducto);
	}
}
