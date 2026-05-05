package com.starsolutions.starsolutionscrm.dao.interfaces;

import com.starsolutions.starsolutionscrm.model.inventario.MateriaPrima;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO para operaciones CRUD sobre MateriaPrima (tabla inv_materia_prima).
 * Política Opción A: baja lógica (activo=0) por defecto, borrado físico condicionado por dependencias.
 */
public interface IMateriaPrimaDAO {

    /**
     * Busca materias primas activas por nombre (búsqueda parcial, LIKE).
     * @param nombre Cadena de búsqueda (case-insensitive si DB lo permite)
     * @return Lista de MateriaPrima activas cuyo nombre contiene la cadena; lista vacía si no hay coincidencias
     */
    List<MateriaPrima> buscarPorNombre(String nombre) throws SQLException;

    /**
     * Crea una nueva materia prima en inv_materia_prima.
     * @param materiaPrima Objeto MateriaPrima (sin idMateria aún)
     * @return true si la inserción fue exitosa, false si falló; asigna idMateria generado
     */
    boolean alta(MateriaPrima materiaPrima) throws SQLException;

    /**
     * Desactiva una materia prima (baja lógica). Setea activo=0, no elimina registro.
     * @param idMateria ID de la materia prima a desactivar
     * @return true si la actualización fue exitosa, false si no existe
     */
    boolean desactivar(int idMateria) throws SQLException;

    /**
     * Intenta eliminar una materia prima (borrado físico).
     * Opción A: retorna bloqueado si existen stock_mp, movimientos_mp o relaciones a producción.
     * @param idMateria ID de la materia prima a eliminar
     * @return ResultadoEliminacion con estado (eliminado sí/no) y causa de bloqueo
     */
    ResultadoEliminacion eliminar(int idMateria) throws SQLException;

    /**
     * Obtiene una materia prima por su ID (incluyendo inactivas).
     * @param idMateria ID de la materia prima
     * @return MateriaPrima si existe, null si no
     */
    MateriaPrima obtenerPorId(int idMateria) throws SQLException;

    /**
     * Lista todas las materias primas activas ordenadas por nombre.
     * @return Lista de MateriaPrima activas
     */
    List<MateriaPrima> listarActivas() throws SQLException;
}

