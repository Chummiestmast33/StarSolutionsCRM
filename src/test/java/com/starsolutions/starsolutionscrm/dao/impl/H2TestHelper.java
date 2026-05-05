package com.starsolutions.starsolutionscrm.dao.impl;

import com.starsolutions.starsolutionscrm.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class that sets up an H2 in-memory database for DAO integration tests.
 * Each test class should call {@link #init(String)} in {@code @BeforeAll} with a
 * unique database name so that schemas are isolated between test classes.
 */
public final class H2TestHelper {

    private H2TestHelper() {}

    /**
     * Points the {@link DatabaseConnection} singleton at a fresh H2 in-memory
     * database, closes any stale connection, and creates the minimal schema.
     *
     * @param dbName unique name for this test's H2 in-memory database
     */
    public static void init(String dbName) throws Exception {
        System.setProperty("db.url",
                "jdbc:h2:mem:" + dbName + ";MODE=MySQL;DB_CLOSE_DELAY=-1");
        System.clearProperty("db.user");
        System.clearProperty("db.password");

        // Force the singleton to reconnect with the new URL
        try {
            DatabaseConnection.getInstance().getConnection().close();
        } catch (Exception ignored) {
            // No existing connection – that's fine
        }

        Connection conn = DatabaseConnection.getInstance().getConnection();
        createSchema(conn);
    }

    /** Drops all tables in reverse FK order so each test class starts clean. */
    public static void dropSchema() throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (Statement s = conn.createStatement()) {
            s.execute("SET REFERENTIAL_INTEGRITY FALSE");
            String[] tables = {
                "prd_orden_produccion",
                "rh_nomina", "rh_asistencia",
                "rh_empleado_ventas", "rh_empleado_rh",
                "rh_empleado_inventario", "rh_empleado_produccion",
                "rh_empleado",
                "inv_stock", "inv_producto", "cat_categoria_producto"
            };
            for (String t : tables) {
                s.execute("DROP TABLE IF EXISTS " + t);
            }
            s.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
    }

    // ------------------------------------------------------------------
    // DDL
    // ------------------------------------------------------------------

    private static void createSchema(Connection conn) throws SQLException {
        try (Statement s = conn.createStatement()) {

            // Disable FK checks while creating tables
            s.execute("SET REFERENTIAL_INTEGRITY FALSE");

            s.execute("""
                CREATE TABLE IF NOT EXISTS cat_categoria_producto (
                    id_categoria  INT          AUTO_INCREMENT PRIMARY KEY,
                    nombre        VARCHAR(80)  NOT NULL,
                    descripcion   VARCHAR(200) NULL,
                    activo        TINYINT(1)   NOT NULL DEFAULT 1
                )
                """);

            s.execute("""
                CREATE TABLE IF NOT EXISTS rh_empleado (
                    num           INT          AUTO_INCREMENT PRIMARY KEY,
                    nombre        VARCHAR(120) NOT NULL,
                    contrasena    VARCHAR(256) NOT NULL,
                    productividad DECIMAL(5,2) NULL DEFAULT 0,
                    eficiencia    DECIMAL(5,2) NULL DEFAULT 0,
                    tipo_empleado VARCHAR(20)  NOT NULL,
                    activo        TINYINT(1)   NOT NULL DEFAULT 1
                )
                """);

            s.execute("""
                CREATE TABLE IF NOT EXISTS rh_empleado_ventas (
                    num INT PRIMARY KEY,
                    CONSTRAINT fk_ev_emp FOREIGN KEY (num) REFERENCES rh_empleado(num)
                )
                """);

            s.execute("""
                CREATE TABLE IF NOT EXISTS rh_empleado_rh (
                    num INT PRIMARY KEY,
                    CONSTRAINT fk_erh_emp FOREIGN KEY (num) REFERENCES rh_empleado(num)
                )
                """);

            s.execute("""
                CREATE TABLE IF NOT EXISTS rh_empleado_inventario (
                    num INT PRIMARY KEY,
                    CONSTRAINT fk_einv_emp FOREIGN KEY (num) REFERENCES rh_empleado(num)
                )
                """);

            s.execute("""
                CREATE TABLE IF NOT EXISTS rh_empleado_produccion (
                    num INT PRIMARY KEY,
                    CONSTRAINT fk_eprod_emp FOREIGN KEY (num) REFERENCES rh_empleado(num)
                )
                """);

            s.execute("""
                CREATE TABLE IF NOT EXISTS rh_asistencia (
                    id_asistencia INT  AUTO_INCREMENT PRIMARY KEY,
                    id_empleado   INT  NOT NULL,
                    fecha         DATE NOT NULL,
                    hora_entrada  TIME NULL,
                    hora_salida   TIME NULL,
                    CONSTRAINT fk_asi_emp FOREIGN KEY (id_empleado) REFERENCES rh_empleado(num)
                )
                """);

            s.execute("""
                CREATE TABLE IF NOT EXISTS rh_nomina (
                    id_nomina    INT           AUTO_INCREMENT PRIMARY KEY,
                    id_empleado  INT           NOT NULL,
                    salario_base DECIMAL(10,2) NOT NULL,
                    deducciones  DECIMAL(10,2) NOT NULL DEFAULT 0,
                    neto         DECIMAL(10,2) GENERATED ALWAYS AS (salario_base - deducciones),
                    periodo      DATE          NOT NULL,
                    CONSTRAINT fk_nom_emp FOREIGN KEY (id_empleado) REFERENCES rh_empleado(num)
                )
                """);

            s.execute("""
                CREATE TABLE IF NOT EXISTS inv_producto (
                    id_producto     INT            AUTO_INCREMENT PRIMARY KEY,
                    nombre          VARCHAR(120)   NOT NULL,
                    descripcion     VARCHAR(300)   NULL,
                    precio_unitario DECIMAL(10,2)  NOT NULL,
                    id_categoria    INT            NOT NULL,
                    activo          TINYINT(1)     NOT NULL DEFAULT 1,
                    CONSTRAINT fk_prod_cat FOREIGN KEY (id_categoria)
                        REFERENCES cat_categoria_producto(id_categoria)
                )
                """);

            s.execute("""
                CREATE TABLE IF NOT EXISTS inv_stock (
                    id_stock        INT          AUTO_INCREMENT PRIMARY KEY,
                    id_producto     INT          NOT NULL,
                    cantidad_actual INT          NOT NULL DEFAULT 0,
                    stock_minimo    INT          NOT NULL DEFAULT 0,
                    stock_maximo    INT          NULL,
                    ubicacion       VARCHAR(100) NULL,
                    CONSTRAINT fk_stk_prod FOREIGN KEY (id_producto)
                        REFERENCES inv_producto(id_producto)
                )
                """);

            s.execute("""
                CREATE TABLE IF NOT EXISTS prd_orden_produccion (
                    id_orden_prod        INT         AUTO_INCREMENT PRIMARY KEY,
                    id_empleado          INT         NOT NULL,
                    id_producto_final    INT         NOT NULL,
                    cantidad_planificada INT         NOT NULL,
                    cantidad_producida   INT         NULL DEFAULT 0,
                    fecha_inicio         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    fecha_estimada_fin   DATE        NULL,
                    fecha_real_fin       DATETIME    NULL,
                    estado               VARCHAR(15) NOT NULL DEFAULT 'En Proceso',
                    CONSTRAINT fk_op_emp  FOREIGN KEY (id_empleado)
                        REFERENCES rh_empleado_produccion(num),
                    CONSTRAINT fk_op_prod FOREIGN KEY (id_producto_final)
                        REFERENCES inv_producto(id_producto)
                )
                """);

            s.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
    }
}
