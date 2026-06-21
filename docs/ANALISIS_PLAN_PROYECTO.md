# 📋 ANÁLISIS - Plan de Proyecto INMIA

## 📌 Información General

**Título del Proyecto**: Sistema para la gestión de reservas y citas de proyectos inmobiliarios vía aplicación móvil

**Coordinador**: Oscar Díaz  
**Curso**: 1TEL05 - Servicios y Aplicaciones para IoT  
**Especialidad**: Ingeniería de las Telecomunicaciones  
**Universidad**: Pontificia Universidad Católica del Perú  
**Fecha**: 16.03.2026

---

## 🎯 Objetivo del Proyecto

Desarrollar un sistema que permita la **gestión de reservas y citas de proyectos inmobiliarios** mediante una aplicación móvil.

### Alcance
- Planificación, diseño, ejecución e implementación del sistema
- Verificación en cada cierre de fase por jefe de práctica
- Validación con el profesor cuando sea necesaria

---

## 📅 Cronograma de Fases

| Fase | Entregable | Hito | Sesión | Fecha |
|------|-----------|------|--------|-------|
| 1 | Formación de grupos | - | Lab 1 | 23/mar/26 |
| 2 | Lista de requerimientos + 100% Mockups | - | Lab 2 | 06/abr/26 |
| 3 | Implementación de mockups en Android (navigation, menús, UI) | - | Lab 3 | 20/abr/26 |
| 4 | RecyclerView: Listado de elementos (data estática) | - | Lab 4 | 04/may/26 |
| - | **EXÁMENES PARCIALES** | - | - | - |
| 5 | Implementación de Storage Local y notificaciones | - | Lab 5 | 25/may/26 |
| 6 | Firebase Authentication y Firebase Database | - | Lab 6 | 08/jun/26 |
| 7 | Presentación prefinal (90%) | - | Lab 7 | 22/jun/26 |
| - | **Presentación Final (100%)** | - | - | - |

**Duración Total**: ~4 meses (23/marzo/2026 → 22/junio/2026)

---

## 👥 Actores / Roles del Sistema

### 1️⃣ **Superadmin**
- Administrador del sistema

**Funcionalidades**:
- Registra a los administradores de inmobiliarias
- Gestión de usuarios (administradores, asesores, clientes)
- Puede activar y desactivar usuarios
- Reporte global de reservas en todas las empresas
- Ver logs de eventos del sistema
- Interfaz exclusiva de gestión de usuarios
- Habilita asesores de ventas

### 2️⃣ **Administrador de la Inmobiliaria** ⭐ (Paul2@gmail.com)
- Representante de la empresa desarrolladora en el sistema

**Funcionalidades**:
- Registra ubicación de oficinas, correo y teléfono
- Registra fotos promocionales (mínimo 2)
- Registra proyectos inmobiliarios con:
  - Ubicación (visible en mapa)
  - Tipología de departamentos (metraje, número de cuartos)
  - Costo de separación y precio total
  - Áreas comunes (coworking, piscina, parrillas)
  - Estado del proyecto (En planos, En preventa, En venta)
  - Fechas estimadas de entrega
- Asigna asesores a proyectos específicos
- Recibe notificaciones de separaciones registradas
- Aprueba montos de separaciones
- Recibe notificación de pagos (checkout)
- Genera reportes de ventas/separaciones por proyecto y asesor

### 3️⃣ **Asesor de Ventas**
- Encargado de guiar visitas y cerrar separaciones

**Funcionalidades**:
- Se auto registra y es aprobado por el administrador
- Datos personales: nombre, apellidos, tipo de documento, número de documento, fecha de nacimiento, correo, teléfono, domicilio, foto
- Visualiza citas agendadas por clientes
- Registra separaciones (proyecto, cliente, valor propuesto)
- Ve historial de citas

### 4️⃣ **Cliente**
- Usuario que busca propiedades y hace reservas

**Funcionalidades**:
- Se auto registra y es habilitado automáticamente
- Datos: nombre, apellidos, documento, fecha de nacimiento, correo, teléfono, domicilio, foto
- Visualiza inmobiliarias, sus proyectos en mapa, y valoraciones de otros usuarios
- Registra citas (una a la vez, sin superposición de horarios)
- Recibe notificación de separación aprobada
- Debe pagar en máximo 10 minutos
- Debe registrar tarjeta de crédito/débito para separar inmuebles
- Ve historial de citas y separaciones

---

## 🌍 Descripción del Proyecto

### Funcionalidades Principales

✅ **Registro de Empresas Inmobiliarias**
- Administrador registra su empresa

✅ **Gestión de Citas y Separaciones Online**
- Clientes agenden citas
- Asesores registren separaciones
- Sistema valide pagos

✅ **Información de Proyectos**
- Ubicación exacta en mapa
- Puntos de interés cercanos
- Tipologías disponibles
- Fotos y galería

✅ **Valoración y Observaciones**
- Al registrar separación, cliente coloca valoración del asesor

✅ **Control de Asesores y Administradores**
- Registro y habilitación por superadmin
- Asignación a proyectos

