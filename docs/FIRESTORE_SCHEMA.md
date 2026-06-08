# Firestore schema (NoSQL) - propuesta

Este documento describe la estructura NoSQL propuesta en Firestore y para que sirve cada parte.
Se diseña para multiempresa (multi-tenant), con roles `superadmin`, `admin`, `asesor` y `cliente`.

## Principios

- Multiempresa: toda la data vive bajo una empresa (companyId).
- Roles: un usuario puede pertenecer a una o mas empresas con roles distintos.
- Lecturas por pantalla: cada pantalla consulta 1-3 documentos/colecciones directas.
- Agregados precalculados: reportes usan documentos resumen para evitar consultas pesadas.

## Colecciones principales

### `companies`
**Proposito:** datos base de cada empresa.

**Documento:** `companies/{companyId}`

Campos sugeridos:
- `name` (string): nombre comercial.
- `ruc` (string): identificador fiscal.
- `status` (string): `active`, `inactive`.
- `createdAt` (timestamp)

Subcolecciones comunes:
- `settings`: configuraciones por empresa.
- `counters`: contadores atomicos (por ejemplo, consecutivos).

### `users`
**Proposito:** perfil base de usuarios (sin asumir rol).

**Documento:** `users/{userId}`

Campos sugeridos:
- `email` (string)
- `displayName` (string)
- `phone` (string)
- `photoUrl` (string)
- `status` (string): `active`, `blocked`
- `createdAt` (timestamp)

### `memberships`
**Proposito:** relacion usuario-empresa y rol.

**Documento:** `companies/{companyId}/memberships/{userId}`

Campos sugeridos:
- `role` (string): `superadmin`, `admin`, `asesor`, `cliente`
- `status` (string): `active`, `invited`, `inactive`
- `permissions` (map): overrides de permisos
- `createdAt` (timestamp)

### `projects`
**Proposito:** proyectos inmobiliarios de una empresa.

**Documento:** `companies/{companyId}/projects/{projectId}`

Campos sugeridos:
- `name` (string)
- `location` (map): direccion, distrito, ciudad
- `status` (string): `active`, `paused`, `closed`
- `startDate` (timestamp)
- `endDate` (timestamp)

Subcolecciones comunes:
- `units`: departamentos, lotes, etc.
- `phases`: etapas del proyecto.

### `units`
**Proposito:** unidades disponibles o vendidas.

**Documento:** `companies/{companyId}/projects/{projectId}/units/{unitId}`

Campos sugeridos:
- `code` (string)
- `type` (string)
- `price` (number)
- `status` (string): `available`, `reserved`, `sold`
- `areaM2` (number)

### `leads`
**Proposito:** prospectos y oportunidades de venta.

**Documento:** `companies/{companyId}/leads/{leadId}`

Campos sugeridos:
- `name` (string)
- `email` (string)
- `phone` (string)
- `source` (string)
- `status` (string): `new`, `contacted`, `qualified`, `lost`
- `assignedTo` (ref userId)
- `createdAt` (timestamp)

Subcolecciones comunes:
- `notes`: comentarios del asesor.
- `activities`: llamadas, citas, etc.

### `sales`
**Proposito:** ventas confirmadas.

**Documento:** `companies/{companyId}/sales/{saleId}`

Campos sugeridos:
- `leadId` (ref)
- `projectId` (ref)
- `unitId` (ref)
- `advisorId` (ref userId)
- `amount` (number)
- `status` (string): `open`, `won`, `canceled`
- `createdAt` (timestamp)

### `notifications`
**Proposito:** notificaciones por usuario y empresa.

**Documento:** `companies/{companyId}/notifications/{notificationId}`

Campos sugeridos:
- `userId` (ref)
- `title` (string)
- `body` (string)
- `read` (boolean)
- `createdAt` (timestamp)

### `reports`
**Proposito:** agregados para dashboard y reportes.

**Documento:** `companies/{companyId}/reports/{periodId}`

Campos sugeridos:
- `period` (string): `2026-05`
- `totals` (map): ventas, citas, leads, etc.
- `advisorStats` (map): por asesor
- `updatedAt` (timestamp)

## Colecciones de administracion global

### `system`
**Proposito:** configuracion global y roles del superadmin.

**Documento:** `system/config`

Campos sugeridos:
- `maintenanceMode` (boolean)
- `allowedDomains` (array)
- `createdAt` (timestamp)

### `system/admins`
**Proposito:** lista de superadmins.

**Documento:** `system/admins/{userId}`

Campos sugeridos:
- `email` (string)
- `status` (string)
- `createdAt` (timestamp)

## Indices sugeridos

- `companies/{companyId}/leads` por `status`, `assignedTo`, `createdAt`.
- `companies/{companyId}/sales` por `status`, `advisorId`, `createdAt`.
- `companies/{companyId}/notifications` por `userId`, `read`.

## Como se usa por rol

- `superadmin`: acceso a `system/*` y lectura global limitada de `companies`.
- `admin`: administra `companies/{companyId}` y sus subcolecciones.
- `asesor`: lee proyectos y unidades, escribe en `leads`, `activities` y `sales` asignadas.
- `cliente`: acceso limitado a su `lead` y seguimiento de compra.

## Notas de migracion desde hardcode

- Mantener la interfaz de repositorio actual, cambiando la fuente a Firestore.
- Crear DTOs/mapper para convertir documentos Firestore a modelos existentes.
- Guardar agregados en `reports` para evitar calculos en el cliente.

