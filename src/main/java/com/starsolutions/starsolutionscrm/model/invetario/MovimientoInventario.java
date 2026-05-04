package com.starsolutions.starsolutionscrm.model.invetario;

import java.time.LocalDate;
import java.util.Objects;

public class MovimientoInventario {
	private Integer idMovimiento;
	private Integer idStock;
	private Integer idEmpleadoInventario;
	private String tipo;
	private int cantidad;
	private LocalDate fecha;
	private String referencia;

	public MovimientoInventario() {
	}

	public MovimientoInventario(Integer idMovimiento, Integer idStock, Integer idEmpleadoInventario, String tipo, int cantidad, LocalDate fecha, String referencia) {
		this.idMovimiento = idMovimiento;
		this.idStock = idStock;
		this.idEmpleadoInventario = idEmpleadoInventario;
		this.tipo = tipo;
		this.cantidad = cantidad;
		this.fecha = fecha;
		this.referencia = referencia;
	}

	public Integer getIdMovimiento() {
		return idMovimiento;
	}

	public void setIdMovimiento(Integer idMovimiento) {
		this.idMovimiento = idMovimiento;
	}

	public Integer getIdStock() {
		return idStock;
	}

	public void setIdStock(Integer idStock) {
		this.idStock = idStock;
	}

	public Integer getIdEmpleadoInventario() {
		return idEmpleadoInventario;
	}

	public void setIdEmpleadoInventario(Integer idEmpleadoInventario) {
		this.idEmpleadoInventario = idEmpleadoInventario;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	@Override
	public String toString() {
		return "MovimientoInventario{" +
				"idMovimiento=" + idMovimiento +
				", idStock=" + idStock +
				", idEmpleadoInventario=" + idEmpleadoInventario +
				", tipo='" + tipo + '\'' +
				", cantidad=" + cantidad +
				", fecha=" + fecha +
				", referencia='" + referencia + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MovimientoInventario that = (MovimientoInventario) o;
		return Objects.equals(idMovimiento, that.idMovimiento);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idMovimiento);
	}
}
