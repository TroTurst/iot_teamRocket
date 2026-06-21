# 📊 Estructura del Rol Administrador de Inmobiliaria

## 👤 Usuario
- **Email**: paul2@gmail.com
- **Contraseña**: 123456
- **Rol**: `admin1` (ROL_ADMIN1)

---

## 🏗️ Arquitectura General

### Flujo de Autenticación
```
LoginActivity 
    ↓
UserCheck.getRol(email, password)
    ↓
ROL_ADMIN1 → AdminHomeActivity (Punto de entrada)
```

---

## 📱 Pantallas del Administrador

### 1. **AdminHomeActivity** - Inicio/Dashboard
- **Archivo**: `AdminHomeActivity.java`
- **Layout**: `activity_admin_home.xml`
- **Descripción**: Pantalla principal con resumen rápido
- **Componentes**:
  - 📍 Header con logo INMIA y campana de notificaciones
  - 🏢 Sección "Proyecto Destacado" (con foto preview)
  - 📊 Resumen rápido:
    - 4 Proyectos
    - 6 Asesores
    - 3 Pendientes
  - 🔔 Badge de notificaciones (hardcodeado: 5)

---

### 2. **AdminProyectosActivity** - Gestión de Proyectos
- **Archivo**: `AdminProyectosActivity.java`
- **Layout**: `activity_admin_proyectos.xml`
- **Descripción**: Listado y gestión de proyectos inmobiliarios
- **Funcionalidades**:
  - 🔍 Búsqueda de proyectos
  - 🎯 Filtros (próximamente)
  - ➕ Botón "Nuevo Proyecto"
  - 📄 Listado con detalles de proyectos

---

### 3. **AdminProyectoNuevoActivity** - Crear/Editar Proyecto
- **Archivo**: `AdminProyectoNuevoActivity.java`
- **Layout**: `activity_admin_proyecto_nuevo.xml`
- **Descripción**: Formulario completo para crear o editar proyectos
- **Campos de Entrada**:
  - 🏷️ Título
  - 📍 Ubicación
  - 📝 Descripción
  - 💰 Precio
  - 📐 Área
  - 🛏️ Dormitorios
  - 🚿 Baños
  - 🚗 Estacionamiento
  - ✅ Estado
  - 📸 Imágenes (Hero + Galería)

- **Tipologías Predefinidas**:
  1. **45 m² · 1d** → S/ 420,000
  2. **65 m² · 2d** → S/ 648,000
  3. **90 m² · 3d** → S/ 915,000

- **Funcionalidades**:
  - ✨ Previsualización en tiempo real
  - 🎯 Selector de tipologías
  - 📸 Galería de imágenes
  - ➕ Agregar tipologías personalizadas (próximamente)

---

### 4. **AdminProyectoDetalleActivity** - Ver Detalles del Proyecto
- **Archivo**: `AdminProyectoDetalleActivity.java`
- **Layout**: `activity_admin_proyecto_detalle.xml`
- **Descripción**: Vista detallada de un proyecto
- **Funcionalidades**:
  - 📸 Galería de fotos
  - 📋 Información completa
  - ✏️ Botón Editar
  - 🗑️ Botón Eliminar

---

### 5. **AdminAsesoresActivity** - Gestión de Asesores
- **Archivo**: `AdminAsesoresActivity.java`
- **Layout**: `activity_admin_asesores.xml`
- **Descripción**: Listado y gestión de asesores inmobiliarios
- **Funcionalidades**:
  - 🔍 Búsqueda de asesores
  - 🎯 Filtros (próximamente)
  - ➕ Botón "Nuevo Asesor"
  - 👤 Detalles de asesores (ej: Carlos)

---

### 6. **AdminAsesorNuevoActivity** - Crear Asesor
- **Archivo**: `AdminAsesorNuevoActivity.java`
- **Layout**: `activity_admin_asesor_nuevo.xml`
- **Descripción**: Formulario para crear nuevo asesor
- **Campos**: Nombre, email, teléfono, foto, etc.

---

### 7. **AdminAsesorDetalleCarlosActivity** - Ver Asesor
- **Archivo**: `AdminAsesorDetalleCarlosActivity.java`
- **Layout**: `activity_admin_asesor_detalle_carlos.xml`
- **Descripción**: Perfil detallado del asesor con estadísticas
- **Información**:
  - 👤 Datos personales
  - 📊 Estadísticas de ventas
  - 📱 Contacto
  - ✏️ Opción de editar

---

### 8. **AdminReportesActivity** - Reportes y Análisis
- **Archivo**: `AdminReportesActivity.java`
- **Layout**: `activity_admin_reportes.xml`
- **Descripción**: Dashboard de reportes y métricas
- **Datos Mostrados**:
  - 📅 Período: Abril 2026
  - 🏆 Mejor Asesor:
    - Nombre: Carlos Mendoza
    - Ventas: 7
    - Citas: 28
    - Conversión: 25%
    - Ingresos: S/ 654,000
  - 📊 Métricas Globales:
    - Casas Vendidas: 13
    - Asesores Activos: 9
    - Pendientes Cierre: 4
  - 📈 Gráficos de Progreso:
    - Meta de Ventas: 76%
    - Tasa de Cierre: 64%
    - Leads: 83%

