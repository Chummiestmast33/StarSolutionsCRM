package com.starsolutions.starsolutionscrm.model.produccion;

public class OrdenProduccionDetalle {

    private int idDetProd;
    private int idOrdenProd;      // FK → prd_orden_produccion
    private int idMateria;        // FK → inv_materia_prima
    private int idProducto;       // FK → inv_producto
    private int cantidadMpUsada;  // materia prima consumida
    private int cantidadProducida;

    // Constructor vacío
    public OrdenProduccionDetalle() {}

    // Constructor completo
    public OrdenProduccionDetalle(int idDetProd, int idOrdenProd,
                                  int idMateria, int idProducto,
                                  int cantidadMpUsada, int cantidadProducida) {
        this.idDetProd         = idDetProd;
        this.idOrdenProd       = idOrdenProd;
        this.idMateria         = idMateria;
        this.idProducto        = idProducto;
        this.cantidadMpUsada   = cantidadMpUsada;
        this.cantidadProducida = cantidadProducida;
    }

    // Getters
    public int getIdDetProd()         { return idDetProd; }
    public int getIdOrdenProd()       { return idOrdenProd; }
    public int getIdMateria()         { return idMateria; }
    public int getIdProducto()        { return idProducto; }
    public int getCantidadMpUsada()   { return cantidadMpUsada; }
    public int getCantidadProducida() { return cantidadProducida; }

    // Setters
    public void setIdDetProd(int idDetProd)                 { this.idDetProd         = idDetProd; }
    public void setIdOrdenProd(int idOrdenProd)             { this.idOrdenProd       = idOrdenProd; }
    public void setIdMateria(int idMateria)                 { this.idMateria         = idMateria; }
    public void setIdProducto(int idProducto)               { this.idProducto        = idProducto; }
    public void setCantidadMpUsada(int cantidadMpUsada)     { this.cantidadMpUsada   = cantidadMpUsada; }
    public void setCantidadProducida(int cantidadProducida) { this.cantidadProducida = cantidadProducida; }

    @Override
    public String toString() {
        return "OrdenProduccionDetalle{id=" + idDetProd +
                ", orden=" + idOrdenProd +
                ", materia=" + idMateria +
                ", producto=" + idProducto +
                ", mpUsada=" + cantidadMpUsada +
                ", producida=" + cantidadProducida + "}";
    }
}