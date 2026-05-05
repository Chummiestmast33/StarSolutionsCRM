package com.starsolutions.starsolutionscrm.model.inventario;

import java.util.Objects;

public class StockMateriaPrima {
	private Integer idStockMateriaPrima;
	private Integer idMateria;
	private int cantidadActual;
	private int stockMinimo;
	private String ubicacion;

	public StockMateriaPrima() {
	}

	public StockMateriaPrima(Integer idStockMateriaPrima, Integer idMateria, int cantidadActual, int stockMinimo, String ubicacion) {
		this.idStockMateriaPrima = idStockMateriaPrima;
		this.idMateria = idMateria;
		this.cantidadActual = cantidadActual;
		this.stockMinimo = stockMinimo;
		this.ubicacion = ubicacion;
	}

	public Integer getIdStockMateriaPrima() {
		return idStockMateriaPrima;
	}

	public void setIdStockMateriaPrima(Integer idStockMateriaPrima) {
		this.idStockMateriaPrima = idStockMateriaPrima;
	}

	public Integer getIdMateria() {
		return idMateria;
	}

	public void setIdMateria(Integer idMateria) {
		this.idMateria = idMateria;
	}

	public int getCantidadActual() {
		return cantidadActual;
	}

	public void setCantidadActual(int cantidadActual) {
		this.cantidadActual = cantidadActual;
	}

	public int getStockMinimo() {
		return stockMinimo;
	}

	public void setStockMinimo(int stockMinimo) {
		this.stockMinimo = stockMinimo;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	@Override
	public String toString() {
		return "StockMateriaPrima{" +
				"idStockMateriaPrima=" + idStockMateriaPrima +
				", idMateria=" + idMateria +
				", cantidadActual=" + cantidadActual +
				", stockMinimo=" + stockMinimo +
				", ubicacion='" + ubicacion + '\'' +
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
		StockMateriaPrima that = (StockMateriaPrima) o;
		return Objects.equals(idStockMateriaPrima, that.idStockMateriaPrima);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idStockMateriaPrima);
	}
}
