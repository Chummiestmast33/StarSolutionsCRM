package com.starsolutions.starsolutionscrm.model.compras;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrdenCompra {

	public static final String ESTADO_PENDIENTE = "Pendiente";
	public static final String ESTADO_RECIBIDO_PARCIAL = "Recibido Parcial";
	public static final String ESTADO_RECIBIDO = "Recibido";
	public static final String ESTADO_PAGADO = "Pagado";
	public static final String ESTADO_CANCELADO = "Cancelado";

	private Integer idOrden;
	private Integer idProveedor;
	private Integer idEmpleado;
	private LocalDateTime fecha;
	private String estado;
	private BigDecimal total;
	private final List<DetalleOrdenCompra> detalles = new ArrayList<>();

	public OrdenCompra() {
		this.estado = ESTADO_PENDIENTE;
		this.total = BigDecimal.ZERO;
	}

	public Integer getIdOrden() {
		return idOrden;
	}

	public void setIdOrden(Integer idOrden) {
		this.idOrden = idOrden;
	}

	public Integer getIdProveedor() {
		return idProveedor;
	}

	public void setIdProveedor(Integer idProveedor) {
		this.idProveedor = idProveedor;
	}

	public Integer getIdEmpleado() {
		return idEmpleado;
	}

	public void setIdEmpleado(Integer idEmpleado) {
		this.idEmpleado = idEmpleado;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public List<DetalleOrdenCompra> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<DetalleOrdenCompra> nuevosDetalles) {
		detalles.clear();
		if (nuevosDetalles != null) {
			detalles.addAll(nuevosDetalles);
		}
	}

	public void agregarDetalle(DetalleOrdenCompra detalle) {
		if (detalle != null) {
			detalles.add(detalle);
		}
	}

	public BigDecimal calcularTotal() {
		BigDecimal acumulado = BigDecimal.ZERO;
		for (DetalleOrdenCompra detalle : detalles) {
			acumulado = acumulado.add(detalle.getSubtotal());
		}
		this.total = acumulado;
		return acumulado;
	}

	public boolean estaCerrada() {
		return ESTADO_RECIBIDO.equalsIgnoreCase(estado) || ESTADO_PAGADO.equalsIgnoreCase(estado);
	}

	@Override
	public String toString() {
		return "OrdenCompra{" +
				"idOrden=" + idOrden +
				", idProveedor=" + idProveedor +
				", idEmpleado=" + idEmpleado +
				", fecha=" + fecha +
				", estado='" + estado + '\'' +
				", total=" + total +
				", detalles=" + detalles +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OrdenCompra that = (OrdenCompra) o;
		return Objects.equals(idOrden, that.idOrden);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idOrden);
	}
}
