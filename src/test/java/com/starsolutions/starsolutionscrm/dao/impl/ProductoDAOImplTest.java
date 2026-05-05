package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.MotivoBloqueoEliminacion;
import com.starsolutions.starsolutionscrm.dao.interfaces.ResultadoEliminacion;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.inventario.Producto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoDAOImplTest {

    private static ProductoDAOImpl dao;
    private static int idCategoriaTest;

    @BeforeAll
    static void setUpClass() throws Exception {
        H2TestHelper.init("test_producto");
        dao = new ProductoDAOImpl();

        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (var s = conn.createStatement()) {
            s.execute("INSERT INTO cat_categoria_producto (nombre, activo) VALUES ('Test Category', 1)");
        }

        try (var s = conn.createStatement();
             var rs = s.executeQuery("SELECT id_categoria FROM cat_categoria_producto LIMIT 1")) {
            rs.next();
            idCategoriaTest = rs.getInt(1);
        }
    }

    @AfterAll
    static void tearDownClass() throws Exception {
        H2TestHelper.dropSchema();
    }

    @BeforeEach
    void limpiarProductos() throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (var s = conn.createStatement()) {
            s.execute("SET REFERENTIAL_INTEGRITY FALSE");
            s.execute("DELETE FROM inv_producto");
            s.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
    }

    // Test de alta

    @Test
    void altaProductoDeberiaRetornarTrue() throws Exception {
        Producto p = new Producto();
        p.setNombre("Producto Test");
        p.setDescripcion("Descripción test");
        p.setPrecioUnitario(new BigDecimal("100.00"));
        p.setIdCategoria(idCategoriaTest);

        boolean resultado = dao.alta(p);

        assertTrue(resultado);
        assertTrue(p.getIdProducto() > 0);
    }

    @Test
    void altaProductoDeberiaCrearActivo() throws Exception {
        Producto p = new Producto();
        p.setNombre("Producto Activo");
        p.setDescripcion("Desc");
        p.setPrecioUnitario(new BigDecimal("50.00"));
        p.setIdCategoria(idCategoriaTest);

        dao.alta(p);

        Producto recuperado = dao.obtenerPorId(p.getIdProducto());
        assertTrue(recuperado.isActivo());
    }

    // Tests de busqueda por nombre

    @Test
    void buscarPorNombreDeberiaEncontrarProductosActivos() throws Exception {
        Producto p1 = crearProducto("Pantalón Azul", "100.00");
        Producto p2 = crearProducto("Camisa Roja", "80.00");
        dao.desactivar(p2.getIdProducto());

        List<Producto> resultados = dao.buscarPorNombre("antalón");

        assertEquals(1, resultados.size());
        assertEquals("Pantalón Azul", resultados.get(0).getNombre());
    }

    @Test
    void buscarPorNombreSinResultadosDeberiaRetornarListaVacia() throws Exception {
        crearProducto("Producto Existente", "100.00");

        List<Producto> resultados = dao.buscarPorNombre("NoExiste");

        assertTrue(resultados.isEmpty());
    }

    // Test de busqueda por categoria

    @Test
    void buscarPorCategoriaDeberiaEncontrarProductosDeEsaCategoria() throws Exception {
        Producto p = crearProducto("Producto Cat Test", "150.00");

        List<Producto> resultados = dao.buscarPorCategoria(idCategoriaTest);

        assertEquals(1, resultados.size());
        assertEquals(idCategoriaTest, resultados.get(0).getIdCategoria());
    }

    // Test desactivar

    @Test
    void desactivarProductoDeberiaPonerActivoEnFalso() throws Exception {
        Producto p = crearProducto("Producto a Desactivar", "100.00");

        boolean resultado = dao.desactivar(p.getIdProducto());

        assertTrue(resultado);
        Producto desactivado = dao.obtenerPorId(p.getIdProducto());
        assertFalse(desactivado.isActivo());
    }

    @Test
    void desactivarProductoNoExistenteDeberiaRetornarFalse() throws Exception {
        boolean resultado = dao.desactivar(99999);
        assertFalse(resultado);
    }

    // Test de eliminicion con validacions

    @Test
    void eliminarProductoSinDependenciasDeberiaRetornarExitoso() throws Exception {
        Producto p = crearProducto("Producto a Eliminar", "100.00");

        ResultadoEliminacion resultado = dao.eliminar(p.getIdProducto());

        assertTrue(resultado.isEliminado());
        assertNull(resultado.getMotivo());
        assertNull(dao.obtenerPorId(p.getIdProducto()));
    }

    @Test
    void eliminarProductoNoExistenteDeberiaRetornarBloqueado() throws Exception {
        ResultadoEliminacion resultado = dao.eliminar(99999);

        assertFalse(resultado.isEliminado());
        assertEquals(MotivoBloqueoEliminacion.NO_EXISTE, resultado.getMotivo());
    }

    @Test
    void eliminarProductoConStockDeberiaRetornarBloqueado() throws Exception {
        Producto p = crearProducto("Producto con Stock", "100.00");
        crearStock(p.getIdProducto(), 50, "Almacén A");

        ResultadoEliminacion resultado = dao.eliminar(p.getIdProducto());

        assertFalse(resultado.isEliminado());
        assertEquals(MotivoBloqueoEliminacion.TIENE_STOCK, resultado.getMotivo());
        assertNotNull(dao.obtenerPorId(p.getIdProducto()));
    }

    // test de obtener por id

    @Test
    void obtenerPorIdExistenteDeberiaRetornarProducto() throws Exception {
        Producto p = crearProducto("Producto Get", "200.00");

        Producto recuperado = dao.obtenerPorId(p.getIdProducto());

        assertNotNull(recuperado);
        assertEquals("Producto Get", recuperado.getNombre());
    }

    @Test
    void obtenerPorIdNoExistenteDeberiaRetornarNull() throws Exception {
        Producto resultado = dao.obtenerPorId(99999);
        assertNull(resultado);
    }

    // test de listas activas

    @Test
    void listarActivosDeberiaRetornarSoloProductosActivos() throws Exception {
        Producto p1 = crearProducto("Activo 1", "100.00");
        Producto p2 = crearProducto("Activo 2", "150.00");
        Producto p3 = crearProducto("Inactivo", "200.00");
        dao.desactivar(p3.getIdProducto());

        List<Producto> activos = dao.listarActivos();

        assertEquals(2, activos.size());
    }

    // Metodos auxiliares

    private Producto crearProducto(String nombre, String precio) throws Exception {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion("Descripción de " + nombre);
        p.setPrecioUnitario(new BigDecimal(precio));
        p.setIdCategoria(idCategoriaTest);
        dao.alta(p);
        return p;
    }

    private void crearStock(int idProducto, int cantidad, String ubicacion) throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (var ps = conn.prepareStatement(
                "INSERT INTO inv_stock (id_producto, cantidad_actual, stock_minimo, ubicacion) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, idProducto);
            ps.setInt(2, cantidad);
            ps.setInt(3, 10);
            ps.setString(4, ubicacion);
            ps.executeUpdate();
        }
    }
}

