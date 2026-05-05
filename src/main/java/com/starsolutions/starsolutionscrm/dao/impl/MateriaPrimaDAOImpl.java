package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.IMateriaPrimaDAO;
import com.starsolutions.starsolutionscrm.dao.interfaces.MotivoBloqueoEliminacion;
import com.starsolutions.starsolutionscrm.dao.interfaces.ResultadoEliminacion;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.inventario.MateriaPrima;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MateriaPrimaDAOImpl implements IMateriaPrimaDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public List<MateriaPrima> buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id_materia, nombre, unidad, descripcion, activo FROM inv_materia_prima WHERE activo = 1 AND nombre LIKE ? ORDER BY nombre";
        List<MateriaPrima> lista = new ArrayList<>();

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
    public boolean alta(MateriaPrima materiaPrima) throws SQLException {
        String sql = "INSERT INTO inv_materia_prima (nombre, unidad, descripcion, activo) VALUES (?, ?, ?, 1)";

        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, materiaPrima.getNombre());
            ps.setString(2, materiaPrima.getUnidad());
            ps.setString(3, materiaPrima.getDescripcion());

            int filas = ps.executeUpdate();
            if (filas == 0) return false;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    materiaPrima.setIdMateria(keys.getInt(1));
                }
            }
        }
        return true;
    }

    @Override
    public boolean desactivar(int idMateria) throws SQLException {
        String sql = "UPDATE inv_materia_prima SET activo = 0 WHERE id_materia = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idMateria);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public ResultadoEliminacion eliminar(int idMateria) throws SQLException {
        String sqlExiste = "SELECT id_materia FROM inv_materia_prima WHERE id_materia = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sqlExiste)) {
            ps.setInt(1, idMateria);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return ResultadoEliminacion.bloqueado(MotivoBloqueoEliminacion.NO_EXISTE);
                }
            }
        }

        String sqlStockMP = "SELECT COUNT(*) FROM inv_stock_mp WHERE id_materia = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sqlStockMP)) {
            ps.setInt(1, idMateria);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return ResultadoEliminacion.bloqueado(MotivoBloqueoEliminacion.TIENE_STOCK);
                }
            }
        }

        String sqlMovimientosMP = "SELECT COUNT(*) FROM inv_movimiento_mp m WHERE m.id_stock_mp IN (SELECT id_stock_mp FROM inv_stock_mp WHERE id_materia = ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sqlMovimientosMP)) {
            ps.setInt(1, idMateria);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return ResultadoEliminacion.bloqueado(MotivoBloqueoEliminacion.TIENE_MOVIMIENTOS);
                }
            }
        }

        String sqlProduccion = "SELECT COUNT(*) FROM prd_orden_detalle WHERE id_materia = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sqlProduccion)) {
            ps.setInt(1, idMateria);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return ResultadoEliminacion.bloqueado(MotivoBloqueoEliminacion.TIENE_PRODUCCION);
                }
            }
        }

        String sqlDelete = "DELETE FROM inv_materia_prima WHERE id_materia = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sqlDelete)) {
            ps.setInt(1, idMateria);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                return new ResultadoEliminacion();
            } else {
                return ResultadoEliminacion.bloqueado(MotivoBloqueoEliminacion.ERROR_INTEGRIDAD);
            }
        }
    }

    @Override
    public MateriaPrima obtenerPorId(int idMateria) throws SQLException {
        String sql = "SELECT id_materia, nombre, unidad, descripcion, activo FROM inv_materia_prima WHERE id_materia = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idMateria);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<MateriaPrima> listarActivas() throws SQLException {
        String sql = "SELECT id_materia, nombre, unidad, descripcion, activo FROM inv_materia_prima WHERE activo = 1 ORDER BY nombre";
        List<MateriaPrima> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private MateriaPrima mapear(ResultSet rs) throws SQLException {
        MateriaPrima mp = new MateriaPrima();
        mp.setIdMateria(rs.getInt("id_materia"));
        mp.setNombre(rs.getString("nombre"));
        mp.setUnidad(rs.getString("unidad"));
        mp.setDescripcion(rs.getString("descripcion"));
        mp.setActivo(rs.getBoolean("activo"));
        return mp;
    }
}

