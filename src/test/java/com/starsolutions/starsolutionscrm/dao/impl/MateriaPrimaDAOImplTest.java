package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.dao.interfaces.MotivoBloqueoEliminacion;
import com.starsolutions.starsolutionscrm.dao.interfaces.ResultadoEliminacion;
import com.starsolutions.starsolutionscrm.database.DatabaseConnection;
import com.starsolutions.starsolutionscrm.model.inventario.MateriaPrima;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MateriaPrimaDAOImplTest {

    private static MateriaPrimaDAOImpl dao;

    @BeforeAll
    static void setUpClass() throws Exception {
        H2TestHelper.init("test_materia_prima");
        dao = new MateriaPrimaDAOImpl();
    }

    @AfterAll
    static void tearDownClass() throws Exception {
        H2TestHelper.dropSchema();
    }

    @BeforeEach
    void limpiarMateriasPrimas() throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (var s = conn.createStatement()) {
            s.execute("SET REFERENTIAL_INTEGRITY FALSE");
            s.execute("DELETE FROM inv_materia_prima");
            s.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
    }

    // Test de alta

    @Test
    void altaMateriasPrimaDeberiaRetornarTrue() throws Exception {
        MateriaPrima mp = new MateriaPrima();
        mp.setNombre("Tela Algodón");
        mp.setUnidad("metros");
        mp.setDescripcion("Tela de algodón puro");

        boolean resultado = dao.alta(mp);

        assertTrue(resultado);
        assertTrue(mp.getIdMateria() > 0);
    }

    @Test
    void altaMateriasPrimaDeberiaCrearActiva() throws Exception {
        MateriaPrima mp = new MateriaPrima();
        mp.setNombre("Hilo Poliéster");
        mp.setUnidad("rollos");
        mp.setDescripcion("Hilo para máquina");

        dao.alta(mp);

        MateriaPrima recuperada = dao.obtenerPorId(mp.getIdMateria());
        assertTrue(recuperada.isActivo());
    }

   // Test de buscar por nombre

    @Test
    void buscarPorNombreDeberiaEncontrarMateriasActivasConCoincidencia() throws Exception {
        MateriaPrima mp1 = crearMateriaPrima("Tela Mezclilla", "metros");
        MateriaPrima mp2 = crearMateriaPrima("Botones Metálicos", "piezas");
        dao.desactivar(mp2.getIdMateria());

        List<MateriaPrima> resultados = dao.buscarPorNombre("Tela");

        assertEquals(1, resultados.size());
        assertEquals("Tela Mezclilla", resultados.get(0).getNombre());
    }

    @Test
    void buscarPorNombreSinResultadosDeberiaRetornarListaVacia() throws Exception {
        crearMateriaPrima("Material Existente", "unidad");

        List<MateriaPrima> resultados = dao.buscarPorNombre("NoExiste");

        assertTrue(resultados.isEmpty());
    }

    //Test desactivar

    @Test
    void desactivarMateriasPrimaDeberiaPonerActivoEnFalso() throws Exception {
        MateriaPrima mp = crearMateriaPrima("Material a Desactivar", "kg");

        boolean resultado = dao.desactivar(mp.getIdMateria());

        assertTrue(resultado);
        MateriaPrima desactivada = dao.obtenerPorId(mp.getIdMateria());
        assertFalse(desactivada.isActivo());
    }

    @Test
    void desactivarMateriasPrimaNoExistenteDeberiaRetornarFalse() throws Exception {
        boolean resultado = dao.desactivar(99999);
        assertFalse(resultado);
    }

   //Test de eliminacion con validacion

    @Test
    void eliminarMateriasPrimaSinDependenciasDeberiaRetornarExitoso() throws Exception {
        MateriaPrima mp = crearMateriaPrima("Material para Eliminar", "litros");

        ResultadoEliminacion resultado = dao.eliminar(mp.getIdMateria());

        assertTrue(resultado.isEliminado());
        assertNull(resultado.getMotivo());
        assertNull(dao.obtenerPorId(mp.getIdMateria()));
    }

    @Test
    void eliminarMateriasPrimaNoExistenteDeberiaRetornarBloqueado() throws Exception {
        ResultadoEliminacion resultado = dao.eliminar(99999);

        assertFalse(resultado.isEliminado());
        assertEquals(MotivoBloqueoEliminacion.NO_EXISTE, resultado.getMotivo());
    }

    @Test
    void eliminarMateriasPrimaConStockDeberiaRetornarBloqueado() throws Exception {
        MateriaPrima mp = crearMateriaPrima("Material con Stock", "metros");
        crearStockMateriaPrima(mp.getIdMateria(), 100, "Almacén A");

        ResultadoEliminacion resultado = dao.eliminar(mp.getIdMateria());

        assertFalse(resultado.isEliminado());
        assertEquals(MotivoBloqueoEliminacion.TIENE_STOCK, resultado.getMotivo());
        assertNotNull(dao.obtenerPorId(mp.getIdMateria()));
    }

    //Test de obtencion por Id

    @Test
    void obtenerPorIdExistenteDeberiaRetornarMateriaPrima() throws Exception {
        MateriaPrima mp = crearMateriaPrima("Material Get", "unidades");

        MateriaPrima recuperada = dao.obtenerPorId(mp.getIdMateria());

        assertNotNull(recuperada);
        assertEquals("Material Get", recuperada.getNombre());
    }

    @Test
    void obtenerPorIdNoExistenteDeberiaRetornarNull() throws Exception {
        MateriaPrima resultado = dao.obtenerPorId(99999);
        assertNull(resultado);
    }
    //Test de listas activas

    @Test
    void listarActivasDeberiaRetornarSoloMateriasActivas() throws Exception {
        MateriaPrima mp1 = crearMateriaPrima("Activa 1", "kg");
        MateriaPrima mp2 = crearMateriaPrima("Activa 2", "litros");
        MateriaPrima mp3 = crearMateriaPrima("Inactiva", "piezas");
        dao.desactivar(mp3.getIdMateria());

        List<MateriaPrima> activas = dao.listarActivas();

        assertEquals(2, activas.size());
    }

    //Metodos auxiliares

    private MateriaPrima crearMateriaPrima(String nombre, String unidad) throws Exception {
        MateriaPrima mp = new MateriaPrima();
        mp.setNombre(nombre);
        mp.setUnidad(unidad);
        mp.setDescripcion("Descripción de " + nombre);
        dao.alta(mp);
        return mp;
    }

    private void crearStockMateriaPrima(int idMateria, int cantidad, String ubicacion) throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (var ps = conn.prepareStatement(
                "INSERT INTO inv_stock_mp (id_materia, cantidad_actual, stock_minimo, ubicacion) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, idMateria);
            ps.setInt(2, cantidad);
            ps.setInt(3, 10);
            ps.setString(4, ubicacion);
            ps.executeUpdate();
        }
    }
}

