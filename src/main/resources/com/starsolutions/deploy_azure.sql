-- ============================================================
--  DEPLOY LIMPIO v2.1  —  Inicialización desde cero
--  Motor:   MySQL 8.0+ / Azure Database for MySQL
--  Incluye: DDL completo + triggers corregidos + seed data
-- ============================================================
--  Sufijos de módulo:
--    cat_  Catálogos base
--    rh_   Recursos Humanos / Empleados
--    crm_  Clientes y Proveedores
--    inv_  Inventario (productos, stock, materia prima)
--    cmp_  Compras
--    ven_  Ventas
--    prd_  Producción
-- ============================================================

CREATE DATABASE IF NOT EXISTS crm_erp
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_spanish_ci;

USE crm_erp;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- 1. CATÁLOGOS BASE
-- ============================================================

CREATE TABLE cat_categoria_producto (
    id_categoria  INT          AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(80)  NOT NULL,
    descripcion   VARCHAR(200) NULL,
    activo        TINYINT(1)   NOT NULL DEFAULT 1
);

-- ============================================================
-- 2. EMPLEADOS
-- ============================================================

CREATE TABLE rh_empleado (
    num           INT          AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(120) NOT NULL,
    contrasena    VARCHAR(256) NOT NULL,
    productividad DECIMAL(5,2) NULL DEFAULT 0,
    eficiencia    DECIMAL(5,2) NULL DEFAULT 0,
    tipo_empleado VARCHAR(20)  NOT NULL,
    activo        TINYINT(1)   NOT NULL DEFAULT 1,
    CONSTRAINT chk_emp_tipo CHECK (tipo_empleado IN ('Ventas','RH','Inventario','Produccion')),
    CONSTRAINT chk_emp_prod CHECK (productividad BETWEEN 0 AND 100),
    CONSTRAINT chk_emp_efic CHECK (eficiencia    BETWEEN 0 AND 100)
);

CREATE TABLE rh_empleado_ventas (
    num INT PRIMARY KEY,
    CONSTRAINT fk_ev_emp FOREIGN KEY (num) REFERENCES rh_empleado(num)
);

CREATE TABLE rh_empleado_rh (
    num INT PRIMARY KEY,
    CONSTRAINT fk_erh_emp FOREIGN KEY (num) REFERENCES rh_empleado(num)
);

CREATE TABLE rh_empleado_inventario (
    num INT PRIMARY KEY,
    CONSTRAINT fk_einv_emp FOREIGN KEY (num) REFERENCES rh_empleado(num)
);

CREATE TABLE rh_empleado_produccion (
    num INT PRIMARY KEY,
    CONSTRAINT fk_eprod_emp FOREIGN KEY (num) REFERENCES rh_empleado(num)
);

CREATE TABLE rh_asistencia (
    id_asistencia INT  AUTO_INCREMENT PRIMARY KEY,
    id_empleado   INT  NOT NULL,
    fecha         DATE NOT NULL,
    hora_entrada  TIME NULL,
    hora_salida   TIME NULL,
    CONSTRAINT fk_asi_emp FOREIGN KEY (id_empleado) REFERENCES rh_empleado(num)
);

CREATE TABLE rh_nomina (
    id_nomina    INT           AUTO_INCREMENT PRIMARY KEY,
    id_empleado  INT           NOT NULL,
    salario_base DECIMAL(10,2) NOT NULL,
    deducciones  DECIMAL(10,2) NOT NULL DEFAULT 0,
    neto         DECIMAL(10,2) GENERATED ALWAYS AS (salario_base - deducciones) STORED,
    periodo      DATE          NOT NULL,
    CONSTRAINT fk_nom_emp  FOREIGN KEY (id_empleado) REFERENCES rh_empleado(num),
    CONSTRAINT chk_nom_base CHECK (salario_base >= 0),
    CONSTRAINT chk_nom_ded  CHECK (deducciones  >= 0)
);

-- ============================================================
-- 3. CLIENTES Y PROVEEDORES
-- ============================================================

CREATE TABLE crm_cliente (
    id_cliente INT          AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(150) NOT NULL,
    direccion  VARCHAR(250) NULL,
    rfc        VARCHAR(13)  NULL UNIQUE,
    activo     TINYINT(1)   NOT NULL DEFAULT 1
);

