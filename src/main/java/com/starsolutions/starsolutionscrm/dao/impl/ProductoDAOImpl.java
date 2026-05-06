package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.IProductoDAO;
import com.starsolutions.starsolutionscrm.dao.interfaces.MotivoBloqueoEliminacion;
import com.starsolutions.starsolutionscrm.dao.interfaces.ResultadoEliminacion;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.inventario.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements IProductoDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }


    @Override
    public List<Producto> buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id_producto, nombre, descripcion, precio_unitario, id_categoria, activo " +
                "FROM inv_producto " +
                "WHERE activo = 1 AND nombre LIKE ? " +
                "ORDER BY nombre";

        List<Producto> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }


    @Override
    public List<Producto> buscarPorCategoria(int idCategoria) throws SQLException {
        String sql = "SELECT id_producto, nombre, descripcion, precio_unitario, id_categoria, activo " +
                "FROM inv_producto " +
                "WHERE activo = 1 AND id_categoria = ? " +
                "ORDER BY nombre";

        List<Producto> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idCategoria);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }


    @Override
    public boolean alta(Producto producto) throws SQLException {
        String sql = "INSERT INTO inv_producto (nombre, descripcion, precio_unitario, id_categoria, activo) " +
                "VALUES (?, ?, ?, ?, 1)";

        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getDescripcion());
            ps.setBigDecimal(3, producto.getPrecioUnitario());
            ps.setInt(4, producto.getIdCategoria());

            int filas = ps.executeUpdate();
            if (filas == 0) return false;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    producto.setIdProducto(keys.getInt(1));
                }
            }
        }
        return true;
    }

  @Override
  public boolean actualizar(Producto producto) throws SQLException {
    String sql = "UPDATE inv_producto SET nombre = ?, descripcion = ?, precio_unitario = ?, id_categoria = ?, activo = ? WHERE id_producto = ?";

    try (PreparedStatement ps = getConn().prepareStatement(sql)) {
      ps.setString(1, producto.getNombre());
      ps.setString(2, producto.getDescripcion());
      ps.setBigDecimal(3, producto.getPrecioUnitario());
      ps.setInt(4, producto.getIdCategoria());
      ps.setBoolean(5, producto.isActivo());
      ps.setInt(6, producto.getIdProducto());
      return ps.executeUpdate() > 0;
    }
  }


    @Override
    public boolean desactivar(int idProducto) throws SQLException {
        String sql = "UPDATE inv_producto SET activo = 0 WHERE id_producto = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;
        }
    }


    @Override
    public ResultadoEliminacion eliminar(int idProducto) throws SQLException {
        // Paso 1: Verificar que el producto existe
        String sqlExiste = "SELECT id_producto FROM inv_producto WHERE id_producto = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sqlExiste)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return ResultadoEliminacion.bloqueado(MotivoBloqueoEliminacion.NO_EXISTE);
                }
            }
        }

        // Paso 2: Verificar que no hay registros en inv_stock
        String sqlStock = "SELECT COUNT(*) FROM inv_stock WHERE id_producto = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sqlStock)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return ResultadoEliminacion.bloqueado(MotivoBloqueoEliminacion.TIENE_STOCK);
                }
            }
        }

        // Paso 3: Verificar que no hay movimientos (a través de inv_stock)
        String sqlMovimientos = "SELECT COUNT(*) FROM inv_movimiento m " +
                "WHERE m.id_stock IN (SELECT id_stock FROM inv_stock WHERE id_producto = ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sqlMovimientos)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return ResultadoEliminacion.bloqueado(MotivoBloqueoEliminacion.TIENE_MOVIMIENTOS);
                }
            }
        }

        // Paso 4: Verificar que no está en producción
        String sqlProduccion = "SELECT COUNT(*) FROM prd_orden_detalle WHERE id_producto = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sqlProduccion)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return ResultadoEliminacion.bloqueado(MotivoBloqueoEliminacion.TIENE_PRODUCCION);
                }
            }
        }

        // Paso 5: Proceder a DELETE si todas las validaciones pasaron
        String sqlDelete = "DELETE FROM inv_producto WHERE id_producto = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sqlDelete)) {
            ps.setInt(1, idProducto);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                return new ResultadoEliminacion();
            } else {
                return ResultadoEliminacion.bloqueado(MotivoBloqueoEliminacion.ERROR_INTEGRIDAD);
            }
        }
    }


    @Override
    public Producto obtenerPorId(int idProducto) throws SQLException {
        String sql = "SELECT id_producto, nombre, descripcion, precio_unitario, id_categoria, activo " +
                "FROM inv_producto WHERE id_producto = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Producto> listarActivos() throws SQLException {
        String sql = "SELECT id_producto, nombre, descripcion, precio_unitario, id_categoria, activo " +
                "FROM inv_producto WHERE activo = 1 ORDER BY nombre";

        List<Producto> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setIdProducto(rs.getInt("id_producto"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        p.setIdCategoria(rs.getInt("id_categoria"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    }
}

