# 📑 ÍNDICE DE DOCUMENTACIÓN - Proyecto INMIA

## 🗂️ Archivos en la Carpeta `docs/`

```
docs/
├── README_DOCUMENTACION.md              ← EMPIEZA AQUÍ
├── ESTRUCTURA_ADMIN_INMOBILIARIA.md     ← Rol Admin (Paul2)
├── ANALISIS_PLAN_PROYECTO.md            ← Requerimientos del Proyecto
├── PDF_CONTENIDO.txt                    ← Extracción del PDF
└── _APPsIOT-2026-1- Plan de proyecto_.docx (1).pdf  ← PDF Original
```

---

## 🎯 Guía Rápida de Lectura

### 👤 "¿Quiero entender qué hace el Administrador de Inmobiliaria?"
→ Lee: **ESTRUCTURA_ADMIN_INMOBILIARIA.md**
- Pantallas del admin
- Casos de uso
- Navegación
- Arquitectura actual

### 📋 "¿Quiero saber qué debe hacer según el proyecto?"
→ Lee: **ANALISIS_PLAN_PROYECTO.md**
- Requerimientos funcionales
- 4 Roles del sistema
- Cronograma
- Tecnologías
- Lo que falta implementar

### 🔍 "¿Quiero buscar algo específico del PDF?"
→ Usa: **PDF_CONTENIDO.txt**
- Busca con CTRL+F
- Referencia textual completa

### 📚 "¿Necesito una visión completa?"
→ Lee: **README_DOCUMENTACION.md**
- Comparativa: Requerimientos vs Implementación
- Plan de acción
- Matriz de funcionalidades
- Cronograma visual

---

## 📌 Información Clave Resumida

### 👥 Usuario del Rol Admin
- **Email**: paul2@gmail.com
- **Contraseña**: 123456
- **Rol**: admin1 (ROL_ADMIN1)

### 📱 Pantallas del Admin (10 total)
1. AdminHomeActivity - Dashboard
2. AdminProyectosActivity - Listado proyectos
3. AdminProyectoNuevoActivity - Crear proyecto
4. AdminProyectoDetalleActivity - Ver proyecto
5. AdminProyectoEditarActivity - Editar proyecto
6. AdminProyectoGaleriaActivity - Galería de fotos
7. AdminAsesoresActivity - Listado asesores
8. AdminAsesorNuevoActivity - Crear asesor
9. AdminAsesorDetalleCarlosActivity - Ver asesor
10. AdminPerfilActivity - Mi Perfil
+ AdminReportesActivity - Reportes

### 🛠️ Tecnologías
- **Lenguaje**: Java (Android Nativo)
- **Versión Android**: 14.0 mínimo
- **BD**: Firebase (Realtime Database)
- **Autenticación**: Firebase Authentication
- **Mapas**: Google Maps SDK
- **UI**: Material Design 3

### 📅 Cronograma del Proyecto
- **Fase 1** (23/mar): Formación de grupos ✅
- **Fase 2** (06/abr): Mockups ✅
- **Fase 3** (20/abr): Implementación mockups ✅
- **Fase 4** (04/may): RecyclerView 🔄
- **Fase 5** (25/may): Storage Local + Notificaciones
- **Fase 6** (08/jun): **Firebase Auth + DB** ⭐
- **Fase 7** (22/jun): Presentación prefinal

---

## ✅ / ⏳ Estado Actual

### ✅ Completado en el Admin
- [x] Dashboard con resumen
- [x] CRUD de proyectos
- [x] CRUD de asesores
- [x] Reportes (con datos mock)
- [x] Perfil de usuario
- [x] Logout

### ⏳ Pendiente en el Admin
- [ ] Firebase Authentication
- [ ] Firebase Realtime Database
- [ ] Google Maps Integration
- [ ] Sistema de Pagos
- [ ] Notificaciones en tiempo real
- [ ] Chat por inmobiliaria
- [ ] QR Scanner
- [ ] Reportes dinámicos

---

## 🔑 Funcionalidades Principales del Admin

### ✅ Ya Implementadas
- Ver dashboard con resumen (4 proyectos, 6 asesores, 3 pendientes)
- Crear, editar, ver, eliminar proyectos
- Seleccionar tipologías predefinidas (45m², 65m², 90m²)
- Previsualización en tiempo real
- Ver galería de imágenes
- Crear, ver, editar asesores
- Ver estadísticas de asesores (ventas, citas, conversión, ingresos)
- Ver reportes de ventas/separaciones (datos mock)
- Ver y editar perfil
- Cerrar sesión

### ⏳ Debe Implementar
- Sincronizar proyectos desde Firebase
- Sincronizar asesores desde Firebase
- Sincronizar separaciones desde Firebase
- Recibir notificaciones de nuevas separaciones
- Aprobar montos de separaciones
- Recibir notificaciones de pagos
- Procesar cobros a tarjeta
- Mostrar ubicación en mapa
- Cambiar contraseña
- Chat con clientes

---

## 🎓 Puntos Importantes

1. **Datos actuales**: Todo está hardcodeado en Java
2. **Próximo paso**: Migrar a Firebase (Fase 6 - 08/jun)
3. **Prioridad**: Firebase Auth + Database
4. **Usuario de prueba**: paul2@gmail.com / 123456
5. **Rol específico**: admin1 (no confundir con ROL_ADMIN)

---

## 📞 Para Más Información

- **Coordinador**: Oscar Díaz
- **Curso**: 1TEL05 - Servicios y Aplicaciones para IoT
- **Laboratorio**: 0892
- **Universidad**: Pontificia Universidad Católica del Perú

---

## 🚀 Recomendación

**Si vienes por primera vez:**
1. Lee **README_DOCUMENTACION.md** (visión general)
2. Lee **ESTRUCTURA_ADMIN_INMOBILIARIA.md** (cómo está implementado)
3. Lee **ANALISIS_PLAN_PROYECTO.md** (qué falta)
4. Usa **PDF_CONTENIDO.txt** para búsquedas específicas

**Total de lectura**: ~30-45 minutos

---

**Documentación generada**: 26 de Abril de 2026  
**Estado del Proyecto**: Fase 3 completada, iniciando Fase 4  
**Próximo Hito**: 04 de Mayo - RecyclerView