---

### 9. **AdminPerfilActivity** - Mi Perfil
- **Archivo**: `AdminPerfilActivity.java`
- **Layout**: `activity_admin_perfil.xml`
- **Descripción**: Configuración y perfil del administrador
- **Secciones**:
  - 👤 Información Personal:
    - Nombre: Administrador
    - Email: admin@inmia.com
    - Rol: Administrador
  - ⚙️ Configuración:
    - 🔒 Cambiar contraseña
    - 🔔 Notificaciones
  - 🚪 Cerrar sesión (con confirmación)

---

## 🔄 Navegación - Bottom Navigation Menu

```
┌─────────────────────────────────────────────────────┐
│ menu_admin.xml (Bottom Navigation Bar)              │
├─────────────────────────────────────────────────────┤
│ 🏠 Inicio         → AdminHomeActivity               │
│ 📦 Proyectos      → AdminProyectosActivity          │
│ 👥 Asesores       → AdminAsesoresActivity           │
│ 📊 Reportes       → AdminReportesActivity           │
│ 👤 Perfil         → AdminPerfilActivity             │
└─────────────────────────────────────────────────────┘
```

---

## 📁 Estructura de Archivos

```
com/example/inmia/admin/
├── AdminHomeActivity.java
├── AdminProyectosActivity.java
├── AdminProyectoNuevoActivity.java
├── AdminProyectoDetalleActivity.java
├── AdminProyectoEditarActivity.java
├── AdminProyectoGaleriaActivity.java
├── AdminProyectoGaleriaAdapter.java
├── AdminAsesoresActivity.java
├── AdminAsesorNuevoActivity.java
├── AdminAsesorDetalleCarlosActivity.java
├── AdminReportesActivity.java
├── AdminPerfilActivity.java
└── RegistroInmobiliariaActivity.java

Layouts (res/layout/):
├── activity_admin_home.xml
├── activity_admin_proyectos.xml
├── activity_admin_proyecto_nuevo.xml
├── activity_admin_proyecto_detalle.xml
├── activity_admin_proyecto_editar.xml
├── activity_admin_proyecto_galeria.xml
├── activity_admin_asesores.xml
├── activity_admin_asesor_nuevo.xml
├── activity_admin_asesor_detalle_carlos.xml
├── activity_admin_reportes.xml
├── activity_admin_perfil.xml
├── item_admin_proyecto_galeria.xml
└── activity_registro_inmobiliaria_inicio.xml

Menu (res/menu/):
└── menu_admin.xml (5 opciones de navegación)
```

---

## 🎨 Diseño y Colores

- **Color Principal**: `@color/inmia_teal_dark`
- **Color Secundario**: `@color/inmia_teal_light`
- **Fondo**: `#F5FAFA`
- **Componentes**: Material Design 3
- **Bottom Navigation**: Barra flotante con etiquetas visibles

---

## 📊 Casos de Uso Principales

1. **Dashboard**: Ver resumen de proyectos, asesores y pendientes
2. **Proyectos**: 
   - ✅ Ver listado
   - ✅ Crear nuevo
   - ✅ Editar existente
   - ✅ Ver detalles y galería
   - ✅ Seleccionar tipologías predefinidas
3. **Asesores**:
   - ✅ Ver listado
   - ✅ Crear nuevo
   - ✅ Ver perfil con estadísticas
4. **Reportes**: Analizar ventas, comisiones y desempeño
5. **Perfil**: Cambiar contraseña, configurar notificaciones, cerrar sesión

---

## ⚙️ Estado Actual

- ✅ **Estructura base** completa
- ✅ **Navegación** implementada
- ✅ **Layouts** diseñados con Material Design
- ⏳ **Firebase** (datos hardcodeados, próximamente integración)
- ⏳ **Filtros avanzados** (funcionalidad próximamente)
- ⏳ **Agregar tipologías personalizadas** (funcionalidad próximamente)
- ⏳ **Cambiar contraseña** (funcionalidad próximamente)
- ⏳ **Configuración de notificaciones** (funcionalidad próximamente)

---

## 🔐 Seguridad & Datos

- **Credenciales**: Actualmente hardcodeadas en `UserCheck.java`
- **Datos de Usuarios**: Hardcodeados temporalmente
- **Datos de Reportes**: Datos mock en `AdminReportesActivity`
- **Tipologías**: Datos mock en `AdminProyectoNuevoActivity`

**Nota**: Se indica en todo el código que la integración con Firebase está pendiente.

---

## 📝 Notas Importantes

- La navegación utiliza `Intent.FLAG_ACTIVITY_CLEAR_TOP` para evitar duplicar activities
- Cada pantalla tiene un badge de notificaciones (hardcodeado en 5)
- Los datos de reportes están basados en datos mock para demostración
- Las imágenes de proyectos usan recursos locales (drawables de onboarding)
- El rol específico es `ROL_ADMIN1` (no confundir con `ROL_ADMIN` que va a `RegistroInmobiliariaActivity`)


