package com.starsolutions.starsolutionscrm.dao.interfaces;

/**
 * Resultado de una operación de eliminación (física).
 * Encapsula: éxito booleano, motivo de bloqueo si no se pudo eliminar y detalle funcional.
 * Política Opción A: baja lógica por defecto (desactivar), borrado físico solo si no hay dependencias.
 */
public class ResultadoEliminacion {
    private boolean eliminado;
    private MotivoBloqueoEliminacion motivo;
    private String detalle;

    public ResultadoEliminacion(boolean eliminado, MotivoBloqueoEliminacion motivo, String detalle) {
        this.eliminado = eliminado;
        this.motivo = motivo;
        this.detalle = detalle;
    }

    /**
     * Constructor para caso de éxito
     */
    public ResultadoEliminacion() {
        this.eliminado = true;
        this.motivo = null;
        this.detalle = "Registro eliminado correctamente";
    }

    /**
     * Constructor para caso de bloqueo
     */
    public static ResultadoEliminacion bloqueado(MotivoBloqueoEliminacion motivo) {
        return new ResultadoEliminacion(false, motivo, motivo.getMensajeFuncional());
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public MotivoBloqueoEliminacion getMotivo() {
        return motivo;
    }

    public void setMotivo(MotivoBloqueoEliminacion motivo) {
        this.motivo = motivo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    @Override
    public String toString() {
        return "ResultadoEliminacion{" +
                "eliminado=" + eliminado +
                ", motivo=" + motivo +
                ", detalle='" + detalle + '\'' +
                '}';
    }
}

