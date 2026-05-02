# 📱 Guía de Implementación de RecyclerView - Fase 4

**Fase**: 4 (04/May/2026)  
**Tarea**: Uso RecyclerView: Listado de elementos (data estática)  
**Tecnología**: Android RecyclerView + Adapters

---

## 🎯 Objetivos de RecyclerView en INMIA

RecyclerView es un componente que permite mostrar listas eficientes de elementos. Reemplazará los listados actuales y preparará la app para datos dinámicos.

---

## 📍 Lugares donde Implementar RecyclerView

### 1️⃣ **AdminProyectosActivity** - Listado de Proyectos ⭐ PRIORITARIO

**Estado Actual**: 
- Muestra detalles de UN proyecto destacado
- No hay listado de múltiples proyectos

**Con RecyclerView**:
- Mostrar lista de 4+ proyectos (datos estáticos)
- Cada item: foto, nombre, precio, estado, ubicación
- Click en item → AdminProyectoDetalleActivity

**Estructura del Adapter**:
```
AdminProyectoAdapter
├── ViewHolder
│   ├── ImageView (foto proyecto)
│   ├── TextView (nombre)
│   ├── TextView (ubicación)
│   ├── TextView (precio)
│   └── TextView (estado)
├── onCreateViewHolder()
├── onBindViewHolder()
├── getItemCount()
└── Lista de Proyectos (data estática)
```

**Implica**:
- ✅ Crear clase `AdminProyectoAdapter extends RecyclerView.Adapter`
- ✅ Crear clase `ProyectoViewHolder extends RecyclerView.ViewHolder`
- ✅ Crear modelo `Proyecto` (Nombre, ubicación, precio, estado, imagen)
- ✅ Definir layout `item_proyecto.xml`
- ✅ Agregar RecyclerView al layout `activity_admin_proyectos.xml`
- ✅ Inicializar RecyclerView en `AdminProyectosActivity`
- ✅ Crear lista de datos mock

**Datos Mock Necesarios** (4+ proyectos):
```java
List<Proyecto> proyectos = Arrays.asList(
    new Proyecto("Proyecto 1", "Santa Catalina", "$420,000", "En planos", R.drawable.onboarding1),
    new Proyecto("Proyecto 2", "Jesús María", "$648,000", "En preventa", R.drawable.onboarding2),
    new Proyecto("Proyecto 3", "Pueblo Libre", "$915,000", "En venta", R.drawable.onboarding3),
    new Proyecto("Proyecto 4", "Miraflores", "$780,000", "En planos", R.drawable.images_2)
);
```

---

### 2️⃣ **AdminAsesoresActivity** - Listado de Asesores ⭐ PRIORITARIO

**Estado Actual**:
- Muestra detalles de UN asesor (Carlos)
- No hay listado de múltiples asesores

**Con RecyclerView**:
- Mostrar lista de 6+ asesores (datos estáticos)
- Cada item: avatar, nombre, email, teléfono, estado
- Click en item → AdminAsesorDetalleCarlosActivity

**Estructura del Adapter**:
```
AdminAsesorAdapter
├── ViewHolder
│   ├── ImageView (avatar)
│   ├── TextView (nombre)
│   ├── TextView (email)
│   ├── TextView (teléfono)
│   └── TextView (estado: activo/inactivo)
├── onCreateViewHolder()
├── onBindViewHolder()
├── getItemCount()
└── Lista de Asesores (data estática)
```

**Implica**:
- ✅ Crear clase `AdminAsesorAdapter extends RecyclerView.Adapter`
- ✅ Crear clase `AsesorViewHolder extends RecyclerView.ViewHolder`
- ✅ Crear modelo `Asesor` (Nombre, email, teléfono, avatar, estado)
- ✅ Definir layout `item_asesor.xml`
- ✅ Agregar RecyclerView al layout `activity_admin_asesores.xml`
- ✅ Inicializar RecyclerView en `AdminAsesoresActivity`
- ✅ Crear lista de datos mock