✅ **Reportes**
- Diario, mensual y anual
- Por empresa inmobiliaria
- Por proyecto y asesor

✅ **Chat por Inmobiliaria**
- Atención al cliente
- Seguimiento

✅ **QR para Proyectos**
- Escanear para acceder al proyecto en la app

---

## 📋 Entregables Finales

✅ **Sistema Desplegado en la Nube**

✅ **Repositorio Git con**:
- Código de cada elemento del sistema
- Esquema de la base de datos

✅ **Archivo de Arquitectura**

✅ **Costo Total de la Solución (OPEX)**

✅ **Manual de Instalación**

✅ **Manual de Usuario**

---

## 🛠️ Requerimientos No Funcionales

| Requerimiento | Especificación |
|--------------|----------------|
| Lenguaje | Java (Android Nativo) |
| Compatibilidad Mínima | Android 14.0 |
| Base de Datos | NoSQL |
| Control de Versiones | Git (GitHub, GitLab, Bitbucket, etc) |

---

## 👨‍💼 Equipo del Laboratorio 0892

**Estructura**: 3 grupos de 4 alumnos cada uno

### Supervisores (Jefes de Práctica)
- 20160679 - GUEVARA HERMOZA, ALONSO SEBASTIAN
- 20161056 - MINAYA ORIHUELA, CARLOS GUILLERMO
- 20191566 - ROSALES ANTUNEZ, ALONSO DABOR

---

## 🏗️ Arquitectura Esperada

### Stack Tecnológico

**Frontend**:
- Android Native (Java)
- Material Design 3 (UI/UX)

**Backend**:
- Firebase Authentication
- Firebase Realtime Database
- Firebase Cloud Storage

**Mapas**:
- Google Maps SDK

**Pagos**:
- Sistema de integración de tarjetas

**Notificaciones**:
- Firebase Cloud Messaging

---

## 📊 Estado Actual del Proyecto INMIA

### ✅ Completado
- [x] **Fase 1**: Formación de grupos (23/mar)
- [x] **Fase 2**: Mockups 100% y requerimientos (06/abr)
- [x] **Fase 3**: Implementación de mockups - Navigation, Menús, UI (20/abr)

### 🔄 En Progreso
- [ ] **Fase 4**: RecyclerView y data estática (04/may)
- [ ] **Exámenes Parciales**
- [ ] **Fase 5**: Storage Local y notificaciones (25/may)
- [ ] **Fase 6**: Firebase Auth + Database (08/jun)

### ⏳ Pendiente
- [ ] **Fase 7**: Presentación prefinal 90% (22/jun)
- [ ] **Presentación Final**: 100%
- [ ] Despliegue en la nube
- [ ] Documentación final

---

## 🎯 Relación con la Estructura Actual

### El rol de Administrador de Inmobiliaria (paul2@gmail.com) DEBE incluir:

✅ **Ya implementado**:
- Dashboard con resumen de proyectos, asesores, pendientes
- Gestión de proyectos (CRUD)
- Gestión de asesores (CRUD)
- Reportes con métricas
- Perfil de usuario

⏳ **Próximo**:
- [ ] Integración con Firebase Authentication
- [ ] Integración con Firebase Realtime Database
- [ ] Sincronización de proyectos desde BD
- [ ] Sincronización de asesores desde BD
- [ ] Sincronización de separaciones desde BD
- [ ] Notificaciones en tiempo real
- [ ] Reportes dinámicos
- [ ] Pago de separaciones

---

## 📌 Notas Importantes

1. **Estado del Proyecto**: En planos, En preventa, En venta
2. **Tipologías**: Metraje, número de cuartos, áreas comunes
3. **Separación**: Reserva de inmueble con depósito
4. **Validaciones**:
   - No superposición de citas
   - Pago en máximo 10 minutos
   - Tarjeta de crédito/débito requerida

---

## 🚀 Próximos Pasos Críticos

1. **Implementar Firebase Authentication** (Fase 6)
   - Migrar credenciales hardcodeadas a Firebase
   - Configurar proveedores (email/contraseña, Google, etc)

2. **Implementar Firebase Realtime Database** (Fase 6)
   - Crear estructura de datos para proyectos
   - Crear estructura de datos para asesores
   - Crear estructura de datos para separaciones
   - Crear estructura de datos para citas

3. **Añadir Funcionalidades de Pago** (Fase 5-6)
   - Integrar pasarela de pagos
   - Implementar validación de tarjetas

4. **Implementar Notificaciones** (Fase 5)
   - Firebase Cloud Messaging
   - Notificaciones en tiempo real para:
     - Nuevas separaciones
     - Pagos recibidos
     - Citas agendadas
     - Aprobaciones de separaciones

5. **Mapas Interactivos** (Fase 6)
   - Google Maps SDK
   - Mostrar ubicación de proyectos
   - Puntos de interés

6. **QR Scanner** (Fase 6-7)
   - Implementar escaneo de QR
   - Redireccionar a proyecto en la app


