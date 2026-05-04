package com.starsolutions.starsolutionscrm.model.invetario;

import java.util.Objects;

public class CategoriaProducto {
	private Integer idCategoria;
	private String nombre;
	private String descripcion;
	private boolean activo;

	public CategoriaProducto() {
	}

	public CategoriaProducto(Integer idCategoria, String nombre, String descripcion, boolean activo) {
		this.idCategoria = idCategoria;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.activo = activo;
	}

	public Integer getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
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

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	@Override
	public String toString() {
		return "CategoriaProducto{" +
				"idCategoria=" + idCategoria +
				", nombre='" + nombre + '\'' +
				", descripcion='" + descripcion + '\'' +
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
		CategoriaProducto that = (CategoriaProducto) o;
		return Objects.equals(idCategoria, that.idCategoria);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idCategoria);
	}
}