CREATE TABLE crm_cliente_descuento (
    id_descuento INT          AUTO_INCREMENT PRIMARY KEY,
    id_cliente   INT          NOT NULL,
    descuento    DECIMAL(5,2) NOT NULL,
    descripcion  VARCHAR(150) NULL,
    activo       TINYINT(1)   NOT NULL DEFAULT 1,
    CONSTRAINT fk_cd_cli  FOREIGN KEY (id_cliente) REFERENCES crm_cliente(id_cliente),
    CONSTRAINT chk_cd_desc CHECK (descuento BETWEEN 0 AND 100)
);

CREATE TABLE crm_cliente_contacto (
    id_contacto INT          AUTO_INCREMENT PRIMARY KEY,
    id_cliente  INT          NOT NULL,
    tipo        VARCHAR(20)  NOT NULL DEFAULT 'Principal',
    telefono    VARCHAR(15)  NULL,
    email       VARCHAR(120) NULL,
    CONSTRAINT fk_cc_cli  FOREIGN KEY (id_cliente) REFERENCES crm_cliente(id_cliente),
    CONSTRAINT chk_cc_tipo CHECK (tipo IN ('Principal','Sucursal','Facturacion','Otro'))
);

CREATE TABLE crm_proveedor (
    id_proveedor INT          AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(150) NOT NULL,
    rfc          VARCHAR(13)  NULL UNIQUE,
    direccion    VARCHAR(250) NULL,
    activo       TINYINT(1)   NOT NULL DEFAULT 1
);

CREATE TABLE crm_proveedor_contacto (
    id_contacto     INT          AUTO_INCREMENT PRIMARY KEY,
    id_proveedor    INT          NOT NULL,
    nombre_contacto VARCHAR(120) NOT NULL,
    telefono        VARCHAR(15)  NULL,
    email           VARCHAR(120) NULL,
    CONSTRAINT fk_pc_prov FOREIGN KEY (id_proveedor) REFERENCES crm_proveedor(id_proveedor)
);

-- ============================================================
-- 4. INVENTARIO
-- ============================================================

CREATE TABLE inv_producto (
    id_producto     INT            AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(120)   NOT NULL,
    descripcion     VARCHAR(300)   NULL,
    precio_unitario DECIMAL(10,2)  NOT NULL,
    id_categoria    INT            NOT NULL,
    activo          TINYINT(1)     NOT NULL DEFAULT 1,
    CONSTRAINT fk_prod_cat    FOREIGN KEY (id_categoria) REFERENCES cat_categoria_producto(id_categoria),
    CONSTRAINT chk_prod_precio CHECK (precio_unitario >= 0)
);

CREATE TABLE inv_stock (
    id_stock        INT          AUTO_INCREMENT PRIMARY KEY,
    id_producto     INT          NOT NULL,
    cantidad_actual INT          NOT NULL DEFAULT 0,
    stock_minimo    INT          NOT NULL DEFAULT 0,
    stock_maximo    INT          NULL,
    ubicacion       VARCHAR(100) NULL,
    CONSTRAINT fk_stk_prod    FOREIGN KEY (id_producto) REFERENCES inv_producto(id_producto),
    CONSTRAINT chk_stk_cant   CHECK (cantidad_actual >= 0)
);

CREATE TABLE inv_movimiento (
    id_movimiento INT          AUTO_INCREMENT PRIMARY KEY,
    id_stock      INT          NOT NULL,
    id_emp_inv    INT          NULL,
    tipo          VARCHAR(10)  NOT NULL,
    cantidad      INT          NOT NULL,
    fecha         DATE         NOT NULL DEFAULT (CURRENT_DATE),
    referencia    VARCHAR(100) NULL,
    CONSTRAINT fk_mov_stk      FOREIGN KEY (id_stock)   REFERENCES inv_stock(id_stock),
    CONSTRAINT fk_mov_emp      FOREIGN KEY (id_emp_inv) REFERENCES rh_empleado_inventario(num),
    CONSTRAINT chk_movinv_tipo CHECK (tipo     IN ('ENTRADA','SALIDA')),
    CONSTRAINT chk_movinv_cant CHECK (cantidad >  0)
);

