package com.starsolutions.starsolutionscrm.model.ventas;

import java.math.BigDecimal;

public class Promocion {
    private int idPromocion;
    private BigDecimal porcentajeDesc;

    public Promocion(int idPromocion, BigDecimal porcentajeDesc) {
        this.idPromocion = idPromocion;
        this.porcentajeDesc = porcentajeDesc;
    }

    public int getIdPromocion() { return idPromocion; }
    public BigDecimal getPorcentajeDesc() { return porcentajeDesc; }
}