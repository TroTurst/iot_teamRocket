# Plan de migración del rol admin a Firebase - Fase 1 (solo lectura)

Este documento define la primera fase de la migración del rol **administrador de inmobiliarias** hacia Firestore, enfocada **solo en lectura**.

La idea es que la interfaz del admin deje de depender de mocks locales y empiece a consumir datos reales de Firebase, pero **sin tocar todavía los flujos de creación, edición o borrado**.

---

## Objetivo de esta fase

Dejar funcionando el rol admin con datos reales de Firestore en modo lectura para:

- listar proyectos
- listar asesores
- mostrar perfil del admin
- mostrar notificaciones
- mostrar reportes base
- abrir detalles de proyectos y asesores

En esta fase **no se escriben documentos nuevos** ni se migran flujos de aprobación o creación.

---

## Fuente de verdad del modelo

La base para esta migración será:

- `docs/modelo_bd_nosql_unificado (1).json`

Ese JSON define el esquema unificado y multiempresa con estos conjuntos principales:

- `usuarios`
- `inmobiliarias`
- `proyectos`
- `notificaciones`
- `reportes`
- `logs`
- `solicitudesAsesor`
- `citas`
- `separaciones`
- `chats`

Para esta fase de solo lectura, el foco real queda en:

- `usuarios`
- `inmobiliarias`
- `proyectos`
- `notificaciones`
- `reportes`

---

## Pantallas admin que deben pasar a lectura real primero

### 1. `AdminHomeActivity`
Debe leer:
- datos básicos del admin actual desde `usuarios/{uid}`
- notificaciones pendientes desde `notificaciones`
- resumen del dashboard desde `reportes`

Debe dejar de depender de:
- badge fijo
- notificaciones mock
- valores hardcodeados

---

### 2. `AdminProyectosActivity`
Debe leer:
- lista de `proyectos` filtrada por `inmobiliariaId`

Debe mostrar:
- nombre
- descripción
- estado
- ubicación
- imagen principal
- tipologías resumen

Debe dejar de usar:
- `AdminProyectoRepositoryMock`
- listas locales en memoria

---

### 3. `AdminAsesoresActivity`
Debe leer:
- usuarios con `rol = asesor`
- usuarios con el mismo `inmobiliariaId` del admin

Debe mostrar:
- nombre
- email
- teléfono
- estado
- zona o especialidad si existe en el documento

Debe dejar de usar:
- `AdminAsesorRepositoryMock`
- datos hardcodeados

---

### 4. `AdminPerfilActivity`
Debe leer:
- `usuarios/{uid}` del admin actual
- `inmobiliarias/{inmobiliariaId}` para mostrar el nombre comercial de la empresa

Debe mostrar:
- nombre
- correo
- rol
- estado
- inmobiliaria asociada

---

### 5. `AdminReportesActivity`
Debe leer:
- `reportes/{reporteId}` según inmobiliaria y período

Debe mostrar:
- totales
- porcentaje de metas
- métricas resumidas por proyecto y asesor

Debe dejar de calcularse con:
- asesores mock
- porcentajes fijos
- períodos demo

---

### 6. `AdminProyectoDetalleActivity`
Debe leer:
- `proyectos/{proyectoId}`

Debe mostrar:
- ficha general
- galería
- tipologías
- áreas comunes
- coordenadas / ubicación
- asesores asignados

---

### 7. `AdminAsesorDetalleCarlosActivity`
Debe leer:
- `usuarios/{asesorId}`
- `proyectos` asignados al asesor

Debe mostrar:
- perfil del asesor
- metas actuales si ya existen en Firestore
- proyectos vinculados

En esta primera fase, la parte de citas puede quedar temporalmente con datos vacíos o un placeholder, pero la lectura de perfil debe ser real.

---

## Estructura de datos que debe usarse en lectura

## `usuarios/{uid}`
Campos relevantes para admin:
- `uid`
- `nombres`
- `apellidos`
- `correo`
- `telefono`
- `domicilio`
- `fotoUrl`
- `rol`
- `activo`
- `inmobiliariaId`
- `fechaCreacion`

### Uso en admin
- identificar al usuario conectado
- saber a qué inmobiliaria pertenece
- filtrar datos por empresa

---

## `inmobiliarias/{inmobiliariaId}`
Campos relevantes:
- `nombre`
- `descripcion`
- `correo`
- `telefono`
- `ubicacion`
- `fotosUrls`
- `adminId`
- `activo`
- `fechaCreacion`

### Uso en admin
- mostrar el nombre de la empresa
- mostrar datos institucionales en perfil y home
- asociar todo el contenido del admin a una inmobiliaria

---

