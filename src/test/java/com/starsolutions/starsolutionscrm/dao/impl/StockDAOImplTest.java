package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.inventario.MovimientoInventario;
import com.starsolutions.starsolutionscrm.model.inventario.Producto;
import com.starsolutions.starsolutionscrm.model.inventario.Stock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockDAOImplTest {

    private static StockDAOImpl dao;
    private static ProductoDAOImpl productoDao;
    private static int idCategoriaTest;
    private static int idProductoTest;
    private static int idStockTest;

    @BeforeAll
    static void setUpClass() throws Exception {
        H2TestHelper.init("test_stock");
        dao = new StockDAOImpl();
        productoDao = new ProductoDAOImpl();

        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (var s = conn.createStatement()) {
            s.execute("INSERT INTO cat_categoria_producto (nombre, activo) VALUES ('Test Category', 1)");
        }

        try (var s = conn.createStatement();
             var rs = s.executeQuery("SELECT id_categoria FROM cat_categoria_producto LIMIT 1")) {
            rs.next();
            idCategoriaTest = rs.getInt(1);
        }

        Producto p = new Producto();
        p.setNombre("Producto Stock Test");
        p.setDescripcion("Test");
        p.setPrecioUnitario(new BigDecimal("100.00"));
        p.setIdCategoria(idCategoriaTest);
        productoDao.alta(p);
        idProductoTest = p.getIdProducto();

        try (var ps = conn.prepareStatement(
                "INSERT INTO inv_stock (id_producto, cantidad_actual, stock_minimo, ubicacion) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, idProductoTest);
            ps.setInt(2, 100);
            ps.setInt(3, 10);
            ps.setString(4, "Almacén A");
            ps.executeUpdate();
        }

        try (var s = conn.createStatement();
             var rs = s.executeQuery("SELECT id_stock FROM inv_stock LIMIT 1")) {
            rs.next();
            idStockTest = rs.getInt(1);
        }
    }

    @AfterAll
    static void tearDownClass() throws Exception {
        H2TestHelper.dropSchema();
    }

    @BeforeEach
    void resetearStock() throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (var ps = conn.prepareStatement("UPDATE inv_stock SET cantidad_actual = 100 WHERE id_stock = ?")) {
            ps.setInt(1, idStockTest);
            ps.executeUpdate();
        }
    }

    // Verificar disponiblidad por producto

    @Test
    void verificarDisponibilidadPorUbicacionDeberiaRetornarTrue() throws Exception {
        boolean resultado = dao.verificarDisponibilidadPorUbicacion(idProductoTest, "Almacén A", 50);
        assertTrue(resultado);
    }

    @Test
    void verificarDisponibilidadPorUbicacionConCantidadExactaDeberiaRetornarTrue() throws Exception {
        boolean resultado = dao.verificarDisponibilidadPorUbicacion(idProductoTest, "Almacén A", 100);
        assertTrue(resultado);
    }

    @Test
    void verificarDisponibilidadPorUbicacionSinSuficienteDeberiaRetornarFalse() throws Exception {
        boolean resultado = dao.verificarDisponibilidadPorUbicacion(idProductoTest, "Almacén A", 150);
        assertFalse(resultado);
    }

    @Test
    void verificarDisponibilidadPorUbicacionUbicacionNoExistenteDeberiaRetornarFalse() throws Exception {
        boolean resultado = dao.verificarDisponibilidadPorUbicacion(idProductoTest, "Almacén Inexistente", 50);
        assertFalse(resultado);
    }

    @Test
    void verificarDisponibilidadPorUbicacionNormalizaUbicacion() throws Exception {
        boolean resultado = dao.verificarDisponibilidadPorUbicacion(idProductoTest, "  ALMACÉN A  ", 50);
        assertTrue(resultado);
    }

    // Disponiblidad total por producto

    @Test
    void verificarDisponibilidadTotalProductoDeberiaRetornarTrue() throws Exception {
        boolean resultado = dao.verificarDisponibilidadTotalProducto(idProductoTest, 50);
        assertTrue(resultado);
    }

    @Test
    void verificarDisponibilidadTotalProductoConCantidadExactaDeberiaRetornarTrue() throws Exception {
        boolean resultado = dao.verificarDisponibilidadTotalProducto(idProductoTest, 100);
        assertTrue(resultado);
    }

    @Test
    void verificarDisponibilidadTotalProductoSinSuficienteDeberiaRetornarFalse() throws Exception {
        boolean resultado = dao.verificarDisponibilidadTotalProducto(idProductoTest, 150);
        assertFalse(resultado);
    }

    @Test
    void verificarDisponibilidadTotalProductoProductoNoExistenteDeberiaRetornarFalse() throws Exception {
        boolean resultado = dao.verificarDisponibilidadTotalProducto(99999, 50);
        assertFalse(resultado);
    }

   // Test de registrar los movimientos de inventario

    @Test
    void registrarMovimientoEntradaDeberiaRetornarIdPositivo() throws Exception {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdStock(idStockTest);
        mov.setTipo("ENTRADA");
        mov.setCantidad(50);
        mov.setFecha(LocalDate.now());
        mov.setReferencia("Compra-001");

        int idMov = dao.registrarMovimiento(mov);

        assertTrue(idMov > 0);
    }

    @Test
    void registrarMovimientoSalidaConDisponibilidadDeberiaRetornarIdPositivo() throws Exception {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdStock(idStockTest);
        mov.setTipo("SALIDA");
        mov.setCantidad(30);
        mov.setFecha(LocalDate.now());
        mov.setReferencia("Venta-001");

        int idMov = dao.registrarMovimiento(mov);

        assertTrue(idMov > 0);
    }

    @Test
    void registrarMovimientoSalidaSinDisponibilidadDeberiaTirarExcepcion() throws Exception {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdStock(idStockTest);
        mov.setTipo("SALIDA");
        mov.setCantidad(150); // Más del disponible (100)
        mov.setFecha(LocalDate.now());
        mov.setReferencia("Venta-002");

        assertThrows(SQLException.class, () -> dao.registrarMovimiento(mov));
    }

    //Consultas de stock por ubicacion

    @Test
    void obtenerStockPorUbicacionExistenteDeberiaRetornarStock() throws Exception {
        Stock stock = dao.obtenerStockPorUbicacion(idProductoTest, "Almacén A");

        assertNotNull(stock);
        assertEquals(idProductoTest, stock.getIdProducto());
        assertEquals(100, stock.getCantidadActual());
    }

    @Test
    void obtenerStockPorUbicacionNoExistenteDeberiaRetornarNull() throws Exception {
        Stock stock = dao.obtenerStockPorUbicacion(idProductoTest, "Almacén Inexistente");
        assertNull(stock);
    }

    // Consultas de stock por producto

    @Test
    void listarStockPorProductoDeberiaRetornarTodosLosStock() throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (var ps = conn.prepareStatement(
                "INSERT INTO inv_stock (id_producto, cantidad_actual, stock_minimo, ubicacion) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, idProductoTest);
            ps.setInt(2, 50);
            ps.setInt(3, 5);
            ps.setString(4, "Almacén B");
            ps.executeUpdate();
        }

        List<Stock> lista = dao.listarStockPorProducto(idProductoTest);

        assertEquals(2, lista.size());
    }
}



