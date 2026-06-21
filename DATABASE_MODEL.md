# Modelo de Base de Datos — INMIA
**Firebase Firestore (NoSQL)**

---

## Colecciones

### `usuarios`
Un documento por usuario, independientemente del rol.

| Campo | Tipo | Descripción |
|---|---|---|
| `uid` | string | Firebase Auth UID |
| `nombres` | string | |
| `apellidos` | string | |
| `tipoDocumento` | string | `"DNI"`, `"Pasaporte"`, `"Carnet de extranjería"` |
| `numDocumento` | string | |
| `fechaNacimiento` | string | Formato `dd/MM/yyyy` |
| `correo` | string | |
| `telefono` | string | |
| `domicilio` | string | |
| `foto` | string | URL de Firebase Storage |
| `rol` | string | `"superadmin"`, `"admin"`, `"asesor"`, `"cliente"` |
| `activo` | boolean | Controlado por el superadmin |
| `fechaCreacion` | timestamp | |

**Campos adicionales según rol:**

| Rol | Campo extra | Tipo | Descripción |
|---|---|---|---|
| `admin` | `inmobiliariaId` | string | ID del documento en `inmobiliarias` |
| `asesor` | `inmobiliariaId` | string | |
| `asesor` | `proyectosAsignados` | array\<string\> | IDs de proyectos asignados |
| `cliente` | `tarjetaRegistrada` | boolean | Necesario para habilitar separaciones |

---

### `solicitudesAsesor`
Solicitudes de registro de asesores pendientes de aprobación por el superadmin.
*(Actualmente en Room como `SolicitudEntity` — migra aquí en Lab 6)*

| Campo | Tipo | Descripción |
|---|---|---|
| `nombre` | string | |
| `apellidos` | string | |
| `inmobiliaria` | string | Nombre de la inmobiliaria a la que pertenece |
| `correo` | string | |
| `telefono` | string | |
| `documento` | string | Ej: `"DNI · 45678901"` |
| `fechaNac` | string | |
| `domicilio` | string | |
| `estado` | string | `"pendiente"`, `"aprobado"`, `"rechazado"` |
| `timestamp` | timestamp | Fecha de envío de la solicitud |

---

### `notificaciones`
Notificaciones in-app por destinatario.
*(Actualmente en Room como `NotificacionSAEntity` — migra aquí en Lab 6)*

| Campo | Tipo | Descripción |
|---|---|---|
| `destinatarioId` | string | UID del usuario que recibe la notificación |
| `titulo` | string | |
| `descripcion` | string | |
| `tipo` | string | Ver tabla de tipos abajo |
| `leida` | boolean | |
| `timestamp` | timestamp | |

**Tipos de notificación definidos:**

| Tipo | Descripción |
|---|---|
| `admin_creado` | El superadmin registró un nuevo administrador |
| `nueva_solicitud_asesor` | Un admin envió una solicitud de registro de asesor |
| `asesor_habilitado` | El superadmin aprobó a un asesor |
| `asesor_rechazado` | El superadmin rechazó a un asesor |
| `usuario_activado` | El superadmin activó un usuario |
| `usuario_desactivado` | El superadmin desactivó un usuario |
| `separacion_registrada` | Un asesor registró una separación (destino: admin) |
| `separacion_aprobada` | El admin aprobó una separación (destino: cliente) |
| `pago_checkout` | El cliente realizó el pago (destino: admin) |

---

### `logs`
Registro de eventos del sistema, visible solo para el superadmin.

| Campo | Tipo | Descripción |
|---|---|---|
| `tipo` | string | `"login"`, `"registro"`, `"habilitacion"`, `"separacion"`, `"desactivacion"`, etc. |
| `descripcion` | string | Descripción legible del evento |
| `usuarioId` | string | UID del usuario que generó el evento |
| `timestamp` | timestamp | |

---

### `inmobiliarias`

| Campo | Tipo | Descripción |
|---|---|---|
| `nombre` | string | |
| `direccion` | string | Dirección textual de las oficinas |
| `ubicacionCoordenadas` | map `{lat, lng}` | Coordenadas para mostrar en mapa |
| `correo` | string | |
| `telefono` | string | |
| `fotos` | array\<string\> | URLs de Firebase Storage (mínimo 2) |
| `adminId` | string | UID del administrador responsable |
| `activo` | boolean | |
| `fechaCreacion` | timestamp | |

