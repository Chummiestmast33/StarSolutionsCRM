package com.starsolutions.starsolutionscrm.dao.interfaces;

import com.starsolutions.starsolutionscrm.model.inventario.Producto;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO para operaciones CRUD sobre Producto (tabla inv_producto).
 * Política Opción A: baja lógica (activo=0) por defecto, borrado físico condicionado por dependencias.
 */
public interface IProductoDAO {

    /**
     * Busca productos activos por nombre (búsqueda parcial, LIKE).
     * @param nombre Cadena de búsqueda (case-insensitive si DB lo permite)
     * @return Lista de Producto activos cuyo nombre contiene la cadena; lista vacía si no hay coincidencias
     */
    List<Producto> buscarPorNombre(String nombre) throws SQLException;

    /**
     * Busca productos activos por categoría.
     * @param idCategoria ID de la categoría
     * @return Lista de Producto activos de esa categoría; lista vacía si no hay
     */
    List<Producto> buscarPorCategoria(int idCategoria) throws SQLException;

    /**
     * Crea un nuevo producto en inv_producto.
     * @param producto Objeto Producto (sin idProducto aún)
     * @return true si la inserción fue exitosa, false si falló; asigna idProducto generado
     */
    boolean alta(Producto producto) throws SQLException;

    /**
     * Desactiva un producto (baja lógica). Setea activo=0, no elimina registro.
     * @param idProducto ID del producto a desactivar
     * @return true si la actualización fue exitosa, false si el producto no existe
     */
    boolean desactivar(int idProducto) throws SQLException;

    /**
     * Intenta eliminar un producto (borrado físico).
     * Opción A: retorna bloqueado si existen stock, movimientos o relaciones a producción.
     * @param idProducto ID del producto a eliminar
     * @return ResultadoEliminacion con estado (eliminado sí/no) y causa de bloqueo
     */
    ResultadoEliminacion eliminar(int idProducto) throws SQLException;

    /**
     * Obtiene un producto por su ID (incluyendo inactivos).
     * @param idProducto ID del producto
     * @return Producto si existe, null si no
     */
    Producto obtenerPorId(int idProducto) throws SQLException;

    /**
     * Lista todos los productos activos ordenados por nombre.
     * @return Lista de Producto activos
     */
    List<Producto> listarActivos() throws SQLException;
}