**Datos Mock Necesarios** (6+ asesores):
```java
List<Asesor> asesores = Arrays.asList(
    new Asesor("Carlos Mendoza", "carlos@inmia.com", "987654321", R.drawable.avatar1, "Activo"),
    new Asesor("Ana García", "ana@inmia.com", "987654322", R.drawable.avatar2, "Activo"),
    new Asesor("Juan López", "juan@inmia.com", "987654323", R.drawable.avatar3, "Inactivo"),
    new Asesor("María Rodríguez", "maria@inmia.com", "987654324", R.drawable.avatar4, "Activo"),
    new Asesor("Pedro Flores", "pedro@inmia.com", "987654325", R.drawable.avatar5, "Activo"),
    new Asesor("Laura Díaz", "laura@inmia.com", "987654326", R.drawable.avatar6, "Activo")
);
```

---

### 3️⃣ **AdminProyectoGaleriaActivity** - Galería de Imágenes

**Estado Actual**:
- Existe pero probablemente muestra una sola imagen

**Con RecyclerView**:
- Mostrar galería de 10+ imágenes del proyecto
- Cada item: thumbnail de imagen
- Click en item → Ver imagen en full screen

**Estructura del Adapter**:
```
AdminProyectoGaleriaAdapter
├── ViewHolder
│   └── ImageView (thumbnail)
├── onCreateViewHolder()
├── onBindViewHolder()
├── getItemCount()
└── Lista de imágenes (data estática)
```

**Implica**:
- ✅ Crear clase `AdminProyectoGaleriaAdapter extends RecyclerView.Adapter`
- ✅ Crear layout `item_foto_galeria.xml`
- ✅ Agregar RecyclerView al layout `activity_admin_proyecto_galeria.xml`
- ✅ Inicializar RecyclerView en `AdminProyectoGaleriaActivity`
- ✅ Crear lista de 10+ imágenes mock

**Configuración GridLayout**:
```java
GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3); // 3 columnas
recyclerView.setLayoutManager(gridLayoutManager);
```

---

### 4️⃣ **AdminAsesorDetalleCarlosActivity** - Historial de Citas

**Estado Actual**:
- Muestra datos del asesor
- Probablemente no tiene historial de citas

**Con RecyclerView**:
- Mostrar lista de 5+ citas del asesor
- Cada item: cliente, fecha, hora, proyecto, estado
- Scroll vertical

**Estructura del Adapter**:
```
CitasAdapter
├── ViewHolder
│   ├── TextView (cliente)
│   ├── TextView (fecha)
│   ├── TextView (hora)
│   ├── TextView (proyecto)
│   └── TextView (estado)
├── onCreateViewHolder()
├── onBindViewHolder()
├── getItemCount()
└── Lista de citas (data estática)
```

**Implica**:
- ✅ Crear clase `CitasAdapter extends RecyclerView.Adapter`
- ✅ Crear modelo `Cita` (Cliente, fecha, hora, proyecto, estado)
- ✅ Crear layout `item_cita.xml`
- ✅ Agregar RecyclerView al layout `activity_admin_asesor_detalle_carlos.xml`
- ✅ Inicializar RecyclerView en `AdminAsesorDetalleCarlosActivity`
- ✅ Crear lista de datos mock

---

### 5️⃣ **AdminReportesActivity** - Tabla de Reportes (Opcional)

**Estado Actual**:
- Muestra métricas con TextViews y ProgressBars
- No hay listado de detalles

**Con RecyclerView** (Opcional):
- Mostrar tabla/listado de asesores con sus ventas
- Cada item: asesor, ventas, citas, conversión, ingresos
- Scroll horizontal o vertical

**Estructura del Adapter**:
```
ReporteAsesorAdapter
├── ViewHolder
│   ├── TextView (nombre asesor)
│   ├── TextView (ventas)
│   ├── TextView (citas)
│   ├── TextView (conversión)
│   └── TextView (ingresos)
├── onCreateViewHolder()
├── onBindViewHolder()
├── getItemCount()
└── Lista de asesores reportados (data estática)
```

---

## 🔧 Pasos Generales para Implementar RecyclerView