---

### `proyectos`

| Campo | Tipo | Descripción |
|---|---|---|
| `nombre` | string | |
| `descripcion` | string | |
| `precio` | number | Precio total del inmueble |
| `precioSeparacion` | number | Costo de separación/reserva |
| `ubicacion` | map `{lat, lng}` | Coordenadas para mostrar en mapa |
| `tipologia` | array\<map\> | Cada elemento: `{metraje, numCuartos}` |
| `areasComunes` | array\<string\> | Ej: `["piscina", "coworking", "parrillas"]` |
| `estado` | string | `"en_planos"`, `"preventa"`, `"venta"` |
| `fechaEntregaEstimada` | string | |
| `imagenes` | array\<string\> | URLs de Firebase Storage (mínimo 2) |
| `inmobiliariaId` | string | |
| `asesoresAsignados` | array\<string\> | UIDs de asesores asignados |
| `codigoQR` | string | URL o contenido del QR del proyecto |

---

### `citas`

| Campo | Tipo | Descripción |
|---|---|---|
| `clienteId` | string | UID del cliente |
| `asesorId` | string | UID del asesor asignado |
| `proyectoId` | string | |
| `fechaHora` | timestamp | Fecha y hora de la visita |
| `estado` | string | `"pendiente"`, `"confirmada"`, `"completada"`, `"cancelada"` |
| `fechaCreacion` | timestamp | |

---

### `separaciones`

| Campo | Tipo | Descripción |
|---|---|---|
| `clienteId` | string | |
| `asesorId` | string | |
| `proyectoId` | string | |
| `inmobiliariaId` | string | |
| `monto` | number | Valor propuesto por el asesor |
| `estado` | string | `"pendiente"`, `"aprobada"`, `"rechazada"`, `"pagada"` |
| `valoracion` | number | Puntuación del cliente al asesor (1-5) |
| `observaciones` | string | Observaciones del cliente sobre la atención |
| `fechaCreacion` | timestamp | |
| `fechaAprobacion` | timestamp | |
| `fechaExpiracionPago` | timestamp | 10 minutos después de la aprobación |

---

### `mensajes`
Chat por inmobiliaria para atención al cliente y seguimiento.

| Campo | Tipo | Descripción |
|---|---|---|
| `inmobiliariaId` | string | Chat asociado a esta inmobiliaria |
| `remitenteId` | string | UID del usuario que envía el mensaje |
| `remitenteRol` | string | `"admin"`, `"asesor"`, `"cliente"` |
| `contenido` | string | Texto del mensaje |
| `timestamp` | timestamp | |
| `leido` | boolean | Si el destinatario ya lo leyó |

---

## Migración Room → Firestore (Lab 5 → Lab 6)

| Room (Lab 5 — actual) | Firestore (Lab 6) |
|---|---|
| `AdminEntity` | colección `usuarios` con `rol: "admin"` |
| `SolicitudEntity` | colección `solicitudesAsesor` |
| `NotificacionSAEntity` | colección `notificaciones` con `destinatarioId` |
| `SessionManager` (SharedPreferences) | se mantiene igual para datos de sesión local |

---

## Notas de diseño

- Todos los roles van en **una sola colección `usuarios`**, diferenciados por el campo `rol`. Esto simplifica las consultas del superadmin para activar/desactivar cualquier tipo de usuario desde un solo lugar.
- Las notificaciones usan `destinatarioId` para que cada rol solo vea las suyas propias — el superadmin ve las de tipo `nueva_solicitud_asesor`, el admin ve `separacion_registrada`, el cliente ve `separacion_aprobada`, etc.
- Los `logs` son de solo lectura para el superadmin; se escriben automáticamente ante cualquier evento relevante del sistema.
- El campo `foto` en `usuarios` almacena la URL de Firebase Storage, no la imagen directamente. Se sube primero al Storage y luego se guarda la URL en Firestore.
- Los `mensajes` son un chat único por inmobiliaria — todos los participantes (admin, asesores y clientes vinculados a esa inmobiliaria) comparten el mismo hilo.