CREATE TABLE inv_materia_prima (
    id_materia  INT          AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    unidad      VARCHAR(20)  NOT NULL,
    descripcion VARCHAR(200) NULL,
    activo      TINYINT(1)   NOT NULL DEFAULT 1
);

CREATE TABLE inv_stock_mp (
    id_stock_mp     INT          AUTO_INCREMENT PRIMARY KEY,
    id_materia      INT          NOT NULL,
    cantidad_actual INT          NOT NULL DEFAULT 0,
    stock_minimo    INT          NOT NULL DEFAULT 0,
    ubicacion       VARCHAR(100) NULL,
    CONSTRAINT fk_smp_mat    FOREIGN KEY (id_materia) REFERENCES inv_materia_prima(id_materia),
    CONSTRAINT chk_smp_cant  CHECK (cantidad_actual >= 0)
);

CREATE TABLE inv_movimiento_mp (
    id_mov_mp   INT          AUTO_INCREMENT PRIMARY KEY,
    id_stock_mp INT          NOT NULL,
    tipo        VARCHAR(10)  NOT NULL,
    cantidad    INT          NOT NULL,
    fecha       DATE         NOT NULL DEFAULT (CURRENT_DATE),
    referencia  VARCHAR(100) NULL,
    CONSTRAINT fk_mmp_stk      FOREIGN KEY (id_stock_mp) REFERENCES inv_stock_mp(id_stock_mp),
    CONSTRAINT chk_movmp_tipo  CHECK (tipo     IN ('ENTRADA','SALIDA')),
    CONSTRAINT chk_movmp_cant  CHECK (cantidad >  0)
);

-- ============================================================
-- 5. COMPRAS
-- ============================================================

CREATE TABLE cmp_orden_compra (
    id_orden     INT           AUTO_INCREMENT PRIMARY KEY,
    id_proveedor INT           NOT NULL,
    id_empleado  INT           NOT NULL,
    fecha        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado       VARCHAR(25)   NOT NULL DEFAULT 'Pendiente',
    total        DECIMAL(12,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_oc_prov   FOREIGN KEY (id_proveedor) REFERENCES crm_proveedor(id_proveedor),
    CONSTRAINT fk_oc_emp    FOREIGN KEY (id_empleado)  REFERENCES rh_empleado_inventario(num),
    CONSTRAINT chk_oc_estado CHECK (estado IN ('Pendiente','Recibido Parcial','Recibido','Pagado','Cancelado'))
);

CREATE TABLE cmp_detalle_orden (
    id_orden          INT           NOT NULL,
    id_producto       INT           NOT NULL,
    cantidad_pedida   INT           NOT NULL,
    cantidad_recibida INT           NOT NULL DEFAULT 0,
    precio_unitario   DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id_orden, id_producto),
    CONSTRAINT fk_doc_ord  FOREIGN KEY (id_orden)    REFERENCES cmp_orden_compra(id_orden),
    CONSTRAINT fk_doc_prod FOREIGN KEY (id_producto) REFERENCES inv_producto(id_producto),
    CONSTRAINT chk_doc_ped CHECK (cantidad_pedida   >  0),
    CONSTRAINT chk_doc_rec CHECK (cantidad_recibida >= 0)
);

-- ============================================================
-- 6. VENTAS
-- ============================================================

CREATE TABLE ven_promocion (
    id_promocion    INT          AUTO_INCREMENT PRIMARY KEY,
    id_producto     INT          NOT NULL,
    nombre          VARCHAR(100) NOT NULL,
    porcentaje_desc DECIMAL(5,2) NOT NULL,
    fecha_inicio    DATE         NULL,
    fecha_fin       DATE         NULL,
    activa          TINYINT(1)   NOT NULL DEFAULT 1,
    CONSTRAINT fk_prom_prod  FOREIGN KEY (id_producto) REFERENCES inv_producto(id_producto),
    CONSTRAINT chk_prom_porc CHECK (porcentaje_desc BETWEEN 0 AND 100),
    CONSTRAINT chk_prom_fech CHECK (fecha_fin IS NULL OR fecha_inicio IS NULL
                                    OR fecha_fin >= fecha_inicio)
);