### Paso 1: Crear Modelo de Datos
```java
public class Proyecto {
    private String nombre;
    private String ubicacion;
    private String precio;
    private String estado;
    private int imagen;
    
    public Proyecto(String nombre, String ubicacion, String precio, String estado, int imagen) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.precio = precio;
        this.estado = estado;
        this.imagen = imagen;
    }
    
    // Getters...
}
```

### Paso 2: Crear Adapter
```java
public class AdminProyectoAdapter extends RecyclerView.Adapter<AdminProyectoAdapter.ViewHolder> {
    
    private List<Proyecto> proyectos;
    private Context context;
    
    public AdminProyectoAdapter(Context context, List<Proyecto> proyectos) {
        this.context = context;
        this.proyectos = proyectos;
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_proyecto, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Proyecto proyecto = proyectos.get(position);
        holder.nombreProyecto.setText(proyecto.getNombre());
        holder.ubicacion.setText(proyecto.getUbicacion());
        holder.precio.setText(proyecto.getPrecio());
        holder.estado.setText(proyecto.getEstado());
        holder.imagen.setImageResource(proyecto.getImagen());
    }
    
    @Override
    public int getItemCount() {
        return proyectos.size();
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreProyecto, ubicacion, precio, estado;
        ImageView imagen;
        
        public ViewHolder(View itemView) {
            super(itemView);
            nombreProyecto = itemView.findViewById(R.id.tvNombreProyecto);
            ubicacion = itemView.findViewById(R.id.tvUbicacion);
            precio = itemView.findViewById(R.id.tvPrecio);
            estado = itemView.findViewById(R.id.tvEstado);
            imagen = itemView.findViewById(R.id.imgProyecto);
            
            // Click listener
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                // Navegar a detalle
                Intent intent = new Intent(context, AdminProyectoDetalleActivity.class);
                context.startActivity(intent);
            });
        }
    }
}
```

### Paso 3: Crear Layout del Item
```xml
<!-- item_proyecto.xml -->
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp"
    app:cardCornerRadius="12dp"
    app:cardElevation="4dp">
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="12dp">
        
        <ImageView
            android:id="@+id/imgProyecto"
            android:layout_width="match_parent"
            android:layout_height="150dp"
            android:scaleType="centerCrop"
            android:contentDescription="Foto proyecto"/>
        
        <TextView
            android:id="@+id/tvNombreProyecto"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Nombre Proyecto"
            android:textSize="16sp"
            android:textStyle="bold"
            android:layout_marginTop="8dp"/>
        
        <TextView
            android:id="@+id/tvUbicacion"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Ubicación"
            android:textSize="14sp"
            android:layout_marginTop="4dp"/>
        
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_marginTop="8dp">
            
            <TextView
                android:id="@+id/tvPrecio"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="Precio"
                android:textSize="14sp"
                android:textStyle="bold"/>
            
            <TextView
                android:id="@+id/tvEstado"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="En planos"
                android:textSize="12sp"
                android:paddingHorizontal="8dp"
                android:paddingVertical="4dp"
                android:background="@drawable/badge_outline"
                android:textColor="@color/inmia_teal_dark"/>
        </LinearLayout>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

### Paso 4: Agregar RecyclerView al Layout Activity
```xml
<!-- activity_admin_proyectos.xml (fragmento) -->
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/recyclerViewProyectos"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_marginHorizontal="16dp"
    android:layout_marginVertical="12dp"
    android:clipToPadding="false"
    android:paddingBottom="80dp"/>
```

### Paso 5: Inicializar RecyclerView en Activity
```java
// En AdminProyectosActivity.java
RecyclerView recyclerViewProyectos = findViewById(R.id.recyclerViewProyectos);

// Configurar LayoutManager
LinearLayoutManager layoutManager = new LinearLayoutManager(this);
recyclerViewProyectos.setLayoutManager(layoutManager);

// Crear lista de datos mock
List<Proyecto> proyectos = new ArrayList<>();
proyectos.add(new Proyecto("Proyecto 1", "Santa Catalina", "$420,000", "En planos", R.drawable.onboarding1));
proyectos.add(new Proyecto("Proyecto 2", "Jesús María", "$648,000", "En preventa", R.drawable.onboarding2));
// Más proyectos...

