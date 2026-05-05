package com.starsolutions.starsolutionscrm.model.inventario;

import java.math.BigDecimal;
import java.util.Objects;

public class Producto {
	private Integer idProducto;
	private String nombre;
	private String descripcion;
	private BigDecimal precioUnitario;
	private Integer idCategoria;
	private boolean activo;

	public Producto() {
	}

	public Producto(Integer idProducto, String nombre, String descripcion, BigDecimal precioUnitario, Integer idCategoria, boolean activo) {
		this.idProducto = idProducto;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.precioUnitario = precioUnitario;
		this.idCategoria = idCategoria;
		this.activo = activo;
	}

	public Integer getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(Integer idProducto) {
		this.idProducto = idProducto;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	public Integer getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	@Override
	public String toString() {
		return "Producto{" +
				"idProducto=" + idProducto +
				", nombre='" + nombre + '\'' +
				", descripcion='" + descripcion + '\'' +
				", precioUnitario=" + precioUnitario +
				", idCategoria=" + idCategoria +
				", activo=" + activo +
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
		Producto producto = (Producto) o;
		return Objects.equals(idProducto, producto.idProducto);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idProducto);
	}
}