CREATE TABLE ven_venta (
    id_venta           INT           AUTO_INCREMENT PRIMARY KEY,
    id_cliente         INT           NOT NULL,
    id_empleado        INT           NOT NULL,
    subtotal           DECIMAL(12,2) NOT NULL DEFAULT 0,
    descuento_aplicado DECIMAL(12,2) NOT NULL DEFAULT 0,
    total              DECIMAL(12,2) NOT NULL DEFAULT 0,
    fecha              DATE          NOT NULL DEFAULT (CURRENT_DATE),
    estatus            VARCHAR(15)   NOT NULL DEFAULT 'Activa',
    condicion_pago     VARCHAR(10)   NOT NULL DEFAULT 'Contado',
    CONSTRAINT fk_vta_cli   FOREIGN KEY (id_cliente)  REFERENCES crm_cliente(id_cliente),
    CONSTRAINT fk_vta_emp   FOREIGN KEY (id_empleado) REFERENCES rh_empleado_ventas(num),
    CONSTRAINT chk_vta_est  CHECK (estatus        IN ('Activa','Liquidada','Cancelada')),
    CONSTRAINT chk_vta_cond CHECK (condicion_pago IN ('Contado','Credito'))
);

CREATE TABLE ven_venta_detalle (
    id_detalle         INT           AUTO_INCREMENT PRIMARY KEY,
    id_venta           INT           NOT NULL,
    id_producto        INT           NOT NULL,
    id_promocion       INT           NULL,
    cantidad           INT           NOT NULL,
    precio_unitario    DECIMAL(10,2) NOT NULL,
    descuento_aplicado DECIMAL(10,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_vd_vta   FOREIGN KEY (id_venta)     REFERENCES ven_venta(id_venta),
    CONSTRAINT fk_vd_prod  FOREIGN KEY (id_producto)  REFERENCES inv_producto(id_producto),
    CONSTRAINT fk_vd_prom  FOREIGN KEY (id_promocion) REFERENCES ven_promocion(id_promocion)
        ON DELETE SET NULL,
    CONSTRAINT chk_vd_cant CHECK (cantidad > 0)
);

-- UNIQUE KEY corregido — necesario para ON DUPLICATE KEY UPDATE del trigger
CREATE TABLE ven_venta_promocion (
    id_venta_promo  INT           AUTO_INCREMENT PRIMARY KEY,
    id_venta        INT           NOT NULL,
    id_promocion    INT           NOT NULL,
    descuento_total DECIMAL(12,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_vp_vta  FOREIGN KEY (id_venta)     REFERENCES ven_venta(id_venta),
    CONSTRAINT fk_vp_prom FOREIGN KEY (id_promocion) REFERENCES ven_promocion(id_promocion),
    CONSTRAINT uq_vp_venta_promo UNIQUE (id_venta, id_promocion)
);

CREATE TABLE ven_cobro (
    id_cobro   INT           AUTO_INCREMENT PRIMARY KEY,
    id_venta   INT           NOT NULL,
    id_cliente INT           NOT NULL,
    monto      DECIMAL(12,2) NOT NULL,
    fecha      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cob_vta   FOREIGN KEY (id_venta)   REFERENCES ven_venta(id_venta),
    CONSTRAINT fk_cob_cli   FOREIGN KEY (id_cliente) REFERENCES crm_cliente(id_cliente),
    CONSTRAINT chk_cob_monto CHECK (monto > 0)
);

CREATE TABLE ven_devolucion (
    id_devolucion  INT           AUTO_INCREMENT PRIMARY KEY,
    id_venta       INT           NOT NULL,
    id_producto    INT           NOT NULL,
    cantidad       INT           NOT NULL,
    monto_devuelto DECIMAL(12,2) NOT NULL,
    motivo         VARCHAR(200)  NOT NULL,
    fecha          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dev_vta   FOREIGN KEY (id_venta)    REFERENCES ven_venta(id_venta),
    CONSTRAINT fk_dev_prod  FOREIGN KEY (id_producto) REFERENCES inv_producto(id_producto),
    CONSTRAINT chk_dev_cant CHECK (cantidad > 0)
);

-- ============================================================
-- 7. PRODUCCIÓN
-- ============================================================

CREATE TABLE prd_orden_produccion (
    id_orden_prod        INT         AUTO_INCREMENT PRIMARY KEY,
    id_empleado          INT         NOT NULL,
    id_producto_final    INT         NOT NULL,
    cantidad_planificada INT         NOT NULL,
    cantidad_producida   INT         NULL DEFAULT 0,
    fecha_inicio         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_estimada_fin   DATE        NULL,
    fecha_real_fin       DATETIME    NULL,
    estado               VARCHAR(15) NOT NULL DEFAULT 'En Proceso',
    CONSTRAINT fk_op_emp   FOREIGN KEY (id_empleado)       REFERENCES rh_empleado_produccion(num),
    CONSTRAINT fk_op_prod  FOREIGN KEY (id_producto_final) REFERENCES inv_producto(id_producto),
    CONSTRAINT chk_op_est  CHECK (estado IN ('En Proceso','Completada','Cancelada')),
    CONSTRAINT chk_op_cant CHECK (cantidad_planificada > 0)
);

CREATE TABLE prd_orden_detalle (
    id_det_prod        INT AUTO_INCREMENT PRIMARY KEY,
    id_orden_prod      INT NOT NULL,
    id_materia         INT NOT NULL,
    id_producto        INT NOT NULL,
    cantidad_mp_usada  INT NOT NULL,
    cantidad_producida INT NOT NULL,
    CONSTRAINT fk_opd_ord  FOREIGN KEY (id_orden_prod) REFERENCES prd_orden_produccion(id_orden_prod),
    CONSTRAINT fk_opd_mat  FOREIGN KEY (id_materia)    REFERENCES inv_materia_prima(id_materia),
    CONSTRAINT fk_opd_pro  FOREIGN KEY (id_producto)   REFERENCES inv_producto(id_producto),
    CONSTRAINT chk_opd_mp  CHECK (cantidad_mp_usada  > 0),
    CONSTRAINT chk_opd_prd CHECK (cantidad_producida >= 0)
);

-- ============================================================
-- 8. TRIGGERS CORREGIDOS
-- ============================================================

DELIMITER $$

-- Actualiza inv_stock al insertar en inv_movimiento
-- FIX v2.1: guardia SIGNAL que impide stock negativo
CREATE TRIGGER trg_actualiza_stock
AFTER INSERT ON inv_movimiento
FOR EACH ROW
BEGIN
    IF NEW.tipo = 'ENTRADA' THEN
        UPDATE inv_stock
           SET cantidad_actual = cantidad_actual + NEW.cantidad
         WHERE id_stock = NEW.id_stock;

    ELSEIF NEW.tipo = 'SALIDA' THEN
        IF (SELECT cantidad_actual FROM inv_stock WHERE id_stock = NEW.id_stock) < NEW.cantidad THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Stock insuficiente para registrar la salida';
        END IF;
        UPDATE inv_stock
           SET cantidad_actual = cantidad_actual - NEW.cantidad
         WHERE id_stock = NEW.id_stock;
    END IF;
END$$

-- Actualiza inv_stock_mp al insertar en inv_movimiento_mp
-- FIX v2.1: guardia SIGNAL que impide stock negativo
CREATE TRIGGER trg_actualiza_stock_mp
AFTER INSERT ON inv_movimiento_mp
FOR EACH ROW
BEGIN
    IF NEW.tipo = 'ENTRADA' THEN
        UPDATE inv_stock_mp
           SET cantidad_actual = cantidad_actual + NEW.cantidad
         WHERE id_stock_mp = NEW.id_stock_mp;

    ELSEIF NEW.tipo = 'SALIDA' THEN
        IF (SELECT cantidad_actual FROM inv_stock_mp WHERE id_stock_mp = NEW.id_stock_mp) < NEW.cantidad THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Stock de materia prima insuficiente para registrar la salida';
        END IF;
        UPDATE inv_stock_mp
           SET cantidad_actual = cantidad_actual - NEW.cantidad
         WHERE id_stock_mp = NEW.id_stock_mp;
    END IF;
END$$

-- Sincroniza ven_venta_promocion y recalcula totales en ven_venta
-- FIX v2.1: funciona correctamente gracias a uq_vp_venta_promo en ven_venta_promocion
CREATE TRIGGER trg_sincroniza_venta_totales
AFTER INSERT ON ven_venta_detalle
FOR EACH ROW
BEGIN
    IF NEW.id_promocion IS NOT NULL THEN
        INSERT INTO ven_venta_promocion (id_venta, id_promocion, descuento_total)
        VALUES (NEW.id_venta, NEW.id_promocion, NEW.descuento_aplicado)
        ON DUPLICATE KEY UPDATE
            descuento_total = descuento_total + NEW.descuento_aplicado;
    END IF;

    UPDATE ven_venta v
       SET v.subtotal = (
               SELECT SUM(precio_unitario * cantidad)
                 FROM ven_venta_detalle WHERE id_venta = NEW.id_venta
           ),
           v.descuento_aplicado = (
               SELECT SUM(descuento_aplicado)
                 FROM ven_venta_detalle WHERE id_venta = NEW.id_venta
           ),
           v.total = (
               SELECT SUM(precio_unitario * cantidad) - SUM(descuento_aplicado)
                 FROM ven_venta_detalle WHERE id_venta = NEW.id_venta
           )
     WHERE v.id_venta = NEW.id_venta;
END$$

DELIMITER ;

-- ============================================================
-- 9. ÍNDICES
-- ============================================================

CREATE INDEX IX_Ventas_Cliente   ON ven_venta           (id_cliente,   fecha DESC);
CREATE INDEX IX_Ventas_Empleado  ON ven_venta           (id_empleado,  fecha DESC);
CREATE INDEX IX_VentasDet_Venta  ON ven_venta_detalle   (id_venta);
CREATE INDEX IX_Promo_Producto   ON ven_promocion        (id_producto,  activa);
CREATE INDEX IX_DescCli_Cliente  ON crm_cliente_descuento(id_cliente,   activo);
CREATE INDEX IX_Producto_Nombre  ON inv_producto         (nombre,       id_categoria);
CREATE INDEX IX_Stock_Producto   ON inv_stock            (id_producto);
CREATE INDEX IX_MovInv_Stock     ON inv_movimiento       (id_stock,     fecha DESC);
CREATE INDEX IX_OC_Proveedor     ON cmp_orden_compra     (id_proveedor, fecha DESC);
CREATE INDEX IX_OP_EmpEstado     ON prd_orden_produccion (id_empleado,  estado, fecha_inicio DESC);
CREATE INDEX IX_MovMP_StockMP    ON inv_movimiento_mp    (id_stock_mp,  fecha DESC);
CREATE INDEX IX_Nomina_Empleado  ON rh_nomina            (id_empleado,  periodo DESC);
CREATE INDEX IX_Cobro_Venta      ON ven_cobro            (id_venta);

-- ============================================================
-- 10. SEED DATA
-- ============================================================

-- Catálogos
INSERT INTO cat_categoria_producto (nombre, descripcion) VALUES
    ('Pantalones', 'Línea de pantalones de producción propia'),
    ('Camisas',    'Línea de camisas'),
    ('Accesorios', 'Cinturones, gorras y complementos');

-- Empleados
INSERT INTO rh_empleado (nombre, contrasena, tipo_empleado) VALUES
    ('Carlos López', 'hash_1', 'Ventas'),
    ('María Garza',  'hash_2', 'RH'),
    ('Pedro Reyes',  'hash_3', 'Inventario'),
    ('Ana Torres',   'hash_4', 'Produccion');

INSERT INTO rh_empleado_ventas     VALUES (1);
INSERT INTO rh_empleado_rh         VALUES (2);
INSERT INTO rh_empleado_inventario VALUES (3);
INSERT INTO rh_empleado_produccion VALUES (4);

-- Clientes
INSERT INTO crm_cliente (nombre, direccion, rfc) VALUES
    ('Tienda Norte SA',   'Av. Juárez 100, Torreón', 'TNS900101AAA'),
    ('Distribuidora Sur', 'Blvd. Revolución 45',      'DSR850615BBB');

INSERT INTO crm_cliente_contacto (id_cliente, tipo, telefono, email) VALUES
    (1, 'Principal', '871-000-0001', 'contacto@tiendasnorte.mx'),
    (2, 'Principal', '871-000-0002', 'ventas@distrisur.mx');

INSERT INTO crm_cliente_descuento (id_cliente, descuento, descripcion) VALUES
    (1, 5.00, 'Descuento general Tienda Norte');

-- Proveedor
INSERT INTO crm_proveedor (nombre, rfc, direccion) VALUES
    ('Telas y Más SA', 'TMA920310CCC', 'Zona Industrial, Gómez Palacio');

INSERT INTO crm_proveedor_contacto (id_proveedor, nombre_contacto, telefono, email) VALUES
    (1, 'Roberto Méndez', '871-100-2000', 'rmendez@telasymas.mx');

-- Materia prima
INSERT INTO inv_materia_prima (nombre, unidad, descripcion) VALUES
    ('Tela Mezclilla',  'metros', 'Mezclilla azul estándar 14 oz'),
    ('Hilo Negro',      'rollos', 'Hilo de poliéster calibre 40'),
    ('Cierre Metálico', 'piezas', 'Cierre YKK 18 cm');

INSERT INTO inv_stock_mp (id_materia, cantidad_actual, stock_minimo, ubicacion) VALUES
    (1, 500, 100, 'Almacén A'),
    (2,  80,  20, 'Almacén A'),
    (3, 300,  50, 'Almacén B');

-- Productos y stock
INSERT INTO inv_producto (nombre, descripcion, precio_unitario, id_categoria) VALUES
    ('Pantalón Clásico Azul', 'Corte recto, talla estándar', 350.00, 1),
    ('Pantalón Slim Negro',   'Corte slim fit',               420.00, 1),
    ('Camisa Blanca Oxford',  'Manga larga, 100% algodón',    280.00, 2);

INSERT INTO inv_stock (id_producto, cantidad_actual, stock_minimo, stock_maximo, ubicacion) VALUES
    (1, 50, 10, 200, 'Bodega 1'),
    (2, 30, 10, 150, 'Bodega 1'),
    (3, 20,  5, 100, 'Bodega 2');

-- Promociones
INSERT INTO ven_promocion (id_producto, nombre, porcentaje_desc, fecha_inicio, fecha_fin, activa) VALUES
    (2, 'Promoción Slim Mayo',  8.00, '2026-05-01', '2026-05-31', 1),
    (1, 'Descuento Clásico Q2', 5.00, '2026-04-01', '2026-06-30', 1);

-- Nómina
INSERT INTO rh_nomina (id_empleado, salario_base, deducciones, periodo) VALUES
    (1, 8000.00, 600.00, '2026-05-01'),
    (2, 9500.00, 712.50, '2026-05-01'),
    (3, 7500.00, 562.50, '2026-05-01'),
    (4, 7000.00, 525.00, '2026-05-01');

-- ============================================================
-- VERIFICACIÓN FINAL
-- ============================================================
SELECT tabla, COUNT(*) AS registros FROM (
    SELECT 'cat_categoria_producto' AS tabla FROM cat_categoria_producto UNION ALL
    SELECT 'rh_empleado'                      FROM rh_empleado            UNION ALL
    SELECT 'crm_cliente'                       FROM crm_cliente            UNION ALL
    SELECT 'crm_proveedor'                     FROM crm_proveedor          UNION ALL
    SELECT 'inv_materia_prima'                 FROM inv_materia_prima      UNION ALL
    SELECT 'inv_stock_mp'                      FROM inv_stock_mp           UNION ALL
    SELECT 'inv_producto'                      FROM inv_producto           UNION ALL
    SELECT 'inv_stock'                         FROM inv_stock              UNION ALL
    SELECT 'ven_promocion'                     FROM ven_promocion          UNION ALL
    SELECT 'rh_nomina'                         FROM rh_nomina
) t GROUP BY tabla;

SET FOREIGN_KEY_CHECKS = 1;
