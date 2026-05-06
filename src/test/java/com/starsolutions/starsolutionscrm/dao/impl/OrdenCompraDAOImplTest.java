package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.compras.DetalleOrdenCompra;
import com.starsolutions.starsolutionscrm.model.compras.OrdenCompra;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrdenCompraDAOImplTest {

    private static OrdenCompraDAOImpl dao;
    private static int idCategoria;
    private static int idProducto;
    private static int idProveedor;
    private static int idEmpleadoInventario;

    @BeforeAll
    static void setUpClass() throws Exception {
        H2TestHelper.init("test_orden_compra_dao");
        dao = new OrdenCompraDAOImpl();

        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (var stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO cat_categoria_producto (nombre, activo) VALUES ('Categoria OC', 1)");
        }

        try (var rs = conn.createStatement().executeQuery("SELECT id_categoria FROM cat_categoria_producto LIMIT 1")) {
            assertTrue(rs.next());
            idCategoria = rs.getInt(1);
        }

        try (var ps = conn.prepareStatement(
                "INSERT INTO inv_producto (nombre, descripcion, precio_unitario, id_categoria, activo) VALUES (?, ?, ?, ?, 1)",
                java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Producto OC");
            ps.setString(2, "Producto para compras");
            ps.setBigDecimal(3, new BigDecimal("12.50"));
            ps.setInt(4, idCategoria);
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                assertTrue(keys.next());
                idProducto = keys.getInt(1);
            }
        }

        try (var ps = conn.prepareStatement(
                "INSERT INTO crm_proveedor (nombre, rfc, direccion, activo) VALUES (?, ?, ?, 1)",
                java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Proveedor OC");
            ps.setString(2, "RFC123");
            ps.setString(3, "Direccion OC");
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                assertTrue(keys.next());
                idProveedor = keys.getInt(1);
            }
        }

        try (var ps = conn.prepareStatement(
                "INSERT INTO rh_empleado (nombre, contrasena, tipo_empleado, activo) VALUES (?, ?, 'Inventario', 1)",
                java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Empleado Inventario");
            ps.setString(2, "pass");
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                assertTrue(keys.next());
                idEmpleadoInventario = keys.getInt(1);
            }
        }

        try (var ps = conn.prepareStatement("INSERT INTO rh_empleado_inventario (num) VALUES (?)")) {
            ps.setInt(1, idEmpleadoInventario);
            ps.executeUpdate();
        }
    }

    @AfterAll
    static void tearDownClass() throws Exception {
        H2TestHelper.dropSchema();
    }

    @BeforeEach
    void limpiarDatos() throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM cmp_detalle_orden");
            stmt.execute("DELETE FROM cmp_orden_compra");
            stmt.execute("DELETE FROM inv_movimiento");
            stmt.execute("DELETE FROM inv_stock");
        }
    }

    @Test
    void altaYRecepcionParcialDeberianActualizarStockYEstado() throws Exception {
        OrdenCompra orden = new OrdenCompra();
        orden.setIdProveedor(idProveedor);
        orden.setIdEmpleado(idEmpleadoInventario);
        orden.setDetalles(List.of(detalle(idProducto, 10, new BigDecimal("12.50"))));

        assertTrue(dao.alta(orden));
        assertNotNull(orden.getIdOrden());

        OrdenCompra cargada = dao.obtenerPorId(orden.getIdOrden());
        assertNotNull(cargada);
        assertEquals(OrdenCompra.ESTADO_PENDIENTE, cargada.getEstado());
        assertEquals(1, cargada.getDetalles().size());
        assertEquals(new BigDecimal("125.00"), cargada.getTotal());

        Map<Integer, Integer> recepcion1 = new HashMap<>();
        recepcion1.put(idProducto, 4);
        assertTrue(dao.registrarRecepcion(orden.getIdOrden(), recepcion1, idEmpleadoInventario, "REC-1"));

        assertEquals(4, cantidadActualStock(idProducto));
        assertEquals(4, cantidadRecibidaOrden(orden.getIdOrden(), idProducto));
        assertEquals(OrdenCompra.ESTADO_RECIBIDO_PARCIAL, dao.obtenerPorId(orden.getIdOrden()).getEstado());

        Map<Integer, Integer> recepcion2 = new HashMap<>();
        recepcion2.put(idProducto, 6);
        assertTrue(dao.registrarRecepcion(orden.getIdOrden(), recepcion2, idEmpleadoInventario, "REC-2"));

        assertEquals(10, cantidadActualStock(idProducto));
        assertEquals(10, cantidadRecibidaOrden(orden.getIdOrden(), idProducto));
        assertEquals(OrdenCompra.ESTADO_RECIBIDO, dao.obtenerPorId(orden.getIdOrden()).getEstado());
        assertTrue(triggerExiste("TRG_ACTUALIZA_STOCK"));
    }

    private DetalleOrdenCompra detalle(int idProducto, int cantidad, BigDecimal precio) {
        DetalleOrdenCompra detalle = new DetalleOrdenCompra();
        detalle.setIdProducto(idProducto);
        detalle.setCantidadPedida(cantidad);
        detalle.setPrecioUnitario(precio);
        return detalle;
    }

    private int cantidadActualStock(int idProducto) throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (var ps = conn.prepareStatement("SELECT cantidad_actual FROM inv_stock WHERE id_producto = ?")) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                return rs.getInt(1);
            }
        }
    }

    private int cantidadRecibidaOrden(int idOrden, int idProducto) throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (var ps = conn.prepareStatement("SELECT cantidad_recibida FROM cmp_detalle_orden WHERE id_orden = ? AND id_producto = ?")) {
            ps.setInt(1, idOrden);
            ps.setInt(2, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                return rs.getInt(1);
            }
        }
    }

    private boolean triggerExiste(String triggerName) throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (var ps = conn.prepareStatement("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TRIGGERS WHERE TRIGGER_NAME = ?")) {
            ps.setString(1, triggerName);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                return rs.getInt(1) > 0;
            }
        }
    }
}