// Crear y asignar adapter
AdminProyectoAdapter adapter = new AdminProyectoAdapter(this, proyectos);
recyclerViewProyectos.setAdapter(adapter);
```

---

## 📋 Checklist de Implementación

### Para AdminProyectosActivity:
- [ ] Crear modelo `Proyecto`
- [ ] Crear adapter `AdminProyectoAdapter`
- [ ] Crear layout `item_proyecto.xml`
- [ ] Agregar RecyclerView a `activity_admin_proyectos.xml`
- [ ] Inicializar RecyclerView en activity
- [ ] Crear 4+ proyectos mock
- [ ] Agregar OnClickListener para navegar a detalles

### Para AdminAsesoresActivity:
- [ ] Crear modelo `Asesor`
- [ ] Crear adapter `AdminAsesorAdapter`
- [ ] Crear layout `item_asesor.xml`
- [ ] Agregar RecyclerView a `activity_admin_asesores.xml`
- [ ] Inicializar RecyclerView en activity
- [ ] Crear 6+ asesores mock
- [ ] Agregar OnClickListener para navegar a detalles

### Para AdminProyectoGaleriaActivity:
- [ ] Crear adapter `AdminProyectoGaleriaAdapter`
- [ ] Crear layout `item_foto_galeria.xml`
- [ ] Usar GridLayoutManager (3 columnas)
- [ ] Crear 10+ imágenes mock
- [ ] Agregar OnClickListener para ver en full screen

### Para AdminAsesorDetalleCarlosActivity:
- [ ] Crear modelo `Cita`
- [ ] Crear adapter `CitasAdapter`
- [ ] Crear layout `item_cita.xml`
- [ ] Agregar RecyclerView a layout
- [ ] Inicializar RecyclerView en activity
- [ ] Crear 5+ citas mock

---

## 🎯 Prioridad de Implementación

### MÁXIMA PRIORIDAD ⭐⭐⭐
1. **AdminProyectosActivity** - Listado de proyectos
   - Reemplaza el proyecto único por lista
   - Impacto visual inmediato
   
2. **AdminAsesoresActivity** - Listado de asesores
   - Reemplaza el asesor único por lista
   - Impacto visual inmediato

### MEDIA PRIORIDAD ⭐⭐
3. **AdminProyectoGaleriaActivity** - Galería
   - Mejora experiencia de galería
   - GridLayout más visual

4. **AdminAsesorDetalleCarlosActivity** - Historial de citas
   - Añade más funcionalidad
   - Enriquece detalles del asesor

### BAJA PRIORIDAD ⭐
5. **AdminReportesActivity** - Tabla de reportes (Opcional)
   - Nice-to-have
   - Puede hacerse después

---

## 📌 Dependencias Necesarias

Verificar que estén en `build.gradle`:
```gradle
dependencies {
    // RecyclerView
    implementation 'androidx.recyclerview:recyclerview:1.3.1'
    
    // Material Design
    implementation 'com.google.android.material:material:1.10.0'
    
    // ConstraintLayout (para layouts)
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
}
```

---

## 🧪 Data Estática Recomendada

**Proyectos**: 4 ejemplo mínimo
**Asesores**: 6 ejemplo mínimo
**Imágenes Galería**: 10 ejemplo mínimo
**Citas**: 5 ejemplo mínimo

---

## ✅ Resultado Esperado (Fase 4)

Después de implementar RecyclerView:
- ✅ Listados funcionales y scrollables
- ✅ Items clickeables
- ✅ Navegación a detalles
- ✅ Data estática visible
- ✅ Preparado para Firebase (Fase 6)

---

## 🚀 Siguiente Fase

**Fase 5 (25/May)**: Storage Local + Notificaciones
- Guardar datos localmente con Room Database
- Implementar notificaciones push

---

**Documento**: Guía de Implementación RecyclerView  
**Fase**: 4 (04/May/2026)  
**Estado**: Listo para implementar  
**Prioridad**: ALTA


