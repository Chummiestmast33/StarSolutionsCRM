package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.produccion.OrdenProduccion;
import com.starsolutions.starsolutionscrm.model.rrhh.Empleado;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrdenProduccionDAOImplTest {

    private static OrdenProduccionDAOImpl dao;
    private static int idEmpleadoProduccion;
    private static int idProducto;

    @BeforeAll
    static void setUpClass() throws Exception {
        H2TestHelper.init("test_orden_prod");
        dao = new OrdenProduccionDAOImpl();

        Connection conn = DatabaseConnection.getInstance().getConnection();

        // Create supporting records
        try (var s = conn.createStatement()) {
            s.execute("INSERT INTO cat_categoria_producto (nombre, activo) VALUES ('Test Cat', 1)");
        }

        // Get the generated category id
        int idCategoria;
        try (var s = conn.createStatement();
             var rs = s.executeQuery("SELECT id_categoria FROM cat_categoria_producto LIMIT 1")) {
            rs.next();
            idCategoria = rs.getInt(1);
        }

        try (var ps = conn.prepareStatement(
                "INSERT INTO inv_producto (nombre, precio_unitario, id_categoria, activo) VALUES (?, 10.00, ?, 1)",
                java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Producto Test");
            ps.setInt(2, idCategoria);
            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) {
                rs.next();
                idProducto = rs.getInt(1);
            }
        }

        // Create Produccion employee
        EmpleadoDAOImpl empleadoDAO = new EmpleadoDAOImpl();
        Empleado e = new Empleado();
        e.setNombre("Empleado Produccion");
        e.setContrasena("pass");
        e.setTipoEmpleado("Produccion");
        empleadoDAO.crear(e);
        idEmpleadoProduccion = e.getNum();
    }

    @AfterAll
    static void tearDownClass() throws Exception {
        H2TestHelper.dropSchema();
    }

    @BeforeEach
    void limpiarOrdenes() throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (var s = conn.createStatement()) {
            s.execute("DELETE FROM prd_orden_produccion");
        }
    }

    // ------------------------------------------------------------------
    // Tests: crear
    // ------------------------------------------------------------------

    @Test
    void crearOrdenDeberiaRetornarTrueYAsignarId() throws Exception {
        OrdenProduccion o = buildOrden(100, null);

        boolean resultado = dao.crear(o);

        assertTrue(resultado);
        assertTrue(o.getIdOrdenProd() > 0);
    }

    @Test
    void crearOrdenConFechaEstimadaDeberiaGuardarFecha() throws Exception {
        LocalDate estimada = LocalDate.of(2026, 12, 31);
        OrdenProduccion o = buildOrden(50, estimada);
        dao.crear(o);

        OrdenProduccion resultado = dao.obtenerPorId(o.getIdOrdenProd());
        assertEquals(estimada, resultado.getFechaEstimadaFin());
    }

    @Test
    void crearOrdenSinFechaEstimadaDeberiaGuardarNull() throws Exception {
        OrdenProduccion o = buildOrden(30, null);
        dao.crear(o);

        OrdenProduccion resultado = dao.obtenerPorId(o.getIdOrdenProd());
        assertNull(resultado.getFechaEstimadaFin());
    }

    @Test
    void crearOrdenDeberiaUsarEstadoEnProcesoPorDefecto() throws Exception {
        OrdenProduccion o = buildOrden(10, null);
        dao.crear(o);

        OrdenProduccion resultado = dao.obtenerPorId(o.getIdOrdenProd());
        assertEquals(OrdenProduccion.ESTADO_EN_PROCESO, resultado.getEstado());
    }

    // ------------------------------------------------------------------
    // Tests: completar
    // ------------------------------------------------------------------

    @Test
    void completarOrdenEnProcesoDeberiaRetornarTrue() throws Exception {
        OrdenProduccion o = buildOrden(100, null);
        dao.crear(o);

        boolean resultado = dao.completar(o.getIdOrdenProd(), 95);
        assertTrue(resultado);
    }

    @Test
    void completarOrdenDeberiaActualizarEstadoYCantidad() throws Exception {
        OrdenProduccion o = buildOrden(100, null);
        dao.crear(o);

        dao.completar(o.getIdOrdenProd(), 98);

        OrdenProduccion resultado = dao.obtenerPorId(o.getIdOrdenProd());
        assertEquals(OrdenProduccion.ESTADO_COMPLETADA, resultado.getEstado());
        assertEquals(98, resultado.getCantidadProducida());
        assertNotNull(resultado.getFechaRealFin());
    }

    @Test
    void completarOrdenYaCompletadaDeberiaRetornarFalse() throws Exception {
        OrdenProduccion o = buildOrden(100, null);
        dao.crear(o);
        dao.completar(o.getIdOrdenProd(), 100);

        boolean resultado = dao.completar(o.getIdOrdenProd(), 50);
        assertFalse(resultado);
    }

    // ------------------------------------------------------------------
    // Tests: cancelar
    // ------------------------------------------------------------------

    @Test
    void cancelarOrdenEnProcesoDeberiaRetornarTrue() throws Exception {
        OrdenProduccion o = buildOrden(100, null);
        dao.crear(o);

        boolean resultado = dao.cancelar(o.getIdOrdenProd());
        assertTrue(resultado);
    }

    @Test
    void cancelarOrdenDeberiaActualizarEstado() throws Exception {
        OrdenProduccion o = buildOrden(100, null);
        dao.crear(o);

        dao.cancelar(o.getIdOrdenProd());

        OrdenProduccion resultado = dao.obtenerPorId(o.getIdOrdenProd());
        assertEquals(OrdenProduccion.ESTADO_CANCELADA, resultado.getEstado());
    }

    @Test
    void cancelarOrdenYaCanceladaDeberiaRetornarFalse() throws Exception {
        OrdenProduccion o = buildOrden(100, null);
        dao.crear(o);
        dao.cancelar(o.getIdOrdenProd());

        boolean resultado = dao.cancelar(o.getIdOrdenProd());
        assertFalse(resultado);
    }

    // ------------------------------------------------------------------
    // Tests: obtenerTodas
    // ------------------------------------------------------------------

    @Test
    void obtenerTodasSinOrdenesDeberiaRetornarListaVacia() throws Exception {
        List<OrdenProduccion> lista = dao.obtenerTodas();
        assertTrue(lista.isEmpty());
    }

    @Test
    void obtenerTodasDeberiaRetornarTodasLasOrdenes() throws Exception {
        dao.crear(buildOrden(10, null));
        dao.crear(buildOrden(20, null));
        dao.crear(buildOrden(30, null));

        List<OrdenProduccion> lista = dao.obtenerTodas();
        assertEquals(3, lista.size());
    }

    // ------------------------------------------------------------------
    // Tests: obtenerPorEstado
    // ------------------------------------------------------------------

    @Test
    void obtenerPorEstadoDeberiaFiltrarPorEstado() throws Exception {
        OrdenProduccion o1 = buildOrden(10, null);
        OrdenProduccion o2 = buildOrden(20, null);
        OrdenProduccion o3 = buildOrden(30, null);
        dao.crear(o1);
        dao.crear(o2);
        dao.crear(o3);

        dao.cancelar(o3.getIdOrdenProd());

        List<OrdenProduccion> enProceso = dao.obtenerPorEstado(OrdenProduccion.ESTADO_EN_PROCESO);
        List<OrdenProduccion> canceladas = dao.obtenerPorEstado(OrdenProduccion.ESTADO_CANCELADA);

        assertEquals(2, enProceso.size());
        assertEquals(1, canceladas.size());
    }

    // ------------------------------------------------------------------
    // Tests: obtenerPorId
    // ------------------------------------------------------------------

    @Test
    void obtenerPorIdExistenteDeberiaRetornarOrden() throws Exception {
        OrdenProduccion o = buildOrden(55, LocalDate.of(2026, 10, 1));
        dao.crear(o);

        OrdenProduccion resultado = dao.obtenerPorId(o.getIdOrdenProd());

        assertNotNull(resultado);
        assertEquals(55, resultado.getCantidadPlanificada());
        assertEquals(idEmpleadoProduccion, resultado.getIdEmpleado());
        assertEquals(idProducto, resultado.getIdProductoFinal());
    }

    @Test
    void obtenerPorIdInexistenteDeberiaRetornarNull() throws Exception {
        OrdenProduccion resultado = dao.obtenerPorId(99999);
        assertNull(resultado);
    }

    // ------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------

    private OrdenProduccion buildOrden(int cantPlanificada, LocalDate fechaEstimada) {
        OrdenProduccion o = new OrdenProduccion();
        o.setIdEmpleado(idEmpleadoProduccion);
        o.setIdProductoFinal(idProducto);
        o.setCantidadPlanificada(cantPlanificada);
        o.setFechaEstimadaFin(fechaEstimada);
        return o;
    }
}
