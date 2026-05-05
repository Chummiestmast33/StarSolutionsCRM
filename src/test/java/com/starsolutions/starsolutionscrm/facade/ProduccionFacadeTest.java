package com.starsolutions.starsolutionscrm.facade;

import com.starsolutions.starsolutionscrm.dao.impl.EmpleadoDAOImpl;
import com.starsolutions.starsolutionscrm.dao.impl.H2TestHelper;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.produccion.OrdenProduccion;
import com.starsolutions.starsolutionscrm.model.rrhh.Empleado;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProduccionFacadeTest {

    private static ProduccionFacade facade;
    private static int idEmpleadoProduccion;
    private static int idProducto;

    @BeforeAll
    static void setUpClass() throws Exception {
        H2TestHelper.init("test_facade_produccion");
        facade = new ProduccionFacade();

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (var s = conn.createStatement()) {
            s.execute("INSERT INTO cat_categoria_producto (nombre, activo) VALUES ('Cat Facade', 1)");
        }

        int idCategoria;
        try (var s = conn.createStatement();
             var rs = s.executeQuery("SELECT id_categoria FROM cat_categoria_producto LIMIT 1")) {
            rs.next();
            idCategoria = rs.getInt(1);
        }

        try (var ps = conn.prepareStatement(
                "INSERT INTO inv_producto (nombre, precio_unitario, id_categoria, activo) VALUES (?, 5.00, ?, 1)",
                java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Prod Facade");
            ps.setInt(2, idCategoria);
            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) {
                rs.next();
                idProducto = rs.getInt(1);
            }
        }

        EmpleadoDAOImpl empleadoDAO = new EmpleadoDAOImpl();
        Empleado e = new Empleado();
        e.setNombre("Empleado Facade");
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
    // Tests: crearOrden — validación de negocio
    // ------------------------------------------------------------------

    @Test
    void crearOrdenConCantidadPlanificadaCeroDebeLanzarIllegalArgumentException() {
        OrdenProduccion o = buildOrden(0, null);
        assertThrows(IllegalArgumentException.class, () -> facade.crearOrden(o));
    }

    @Test
    void crearOrdenConCantidadNegativaDebeLanzarIllegalArgumentException() {
        OrdenProduccion o = buildOrden(-10, null);
        assertThrows(IllegalArgumentException.class, () -> facade.crearOrden(o));
    }

    @Test
    void crearOrdenValidaDeberiaRetornarTrue() throws Exception {
        OrdenProduccion o = buildOrden(100, null);
        boolean resultado = facade.crearOrden(o);
        assertTrue(resultado);
        assertTrue(o.getIdOrdenProd() > 0);
    }

    // ------------------------------------------------------------------
    // Tests: completarOrden — validación de negocio
    // ------------------------------------------------------------------

    @Test
    void completarOrdenConCantidadCeroDebeLanzarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> facade.completarOrden(1, 0));
    }

    @Test
    void completarOrdenConCantidadNegativaDebeLanzarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> facade.completarOrden(1, -5));
    }

    @Test
    void completarOrdenInexistenteDebeLanzarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> facade.completarOrden(99999, 10));
    }

    @Test
    void completarOrdenCanceladaDebeLanzarIllegalStateException() throws Exception {
        OrdenProduccion o = buildOrden(50, null);
        facade.crearOrden(o);
        facade.cancelarOrden(o.getIdOrdenProd());

        assertThrows(IllegalStateException.class,
                () -> facade.completarOrden(o.getIdOrdenProd(), 30));
    }

    @Test
    void completarOrdenValidaDeberiaRetornarTrue() throws Exception {
        OrdenProduccion o = buildOrden(100, null);
        facade.crearOrden(o);

        boolean resultado = facade.completarOrden(o.getIdOrdenProd(), 90);
        assertTrue(resultado);
    }

    // ------------------------------------------------------------------
    // Tests: cancelarOrden — validación de negocio
    // ------------------------------------------------------------------

    @Test
    void cancelarOrdenInexistenteDebeLanzarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> facade.cancelarOrden(99999));
    }

    @Test
    void cancelarOrdenCompletadaDebeLanzarIllegalStateException() throws Exception {
        OrdenProduccion o = buildOrden(50, null);
        facade.crearOrden(o);
        facade.completarOrden(o.getIdOrdenProd(), 50);

        assertThrows(IllegalStateException.class, () -> facade.cancelarOrden(o.getIdOrdenProd()));
    }

    @Test
    void cancelarOrdenValidaDeberiaRetornarTrue() throws Exception {
        OrdenProduccion o = buildOrden(50, null);
        facade.crearOrden(o);

        boolean resultado = facade.cancelarOrden(o.getIdOrdenProd());
        assertTrue(resultado);
    }

    // ------------------------------------------------------------------
    // Tests: consultas
    // ------------------------------------------------------------------

    @Test
    void obtenerTodasLasOrdenesDeberiaRetornarOrdenesCreadas() throws Exception {
        facade.crearOrden(buildOrden(10, null));
        facade.crearOrden(buildOrden(20, null));

        assertEquals(2, facade.obtenerTodasLasOrdenes().size());
    }

    @Test
    void obtenerOrdenesEnProcesoDeberiaFiltrarPorEstado() throws Exception {
        OrdenProduccion o1 = buildOrden(10, null);
        OrdenProduccion o2 = buildOrden(20, null);
        facade.crearOrden(o1);
        facade.crearOrden(o2);
        facade.cancelarOrden(o2.getIdOrdenProd());

        assertEquals(1, facade.obtenerOrdenesEnProceso().size());
        assertEquals(o1.getIdOrdenProd(),
                facade.obtenerOrdenesEnProceso().get(0).getIdOrdenProd());
    }

    @Test
    void obtenerOrdenPorIdDeberiaRetornarOrdenCorrecta() throws Exception {
        OrdenProduccion o = buildOrden(75, LocalDate.of(2026, 11, 1));
        facade.crearOrden(o);

        OrdenProduccion resultado = facade.obtenerOrdenPorId(o.getIdOrdenProd());
        assertNotNull(resultado);
        assertEquals(75, resultado.getCantidadPlanificada());
    }

    @Test
    void obtenerOrdenesPorEstadoDeberiaFiltrar() throws Exception {
        OrdenProduccion o1 = buildOrden(5, null);
        OrdenProduccion o2 = buildOrden(6, null);
        facade.crearOrden(o1);
        facade.crearOrden(o2);
        facade.completarOrden(o1.getIdOrdenProd(), 5);

        var completadas = facade.obtenerOrdenesPorEstado(OrdenProduccion.ESTADO_COMPLETADA);
        assertEquals(1, completadas.size());
        assertEquals(OrdenProduccion.ESTADO_COMPLETADA, completadas.get(0).getEstado());
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
