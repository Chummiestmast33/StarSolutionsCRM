package com.starsolutions.starsolutionscrm.dao.interfaces;

/**
 * Enumeración que define los motivos por los que no se puede eliminar un registro
 * (política Opción A: baja lógica por defecto, borrado físico bloqueado si hay dependencias).
 */
public enum MotivoBloqueoEliminacion {
    NO_EXISTE("El registro no existe en la base de datos"),
    TIENE_STOCK("No se puede eliminar: existen registros de stock asociados"),
    TIENE_MOVIMIENTOS("No se puede eliminar: existen movimientos de inventario asociados"),
    TIENE_PRODUCCION("No se puede eliminar: está utilizándose en órdenes de producción"),
    ERROR_INTEGRIDAD("Error de integridad: verifique las dependencias de la base de datos");

    private final String mensajeFuncional;

    MotivoBloqueoEliminacion(String mensajeFuncional) {
        this.mensajeFuncional = mensajeFuncional;
    }

    public String getMensajeFuncional() {
        return mensajeFuncional;
    }
}