## `proyectos/{proyectoId}`
Campos relevantes:
- `inmobiliariaId`
- `nombre`
- `descripcion`
- `estado`
- `ubicacion`
- `tipologias`
- `areasComunes`
- `imagenesUrls`
- `asesoresIds`
- `valoracionPromedio`
- `totalValoraciones`
- `fechaCreacion`

### Uso en admin
- listado de proyectos
- detalle de proyecto
- relación con asesores
- datos para reportes futuros

---

## `notificaciones/{notificacionId}`
Campos relevantes:
- `destinatarioId`
- `titulo`
- `descripcion`
- `tipo`
- `leida`
- `timestamp`

### Uso en admin
- badge superior
- listado de notificaciones
- estados leídas/no leídas

---

## `reportes/{reporteId}`
Formato recomendado del documento:
- `{inmobiliariaId}_{granularidad}_{periodo}`

Ejemplo:
- `inmob123_mensual_2026-05`

Campos relevantes:
- `inmobiliariaId`
- `granularidad`
- `periodo`
- `totales`
- `porProyecto`
- `porAsesor`
- `actualizadoEn`

### Uso en admin
- dashboard
- métricas por período
- listas resumidas

---

## Capa técnica que conviene crear para lectura

### 1. `AdminRemoteDataSource`
Debe dejar de estar vacío y convertirse en la capa que consulta Firestore.

Debe exponer operaciones como:
- obtener usuario por UID o email
- obtener inmobiliaria por ID
- obtener proyectos por `inmobiliariaId`
- obtener asesores por `inmobiliariaId`
- obtener notificaciones no leídas
- obtener reporte por período

---

### 2. `AdminRepositoryFirebase`
Debe implementar `AdminRepository` y sustituir al repositorio local en esta fase de lectura.

Este repositorio debe encargarse de:
- coordinar llamadas a Firestore
- devolver modelos de dominio existentes
- aplicar filtros básicos

---

### 3. DTOs Firestore
No conviene mapear directamente documentos Firestore a las clases de UI.

Crear DTOs para:
- `UsuarioDto`
- `InmobiliariaDto`
- `ProyectoDto`
- `NotificacionDto`
- `ReporteDto`

---

### 4. Mappers
Crear mappers entre DTOs y modelos de dominio:
- `UsuarioDto -> AdminUser` o modelo equivalente
- `ProyectoDto -> Proyecto`
- `AsesorDto -> Asesor`
- `ReporteDto -> ReporteItem` o estructura usada en UI

---

## Orden recomendado de implementación

### Paso 1: dejar listo el acceso al usuario admin
- leer `usuarios/{uid}`
- obtener `inmobiliariaId`
- guardar ese ID en memoria o sesión local

### Paso 2: migrar `AdminHomeActivity`
- badge de notificaciones real
- resumen base real

### Paso 3: migrar `AdminProyectosActivity`
- listado real por empresa

### Paso 4: migrar `AdminAsesoresActivity`
- listado real de asesores por empresa

### Paso 5: migrar `AdminPerfilActivity`
- perfil real del admin y su inmobiliaria

### Paso 6: migrar `AdminReportesActivity`
- usar agregados reales

### Paso 7: migrar vistas detalle
- proyecto
- asesor

---

## Qué queda fuera de esta fase

Todavía **no** se hace en esta etapa:

- crear proyectos
- editar proyectos
- crear asesores
- aprobar solicitudes
- registrar separaciones
- registrar citas
- escribir notificaciones
- actualizar reportes en Firestore
- mover Room a Firestore para solicitudes

---

## Qué se puede mantener temporalmente

Para no romper la app mientras se migran las lecturas:

- `AdminSessionDefaults` puede seguir como fallback temporal
- `AdminRepositoryLocal` puede quedar como respaldo mientras se conecta Firestore
- `AdminProyectoRepositoryMock` y `AdminAsesorRepositoryMock` deben ir quedando obsoletos, pero pueden convivir muy poco tiempo si necesitas transición

---

## Validación esperada al terminar esta fase

La fase 1 estará lista cuando:

- el admin vea proyectos reales
- el admin vea asesores reales
- el admin vea su perfil desde Firestore
- el badge de notificaciones ya no sea fijo
- los reportes al menos lean un documento real
- la UI no dependa de listas mock para las pantallas principales

---

## Siguiente fase

Cuando esta fase de lectura esté estable, la siguiente será:

- escritura de proyectos
- escritura de solicitudes de asesor
- edición de proyectos
- reportes actualizables
- separación de lógica por roles

---

## Resumen corto

**Primero solo lectura** significa:
- conectar Firestore
- leer `usuarios`, `inmobiliarias`, `proyectos`, `notificaciones` y `reportes`
- mantener los writes desactivados
- reemplazar mocks por repositorios reales
- dejar la base lista para la fase de escritura después

