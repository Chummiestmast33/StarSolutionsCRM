package com.starsolutions.starsolutionscrm.model.inventario;

import java.util.Objects;

public class MateriaPrima {
	private Integer idMateria;
	private String nombre;
	private String unidad;
	private String descripcion;
	private boolean activo;

	public MateriaPrima() {
	}

	public MateriaPrima(Integer idMateria, String nombre, String unidad, String descripcion, boolean activo) {
		this.idMateria = idMateria;
		this.nombre = nombre;
		this.unidad = unidad;
		this.descripcion = descripcion;
		this.activo = activo;
	}

	public Integer getIdMateria() {
		return idMateria;
	}

	public void setIdMateria(Integer idMateria) {
		this.idMateria = idMateria;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getUnidad() {
		return unidad;
	}

	public void setUnidad(String unidad) {
		this.unidad = unidad;
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
		return "MateriaPrima{" +
				"idMateria=" + idMateria +
				", nombre='" + nombre + '\'' +
				", unidad='" + unidad + '\'' +
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
		MateriaPrima that = (MateriaPrima) o;
		return Objects.equals(idMateria, that.idMateria);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idMateria);
	}
}
