package com.starsolutions.starsolutionscrm.model.inventario;

import java.util.Objects;

public class Stock {
	private Integer idStock;
	private Integer idProducto;
	private int cantidadActual;
	private int stockMinimo;
	private Integer stockMaximo;
	private String ubicacion;

	public Stock() {
	}

	public Stock(Integer idStock, Integer idProducto, int cantidadActual, int stockMinimo, Integer stockMaximo, String ubicacion) {
		this.idStock = idStock;
		this.idProducto = idProducto;
		this.cantidadActual = cantidadActual;
		this.stockMinimo = stockMinimo;
		this.stockMaximo = stockMaximo;
		this.ubicacion = ubicacion;
	}

	public Integer getIdStock() {
		return idStock;
	}

	public void setIdStock(Integer idStock) {
		this.idStock = idStock;
	}

	public Integer getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(Integer idProducto) {
		this.idProducto = idProducto;
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

	public Integer getStockMaximo() {
		return stockMaximo;
	}

	public void setStockMaximo(Integer stockMaximo) {
		this.stockMaximo = stockMaximo;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	@Override
	public String toString() {
		return "Stock{" +
				"idStock=" + idStock +
				", idProducto=" + idProducto +
				", cantidadActual=" + cantidadActual +
				", stockMinimo=" + stockMinimo +
				", stockMaximo=" + stockMaximo +
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
		Stock stock = (Stock) o;
		return Objects.equals(idStock, stock.idStock);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idStock);
	}
}
