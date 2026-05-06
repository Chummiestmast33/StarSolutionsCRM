# Casos de Uso — Sistema CRM/ERP

**Proyecto:** Sistema de Gestión Comercial, Ventas y Producción  
**Versión:** 1.0  
**Módulos:** Ventas · Inventario · Compras · Producción · Recursos Humanos

---

## Índice

1. [Módulo Ventas](#módulo-ventas)
   - CU-V01 Registrar Venta
   - CU-V02 Consultar Historial de Compras de un Cliente
   - CU-V03 Registrar Devolución
   - CU-V04 Aplicar Descuento en Venta
   - CU-V05 Registrar Cobro de Venta
2. [Módulo Inventario](#módulo-inventario)
   - CU-I01 Dar de Alta un Producto
   - CU-I02 Buscar Producto
   - CU-I03 Modificar Producto
   - CU-I04 Dar de Baja un Producto
   - CU-I05 Consultar Stock de un Producto
   - CU-I06 Registrar Ajuste de Inventario
3. [Módulo Compras](#módulo-compras)
   - CU-C01 Crear Orden de Compra
   - CU-C02 Registrar Recepción de Mercancía
   - CU-C03 Consultar Historial de Compras a Proveedor
   - CU-C04 Alta / Baja / Modificación de Proveedor
4. [Módulo Producción](#módulo-producción)
   - CU-P01 Crear Orden de Producción
   - CU-P02 Completar Orden de Producción
   - CU-P03 Consultar Órdenes de Producción
5. [Módulo Recursos Humanos](#módulo-recursos-humanos)
   - CU-RH01 Dar de Alta un Empleado
   - CU-RH02 Dar de Baja un Empleado
   - CU-RH03 Modificar Datos de Empleado
   - CU-RH04 Consultar Indicadores de Desempeño

---

## Módulo Ventas

---

### CU-V01 — Registrar Venta

| Campo | Descripción |
|---|---|
| **ID** | CU-V01 |
| **Nombre** | Registrar Venta |
| **Actor principal** | Empleado de Ventas |
| **Actores secundarios** | Sistema de Inventario |
| **Prioridad** | Alta |

**Descripción:**  
El empleado de ventas registra una nueva venta a un cliente, seleccionando los productos y cantidades deseadas. El sistema calcula el total aplicando descuentos vigentes y actualiza el stock.

**Precondiciones:**
- El empleado debe haber iniciado sesión con rol de Ventas.
- El cliente debe estar registrado en el sistema.
- Al menos un producto con stock disponible debe existir.

**Flujo Principal:**

1. El empleado selecciona la opción "Nueva Venta".
2. El sistema solicita identificar al cliente (por nombre, número o RFC).
3. El empleado busca y selecciona al cliente.
4. El sistema muestra los datos del cliente y sus descuentos vigentes.
5. El empleado agrega productos a la venta (código, nombre o categoría).
6. Por cada producto, el sistema verifica el stock disponible.
7. El sistema aplica el descuento correspondiente según la regla de prioridad:
   - Primero: descuento específico (cliente + producto).
   - Segundo: descuento general del cliente.
   - Tercero: sin descuento (precio normal).
8. El sistema calcula el subtotal por línea y el total general.
9. El empleado selecciona la condición de pago (contado / crédito).
10. El empleado confirma la venta.
11. El sistema registra la venta, descuenta el stock de cada producto y genera el número de venta.
12. El sistema muestra el resumen de la venta registrada.

**Flujos Alternos:**

- **FA-1 (paso 6 — stock insuficiente):** El sistema notifica que la cantidad solicitada supera el stock disponible. El empleado puede ajustar la cantidad o eliminar el producto de la línea.
- **FA-2 (paso 3 — cliente no encontrado):** El sistema informa que el cliente no existe. El empleado puede registrar un nuevo cliente o cancelar la operación.
- **FA-3 (paso 10 — cancelación):** El empleado cancela antes de confirmar. El sistema descarta los datos y no genera registro alguno.

**Postcondiciones:**
- La venta queda registrada con un número único.
- El stock de cada producto se reduce según las cantidades vendidas.
- Si la condición es crédito, queda pendiente un cobro en `VentasCobro`.

---

### CU-V02 — Consultar Historial de Compras de un Cliente

| Campo | Descripción |
|---|---|
| **ID** | CU-V02 |
| **Nombre** | Consultar Historial de Compras de un Cliente |
| **Actor principal** | Empleado de Ventas |
| **Prioridad** | Alta |

**Descripción:**  
El empleado consulta todas las ventas realizadas a un cliente específico, con la posibilidad de filtrar por fecha, número de venta o monto.

**Precondiciones:**
- El empleado debe haber iniciado sesión.
- El cliente debe estar registrado en el sistema.

**Flujo Principal:**

1. El empleado selecciona "Consultar Ventas por Cliente".
2. El sistema presenta campos de búsqueda: nombre, número de cliente o RFC.
3. El empleado ingresa el criterio y ejecuta la búsqueda.
4. El sistema muestra la lista de ventas del cliente ordenadas por fecha descendente.
5. El empleado puede aplicar filtros adicionales: rango de fechas, monto mínimo / monto máximo, número de venta.
6. El sistema actualiza la lista con los resultados filtrados.
7. El empleado selecciona una venta para ver su detalle completo (productos, cantidades, descuentos aplicados, estado de cobro).

**Flujos Alternos:**

- **FA-1 (paso 4 — sin resultados):** El sistema muestra el mensaje "El cliente no tiene ventas registradas en el período seleccionado."
- **FA-2 (paso 3 — varios clientes coinciden):** El sistema muestra una lista de coincidencias para que el empleado elija el cliente correcto.

**Postcondiciones:**
- No se modifica ningún dato. Es una operación de solo lectura.

---

### CU-V03 — Registrar Devolución

| Campo | Descripción |
|---|---|
| **ID** | CU-V03 |
| **Nombre** | Registrar Devolución |
| **Actor principal** | Empleado de Ventas |
| **Actores secundarios** | Sistema de Inventario |
| **Prioridad** | Media |

**Descripción:**  
El empleado registra la devolución parcial o total de una venta previamente realizada, indicando el motivo y los productos devueltos.

**Precondiciones:**
- El empleado debe haber iniciado sesión con rol de Ventas.
- La venta asociada debe estar registrada en el sistema.

**Flujo Principal:**

1. El empleado selecciona "Registrar Devolución".
2. El sistema solicita el número de venta.
3. El empleado ingresa el número y el sistema muestra el detalle de la venta.
4. El empleado selecciona los productos a devolver y las cantidades.
5. El empleado ingresa el motivo de la devolución.
6. El sistema calcula el monto a devolver aplicando los mismos descuentos originales.
7. El empleado confirma la devolución.
8. El sistema registra la devolución, incrementa el stock de los productos devueltos y genera el registro en `Devolucion`.

**Flujos Alternos:**

- **FA-1 (paso 2 — venta no encontrada):** El sistema informa que el número no existe y solicita verificarlo.
- **FA-2 (paso 4 — devolución mayor a lo vendido):** El sistema valida que la cantidad a devolver no supere la cantidad original vendida y muestra un error si se intenta.

**Postcondiciones:**
- La devolución queda registrada con fecha y motivo.
- El stock de los productos devueltos se incrementa.
- Se genera un registro de ajuste de monto en `VentasCobro` si ya había cobro registrado.

---

### CU-V04 — Aplicar Descuento en Venta

| Campo | Descripción |
|---|---|
| **ID** | CU-V04 |
| **Nombre** | Aplicar Descuento en Venta |
| **Actor principal** | Empleado de Ventas |
| **Actores secundarios** | Empleado de RH (gestión de descuentos) |
| **Prioridad** | Alta |

**Descripción:**  
El sistema aplica automáticamente el descuento vigente correspondiente al cliente y/o al producto al momento de registrar una línea de venta. Los descuentos son gestionados por RH o el administrador, no por el vendedor.

**Precondiciones:**
- Debe existir al menos un descuento registrado y activo en la tabla `Descuento`.
- La venta está en proceso (CU-V01 activo).

**Flujo Principal:**

1. Al agregar un producto en la venta (CU-V01, paso 5), el sistema consulta la tabla `Descuento`.
2. El sistema evalúa las reglas en orden de prioridad:
   - **Prioridad 1:** Existe fila con `idCliente = X` **y** `idProducto = Y` → se aplica ese descuento.
   - **Prioridad 2:** Existe fila con `idCliente = X` **y** `idProducto = NULL` → descuento general del cliente.
   - **Prioridad 3:** No existe ninguna coincidencia → precio sin descuento.
3. El sistema verifica que el descuento esté activo (`Activo = 1`) y dentro de su rango de fechas (`FechaInicio` / `FechaFin`).
4. El sistema aplica el porcentaje al precio unitario y calcula el subtotal de la línea.
5. El descuento aplicado se muestra al empleado junto con el precio original y el precio con descuento.

**Flujos Alternos:**

- **FA-1 (descuento expirado):** Si el descuento existe pero está fuera de rango de fechas, el sistema lo ignora y aplica la siguiente prioridad o precio normal.
- **FA-2 (descuento inactivo):** Si `Activo = 0`, el sistema lo omite aunque coincida con el cliente y producto.

**Postcondiciones:**
- El descuento aplicado queda registrado en `VentasProducto` junto con el costo resultante.

---

### CU-V05 — Registrar Cobro de Venta

| Campo | Descripción |
|---|---|
| **ID** | CU-V05 |
| **Nombre** | Registrar Cobro de Venta |
| **Actor principal** | Empleado de Ventas |
| **Prioridad** | Alta |

**Descripción:**  
El empleado registra el pago (total o parcial) de una venta con condición de crédito.

**Precondiciones:**
- La venta debe estar registrada y tener saldo pendiente en `VentasCobro`.

**Flujo Principal:**

1. El empleado selecciona "Registrar Cobro".
2. El sistema solicita el número de venta o el nombre del cliente.
3. El empleado busca y selecciona la venta con saldo pendiente.
4. El sistema muestra el total de la venta, los cobros anteriores y el saldo restante.
5. El empleado ingresa el monto recibido y la fecha del cobro.
6. El sistema valida que el monto no supere el saldo pendiente.
7. El empleado confirma el cobro.
8. El sistema registra el pago en `VentasCobro` y actualiza el saldo.

**Flujos Alternos:**

- **FA-1 (monto superior al saldo):** El sistema muestra el saldo actual y no permite registrar un cobro mayor.
- **FA-2 (venta ya liquidada):** El sistema informa que la venta ya está pagada en su totalidad.

**Postcondiciones:**
- El cobro queda registrado con fecha y monto.
- Si el saldo llega a cero, la venta se marca como liquidada.

---

## Módulo Inventario

---

### CU-I01 — Dar de Alta un Producto

| Campo | Descripción |
|---|---|
| **ID** | CU-I01 |
| **Nombre** | Dar de Alta un Producto |
| **Actor principal** | Empleado de Inventario |
| **Prioridad** | Alta |

**Descripción:**  
El empleado de inventario registra un nuevo producto en el sistema con su información básica, precio y categoría.

**Precondiciones:**
- El empleado debe haber iniciado sesión con rol de Inventario.
- Debe existir al menos una categoría registrada.

**Flujo Principal:**

1. El empleado selecciona "Nuevo Producto".
2. El sistema presenta el formulario: nombre, descripción, precio unitario, categoría, stock inicial y stock mínimo.
3. El empleado completa los campos y confirma.
4. El sistema valida que no exista otro producto con el mismo nombre en la misma categoría.
5. El sistema registra el producto y crea el registro de stock inicial.
6. El sistema confirma el alta con el número de producto generado.

**Flujos Alternos:**

- **FA-1 (producto duplicado):** El sistema alerta que ya existe un producto con ese nombre en la categoría indicada y solicita verificar.
- **FA-2 (datos inválidos):** El sistema resalta los campos con error (precio negativo, campos vacíos obligatorios).

**Postcondiciones:**
- El producto queda registrado y disponible para ventas y producción.
- El stock queda inicializado en `Stock`.

---

### CU-I02 — Buscar Producto

| Campo | Descripción |
|---|---|
| **ID** | CU-I02 |
| **Nombre** | Buscar Producto |
| **Actor principal** | Empleado de Inventario, Empleado de Ventas |
| **Prioridad** | Alta |

**Descripción:**  
El empleado busca un producto usando distintos criterios de búsqueda.

**Precondiciones:**
- El empleado debe haber iniciado sesión.

**Flujo Principal:**

1. El empleado accede al catálogo de productos.
2. El sistema muestra los criterios de búsqueda: nombre, categoría, código de producto o rango de precio.
3. El empleado ingresa uno o más criterios.
4. El sistema muestra los resultados coincidentes con nombre, categoría, precio y stock actual.
5. El empleado puede seleccionar un producto para ver su detalle completo.

**Flujos Alternos:**

- **FA-1 (sin resultados):** El sistema informa que no se encontraron productos con los criterios dados y sugiere ampliar la búsqueda.

**Postcondiciones:**
- Operación de solo lectura. No se modifica ningún dato.

---

### CU-I03 — Modificar Producto

| Campo | Descripción |
|---|---|
| **ID** | CU-I03 |
| **Nombre** | Modificar Producto |
| **Actor principal** | Empleado de Inventario |
| **Prioridad** | Media |

**Descripción:**  
El empleado actualiza los datos de un producto existente (nombre, descripción, precio o categoría).

**Precondiciones:**
- El empleado debe haber iniciado sesión con rol de Inventario.
- El producto debe existir y estar activo.

**Flujo Principal:**

1. El empleado busca el producto (CU-I02).
2. El empleado selecciona "Editar".
3. El sistema carga el formulario con los datos actuales.
4. El empleado modifica los campos necesarios.
5. El empleado confirma los cambios.
6. El sistema valida los nuevos datos y guarda los cambios.
7. El sistema confirma la modificación exitosa.

**Flujos Alternos:**

- **FA-1 (datos inválidos):** El sistema resalta los campos con error y no guarda hasta que sean corregidos.
- **FA-2 (cancelación):** El empleado cancela; el sistema descarta los cambios y mantiene los datos originales.

**Postcondiciones:**
- Los datos del producto quedan actualizados.

---

### CU-I04 — Dar de Baja un Producto

| Campo | Descripción |
|---|---|
| **ID** | CU-I04 |
| **Nombre** | Dar de Baja un Producto |
| **Actor principal** | Empleado de Inventario |
| **Prioridad** | Media |

**Descripción:**  
El empleado desactiva un producto del catálogo. El producto no se elimina físicamente para conservar el historial de ventas.

**Precondiciones:**
- El producto debe existir y estar activo.
- El producto no debe tener ventas pendientes de entrega.

**Flujo Principal:**

1. El empleado busca el producto (CU-I02).
2. El empleado selecciona "Dar de Baja".
3. El sistema muestra una confirmación advirtiendo que el producto no estará disponible para nuevas ventas.
4. El empleado confirma.
5. El sistema marca el producto como inactivo (`Activo = 0`).

**Flujos Alternos:**

- **FA-1 (producto con ventas activas):** El sistema impide la baja e informa que el producto tiene transacciones pendientes.
- **FA-2 (cancelación):** El empleado cancela; el producto permanece activo.

**Postcondiciones:**
- El producto queda inactivo y no aparece en ventas ni producción nuevas.
- El historial de ventas anteriores se conserva íntegro.

---

### CU-I05 — Consultar Stock de un Producto

| Campo | Descripción |
|---|---|
| **ID** | CU-I05 |
| **Nombre** | Consultar Stock de un Producto |
| **Actor principal** | Empleado de Inventario |
| **Prioridad** | Alta |

**Descripción:**  
El empleado consulta la cantidad disponible de un producto y verifica si está por debajo del stock mínimo.

**Precondiciones:**
- El empleado debe haber iniciado sesión.

**Flujo Principal:**

1. El empleado busca el producto (CU-I02).
2. El sistema muestra: stock actual, stock mínimo, fecha de última actualización y estado (Normal / Bajo / Crítico).
3. Si el stock está en o por debajo del mínimo, el sistema resalta la alerta visualmente.

**Flujos Alternos:**

- **FA-1 (stock crítico):** El sistema muestra un aviso que recomienda crear una orden de compra al proveedor correspondiente.

**Postcondiciones:**
- Operación de solo lectura.

---

### CU-I06 — Registrar Ajuste de Inventario

| Campo | Descripción |
|---|---|
| **ID** | CU-I06 |
| **Nombre** | Registrar Ajuste de Inventario |
| **Actor principal** | Empleado de Inventario |
| **Prioridad** | Media |

**Descripción:**  
El empleado registra una variación en el inventario no generada por una venta ni por una compra, como merma, producto dañado o ajuste por conteo físico.

**Precondiciones:**
- El empleado debe haber iniciado sesión con rol de Inventario.
- El producto debe existir y estar activo.

**Flujo Principal:**

1. El empleado selecciona "Ajuste de Inventario".
2. El empleado busca el producto.
3. El sistema muestra el stock actual.
4. El empleado ingresa el tipo de ajuste (entrada o salida), la cantidad y el motivo (merma, daño, conteo físico, otro).
5. El empleado confirma el ajuste.
6. El sistema registra el movimiento en `MovimientoInventario` y actualiza el stock.

**Flujos Alternos:**

- **FA-1 (ajuste negativo mayor al stock disponible):** El sistema impide dejar el stock en negativo y solicita verificar la cantidad.

**Postcondiciones:**
- El stock queda actualizado.
- El ajuste queda registrado con fecha, tipo, cantidad y motivo para trazabilidad.

---

## Módulo Compras

---

### CU-C01 — Crear Orden de Compra

| Campo | Descripción |
|---|---|
| **ID** | CU-C01 |
| **Nombre** | Crear Orden de Compra |
| **Actor principal** | Empleado de Inventario |
| **Prioridad** | Alta |

**Descripción:**  
El empleado genera una orden de compra a un proveedor para reabastecer uno o más productos con stock bajo.

**Precondiciones:**
- El proveedor debe estar registrado y activo.
- Los productos a pedir deben estar registrados.

**Flujo Principal:**

1. El empleado selecciona "Nueva Orden de Compra".
2. El sistema muestra los productos con stock bajo o crítico como sugerencia.
3. El empleado selecciona el proveedor.
4. El empleado agrega los productos a pedir con las cantidades deseadas y el precio unitario pactado.
5. El sistema calcula el total de la orden.
6. El empleado confirma la orden.
7. El sistema registra la orden con estado "Pendiente" en `OrdenCompra` y su detalle en `DetalleOrdenCompra`.

**Flujos Alternos:**

- **FA-1 (proveedor no registrado):** El sistema informa que el proveedor no existe y sugiere darlo de alta antes de continuar.
- **FA-2 (cancelación):** El empleado cancela; la orden no se guarda.

**Postcondiciones:**
- La orden queda registrada con estado "Pendiente".
- El stock no se modifica hasta la recepción (CU-C02).

---

### CU-C02 — Registrar Recepción de Mercancía

| Campo | Descripción |
|---|---|
| **ID** | CU-C02 |
| **Nombre** | Registrar Recepción de Mercancía |
| **Actor principal** | Empleado de Inventario |
| **Actores secundarios** | Sistema de Inventario |
| **Prioridad** | Alta |

**Descripción:**  
El empleado confirma que los productos de una orden de compra han sido recibidos físicamente, actualizando el stock.

**Precondiciones:**
- Debe existir una orden de compra con estado "Pendiente".

**Flujo Principal:**

1. El empleado selecciona "Registrar Recepción".
2. El sistema muestra las órdenes de compra pendientes.
3. El empleado selecciona la orden recibida.
4. El sistema muestra el detalle de la orden (productos y cantidades esperadas).
5. El empleado confirma las cantidades realmente recibidas (pueden diferir de lo pedido).
6. El empleado confirma la recepción.
7. El sistema actualiza el stock de cada producto recibido en `Stock` y registra el movimiento en `MovimientoInventario`.
8. El sistema cambia el estado de la orden a "Recibido".

**Flujos Alternos:**

- **FA-1 (recepción parcial):** El empleado ingresa cantidades menores a las pedidas. El sistema registra lo recibido y puede dejar la orden en estado "Recibido parcialmente" para seguimiento.

**Postcondiciones:**
- El stock se incrementa según las cantidades recibidas.
- La orden queda en estado "Recibido" o "Recibido parcialmente".

---

### CU-C03 — Consultar Historial de Compras a Proveedor

| Campo | Descripción |
|---|---|
| **ID** | CU-C03 |
| **Nombre** | Consultar Historial de Compras a Proveedor |
| **Actor principal** | Empleado de Inventario |
| **Prioridad** | Media |

**Descripción:**  
El empleado consulta todas las órdenes de compra realizadas a un proveedor, con filtros por fecha, monto o estado.

**Precondiciones:**
- El empleado debe haber iniciado sesión.

**Flujo Principal:**

1. El empleado selecciona "Historial de Compras por Proveedor".
2. El empleado busca el proveedor (por nombre o número).
3. El sistema muestra las órdenes de compra del proveedor con fecha, total y estado.
4. El empleado puede filtrar por rango de fechas, monto o estado (Pendiente / Recibido / Pagado).
5. El empleado puede seleccionar una orden para ver su detalle de productos y cantidades.

**Flujos Alternos:**

- **FA-1 (sin órdenes):** El sistema informa que el proveedor no tiene órdenes de compra registradas en el período.

**Postcondiciones:**
- Operación de solo lectura.

---

### CU-C04 — Alta / Baja / Modificación de Proveedor

| Campo | Descripción |
|---|---|
| **ID** | CU-C04 |
| **Nombre** | Alta, Baja y Modificación de Proveedor |
| **Actor principal** | Empleado de Inventario |
| **Prioridad** | Media |

**Descripción:**  
El empleado gestiona el catálogo de proveedores: registrar nuevos, actualizar datos y desactivar proveedores que ya no operan.

**Precondiciones:**
- El empleado debe haber iniciado sesión con rol de Inventario.

**Flujo Principal — Alta:**

1. El empleado selecciona "Nuevo Proveedor".
2. El sistema presenta el formulario: nombre, RFC, dirección, teléfono, email y nombre de contacto.
3. El empleado completa los datos y confirma.
4. El sistema valida que no exista otro proveedor con el mismo RFC.
5. El sistema registra el proveedor y confirma el alta.

**Flujo Principal — Baja:**

1. El empleado busca el proveedor.
2. El empleado selecciona "Dar de Baja".
3. El sistema verifica que no existan órdenes de compra pendientes asociadas.
4. El empleado confirma; el sistema marca el proveedor como inactivo.

**Flujo Principal — Modificación:**

1. El empleado busca el proveedor y selecciona "Editar".
2. El sistema carga el formulario con los datos actuales.
3. El empleado modifica los campos necesarios y confirma.
4. El sistema guarda los cambios.

**Flujos Alternos:**

- **FA-1 (baja con órdenes pendientes):** El sistema impide la baja e informa que hay órdenes activas asociadas al proveedor.
- **FA-2 (RFC duplicado en alta):** El sistema alerta que ya existe un proveedor con ese RFC.

**Postcondiciones:**
- El catálogo de proveedores queda actualizado según la operación realizada.

---

## Módulo Producción

---

### CU-P01 — Crear Orden de Producción

| Campo | Descripción |
|---|---|
| **ID** | CU-P01 |
| **Nombre** | Crear Orden de Producción |
| **Actor principal** | Empleado de Producción |
| **Prioridad** | Alta |

**Descripción:**  
El empleado de producción genera una orden para fabricar una cantidad determinada de un producto, registrando las materias primas que se consumirán.

**Precondiciones:**
- El empleado debe haber iniciado sesión con rol de Producción.
- El producto a fabricar debe estar registrado.
- Las materias primas requeridas deben tener stock suficiente.

**Flujo Principal:**

1. El empleado selecciona "Nueva Orden de Producción".
2. El sistema solicita: producto a fabricar, cantidad y fecha estimada de finalización.
3. El empleado ingresa los datos y agrega las materias primas a consumir con sus cantidades.
4. El sistema verifica el stock disponible de cada materia prima.
5. El empleado confirma la orden.
6. El sistema registra la orden con estado "En proceso" en `OrdenProduccion` y el detalle en `OrdenProduccionDetalle`.
7. El sistema descuenta las materias primas del inventario al iniciar la orden.

**Flujos Alternos:**

- **FA-1 (materia prima insuficiente):** El sistema indica cuáles materias primas no tienen stock suficiente. El empleado puede ajustar la cantidad a producir o esperar reabastecimiento.

**Postcondiciones:**
- La orden queda registrada con estado "En proceso".
- Las materias primas quedan reservadas / descontadas del stock.

---

### CU-P02 — Completar Orden de Producción

| Campo | Descripción |
|---|---|
| **ID** | CU-P02 |
| **Nombre** | Completar Orden de Producción |
| **Actor principal** | Empleado de Producción |
| **Actores secundarios** | Sistema de Inventario |
| **Prioridad** | Alta |

**Descripción:**  
El empleado marca una orden de producción como terminada. El sistema agrega los productos fabricados al inventario de productos terminados.

**Precondiciones:**
- Debe existir una orden de producción con estado "En proceso".

**Flujo Principal:**

1. El empleado selecciona "Completar Orden".
2. El sistema muestra las órdenes en proceso asignadas al empleado.
3. El empleado selecciona la orden finalizada.
4. El sistema muestra el detalle: producto, cantidad planificada y materias primas consumidas.
5. El empleado confirma la cantidad real producida (puede diferir de la planificada).
6. El empleado confirma la finalización.
7. El sistema cambia el estado de la orden a "Completada".
8. El sistema incrementa el stock del producto terminado en `Stock`.

**Flujos Alternos:**

- **FA-1 (producción menor a la planificada):** El empleado registra la cantidad real producida. El sistema ajusta el stock en consecuencia y registra la diferencia.

**Postcondiciones:**
- La orden queda en estado "Completada" con fecha de finalización real.
- El stock del producto terminado se incrementa según las unidades producidas.

---

### CU-P03 — Consultar Órdenes de Producción

| Campo | Descripción |
|---|---|
| **ID** | CU-P03 |
| **Nombre** | Consultar Órdenes de Producción |
| **Actor principal** | Empleado de Producción |
| **Prioridad** | Media |

**Descripción:**  
El empleado consulta el listado de órdenes de producción con posibilidad de filtrar por estado, producto, empleado o rango de fechas.

**Precondiciones:**
- El empleado debe haber iniciado sesión.

**Flujo Principal:**

1. El empleado selecciona "Consultar Órdenes de Producción".
2. El sistema muestra todas las órdenes con: número, producto, cantidad, estado y fecha.
3. El empleado aplica filtros: estado (En proceso / Completada), producto, empleado responsable o rango de fechas.
4. El sistema actualiza la lista con los resultados filtrados.
5. El empleado puede seleccionar una orden para ver su detalle completo incluyendo materias primas usadas.

**Postcondiciones:**
- Operación de solo lectura.

---

## Módulo Recursos Humanos

---

### CU-RH01 — Dar de Alta un Empleado

| Campo | Descripción |
|---|---|
| **ID** | CU-RH01 |
| **Nombre** | Dar de Alta un Empleado |
| **Actor principal** | Empleado de RH |
| **Prioridad** | Alta |

**Descripción:**  
El empleado de RH registra a un nuevo empleado en el sistema, asignándole un tipo de rol que determina a qué módulos tendrá acceso.

**Precondiciones:**
- El empleado de RH debe haber iniciado sesión.

**Flujo Principal:**

1. El empleado de RH selecciona "Nuevo Empleado".
2. El sistema presenta el formulario: nombre, contraseña inicial y tipo de empleado (Ventas / RH / Inventario / Producción).
3. El empleado de RH completa los datos y confirma.
4. El sistema asigna un número de empleado único.
5. El sistema registra al empleado en `Empleado` y en la tabla de especialización correspondiente según el tipo.
6. El sistema confirma el alta mostrando el número asignado.

**Flujos Alternos:**

- **FA-1 (datos incompletos):** El sistema resalta los campos obligatorios vacíos y no guarda hasta que estén completos.

**Postcondiciones:**
- El empleado queda registrado y puede iniciar sesión con su número y contraseña.
- Solo tiene acceso al módulo correspondiente a su tipo.

---

### CU-RH02 — Dar de Baja un Empleado

| Campo | Descripción |
|---|---|
| **ID** | CU-RH02 |
| **Nombre** | Dar de Baja un Empleado |
| **Actor principal** | Empleado de RH |
| **Prioridad** | Media |

**Descripción:**  
El empleado de RH desactiva a un empleado que ya no pertenece a la organización. El registro se conserva para mantener el historial de operaciones.

**Precondiciones:**
- El empleado a dar de baja debe existir y estar activo.
- No puede darse de baja a sí mismo.

**Flujo Principal:**

1. El empleado de RH busca al empleado por nombre o número.
2. El empleado de RH selecciona "Dar de Baja".
3. El sistema solicita confirmación advirtiendo que el empleado perderá acceso al sistema.
4. El empleado de RH confirma.
5. El sistema marca al empleado como inactivo.

**Flujos Alternos:**

- **FA-1 (empleado con procesos activos):** El sistema advierte que el empleado tiene órdenes de producción o ventas abiertas y solicita confirmar de todas formas o cancelar.

**Postcondiciones:**
- El empleado queda inactivo y no puede iniciar sesión.
- Su historial de operaciones (ventas, producciones) se conserva íntegro.

---

### CU-RH03 — Modificar Datos de Empleado

| Campo | Descripción |
|---|---|
| **ID** | CU-RH03 |
| **Nombre** | Modificar Datos de Empleado |
| **Actor principal** | Empleado de RH |
| **Prioridad** | Media |

**Descripción:**  
El empleado de RH actualiza los datos de un empleado existente, incluyendo cambio de rol, nombre o contraseña.

**Precondiciones:**
- El empleado a modificar debe existir y estar activo.

**Flujo Principal:**

1. El empleado de RH busca al empleado.
2. El empleado de RH selecciona "Editar".
3. El sistema carga el formulario con los datos actuales.
4. El empleado de RH modifica los campos necesarios (nombre, contraseña o tipo de empleado).
5. Si cambia el tipo de empleado, el sistema advierte que el acceso del empleado cambiará de módulo.
6. El empleado de RH confirma los cambios.
7. El sistema actualiza los datos y, si hubo cambio de rol, mueve el registro a la tabla de especialización correspondiente.

**Flujos Alternos:**

- **FA-1 (cambio de rol con operaciones activas en rol anterior):** El sistema advierte que el empleado tiene tareas abiertas en su rol actual antes de proceder.

**Postcondiciones:**
- Los datos del empleado quedan actualizados.
- Si cambió de rol, el empleado solo tiene acceso al nuevo módulo asignado.

---

### CU-RH04 — Consultar Indicadores de Desempeño

| Campo | Descripción |
|---|---|
| **ID** | CU-RH04 |
| **Nombre** | Consultar Indicadores de Desempeño |
| **Actor principal** | Empleado de RH |
| **Prioridad** | Media |

**Descripción:**  
El empleado de RH consulta los porcentajes de productividad y eficiencia de los empleados registrados en el sistema.

**Precondiciones:**
- El empleado de RH debe haber iniciado sesión.

**Flujo Principal:**

1. El empleado de RH selecciona "Indicadores de Desempeño".
2. El sistema muestra la lista de empleados activos con sus valores de `Productividad_%` y `Eficiencia_%`.
3. El empleado de RH puede filtrar por tipo de empleado o buscar a uno en específico.
4. El empleado de RH puede seleccionar a un empleado para actualizar manualmente sus indicadores.
5. El empleado de RH confirma los nuevos valores.
6. El sistema guarda los cambios.

**Flujos Alternos:**

- **FA-1 (valor fuera de rango):** El sistema valida que los porcentajes estén entre 0 y 100 y muestra error si se ingresa un valor inválido.

**Postcondiciones:**
- Los indicadores del empleado quedan actualizados en la base de datos.

---

*Fin del documento — 21 casos de uso registrados.*