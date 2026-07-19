package com.example.inmia.admin.data;

import android.util.Log;

import com.example.inmia.R;
import com.example.inmia.admin.ReporteItem;
import com.example.inmia.models.Asesor;
import com.example.inmia.models.CitaAsesor;
import com.example.inmia.models.Proyecto;
import com.example.inmia.models.Tipologia;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class AdminFirestoreGateway {

    public interface FirestoreCallback<T> {
        void onSuccess(T value);

        void onError(Exception e);
    }

    public interface FirestoreListCallback<T> {
        void onSuccess(List<T> value);

        void onError(Exception e);
    }

    public static class AdminContext {
        private final String userId;
        private final String email;
        private final String displayName;
        private final String phone;
        private final String address;
        private final String companyId;
        private final String companyName;
        private final String companyPhone;
        private final String role;
        private final boolean active;
        private final String fotoUrl;

        public AdminContext(String userId,
                            String email,
                            String displayName,
                            String phone,
                            String address,
                            String companyId,
                            String companyName,
                            String companyPhone,
                            String role,
                            boolean active,
                            String fotoUrl) {
            this.userId = userId;
            this.email = email;
            this.displayName = displayName;
            this.phone = phone;
            this.address = address;
            this.companyId = companyId;
            this.companyName = companyName;
            this.companyPhone = companyPhone;
            this.role = role;
            this.active = active;
            this.fotoUrl = fotoUrl;
        }

        public String getUserId() {
            return userId;
        }

        public String getEmail() {
            return email;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getPhone() {
            return phone;
        }

        public String getAddress() {
            return address;
        }

        public String getCompanyId() {
            return companyId;
        }

        public String getCompanyName() {
            return companyName;
        }

        public String getCompanyPhone() {
            return companyPhone;
        }

        public String getRole() {
            return role;
        }

        public boolean isActive() {
            return active;
        }

        public String getFotoUrl() {
            return fotoUrl;
        }
    }

    public static class ReportSnapshot {
        private final String periodLabel;
        private final List<ReporteItem> items;
        private final int metaVentasPct;
        private final int tasaCierrePct;
        private final int leadsPct;

        public ReportSnapshot(String periodLabel,
                              List<ReporteItem> items,
                              int metaVentasPct,
                              int tasaCierrePct,
                              int leadsPct) {
            this.periodLabel = periodLabel;
            this.items = items;
            this.metaVentasPct = metaVentasPct;
            this.tasaCierrePct = tasaCierrePct;
            this.leadsPct = leadsPct;
        }

        public String getPeriodLabel() {
            return periodLabel;
        }

        public List<ReporteItem> getItems() {
            return items;
        }

        public int getMetaVentasPct() {
            return metaVentasPct;
        }

        public int getTasaCierrePct() {
            return tasaCierrePct;
        }

        public int getLeadsPct() {
            return leadsPct;
        }
    }

    public static class ProjectStats {
        private final int enPlanos;
        private final int enConstruccion;
        private final int entregados;

        public ProjectStats(int enPlanos, int enConstruccion, int entregados) {
            this.enPlanos = enPlanos;
            this.enConstruccion = enConstruccion;
            this.entregados = entregados;
        }

        public int getEnPlanos() {
            return enPlanos;
        }

        public int getEnConstruccion() {
            return enConstruccion;
        }

        public int getEntregados() {
            return entregados;
        }
    }

    private static final String TAG = "AdminFirestoreGateway";

    private final FirebaseFirestore db;

    public AdminFirestoreGateway() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void resolveAdminContextByUserId(String userId, FirestoreCallback<AdminContext> callback) {
        if (userId == null || userId.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("El id del usuario no puede estar vacío"));
            return;
        }

        db.collection("usuarios").document(userId).get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc == null || !userDoc.exists()) {
                        callback.onError(new IllegalStateException("No se encontró el usuario admin"));
                        return;
                    }
                    resolveContextFromUserDoc(userDoc, callback);
                })
                .addOnFailureListener(callback::onError);
    }

    public void resolveAdminContextByEmail(String email, FirestoreCallback<AdminContext> callback) {
        if (email == null || email.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("El email no puede estar vacío"));
            return;
        }

        db.collection("usuarios")
                .whereEqualTo("correo", email.trim())
                .limit(1)
                .get()
                .addOnSuccessListener(snap -> {
                    if (snap == null || snap.isEmpty()) {
                        callback.onError(new IllegalStateException(
                                "No se encontró el usuario admin con email " + email));
                        return;
                    }
                    resolveContextFromUserDoc(snap.getDocuments().get(0), callback);
                })
                .addOnFailureListener(callback::onError);
    }

    private void resolveContextFromUserDoc(DocumentSnapshot userDoc, FirestoreCallback<AdminContext> callback) {
        String userId = userDoc.getId();
        String displayName = joinNames(userDoc.getString("nombres"), userDoc.getString("apellidos"), "Administrador");
        String phone = safeString(userDoc.getString("telefono"), "—");
        String address = safeString(userDoc.getString("domicilio"), "—");
        String role = safeString(userDoc.getString("rol"), "admin");
        boolean active = getBoolean(userDoc.get("activo"), true);
        String fotoUrl = safeString(userDoc.getString("fotoUrl"), "");
        String email = safeString(userDoc.getString("correo"), "");
        String companyId = safeString(userDoc.getString("inmobiliariaId"), AdminSessionDefaults.DEFAULT_COMPANY_ID);

        db.collection("inmobiliarias").document(companyId).get()
                .addOnSuccessListener(companyDoc -> {
                    String companyName = companyDoc.exists()
                            ? safeString(companyDoc.getString("nombre"), companyId)
                            : companyId;
                    String companyPhone = companyDoc.exists() ? safeString(companyDoc.getString("telefono"), "—") : "—";
                    callback.onSuccess(new AdminContext(
                            userId,
                            email,
                            displayName,
                            phone,
                            address,
                            companyId,
                            companyName,
                            companyPhone,
                            role,
                            active,
                            fotoUrl
                    ));
                })
                .addOnFailureListener(callback::onError);
    }

    public void observeUnreadNotifications(String userId, FirestoreCallback<Integer> callback) {
        if (userId == null || userId.trim().isEmpty()) {
            callback.onSuccess(0);
            return;
        }

        db.collection("notificaciones")
                .whereEqualTo("destinatarioId", userId)
                .whereEqualTo("leida", false)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    callback.onSuccess(value != null ? value.size() : 0);
                });
    }

    public void observeProjectsByCompany(String companyId, FirestoreListCallback<Proyecto> callback) {
        observeProjectsByCompany(companyId, companyId, callback);
    }

    public void observeProjectsByCompany(String companyId, String companyName, FirestoreListCallback<Proyecto> callback) {
        if (companyId == null || companyId.trim().isEmpty()) {
            callback.onSuccess(new ArrayList<>());
            return;
        }

        db.collection("proyectos")
                .whereEqualTo("inmobiliariaId", companyId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }

                    List<Proyecto> proyectos = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            proyectos.add(mapProyecto(doc, companyName));
                        }
                    }
                    callback.onSuccess(proyectos);
                });
    }

public ListenerRegistration observeProjectById(String projectId, FirestoreCallback<Proyecto> callback) {
        if (projectId == null || projectId.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("projectId vacío"));
            return null;
        }

        return db.collection("proyectos").document(projectId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    if (value == null || !value.exists()) {
                        callback.onError(new IllegalStateException("No se encontró el proyecto"));
                        return;
                    }
                    callback.onSuccess(mapProyecto(value, safeString(value.getString("inmobiliariaId"), "")));
                });
    }

    public void getProjectById(String projectId, FirestoreCallback<Proyecto> callback) {
        if (projectId == null || projectId.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("projectId vacío"));
            return;
        }

        db.collection("proyectos").document(projectId)
                .get()
                .addOnSuccessListener(value -> {
                    if (value == null || !value.exists()) {
                        callback.onError(new IllegalStateException("No se encontró el proyecto"));
                        return;
                    }
                    callback.onSuccess(mapProyecto(value, safeString(value.getString("inmobiliariaId"), "")));
                })
                .addOnFailureListener(callback::onError);
    }

    public void observeAdvisorsByCompany(String companyId, FirestoreListCallback<Asesor> callback) {
        if (companyId == null || companyId.trim().isEmpty()) {
            callback.onSuccess(new ArrayList<>());
            return;
        }

        db.collection("usuarios")
                .whereEqualTo("rol", "asesor")
                .whereEqualTo("inmobiliariaId", companyId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }

                    List<Asesor> asesores = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            asesores.add(mapAsesor(doc));
                        }
                    }
                    callback.onSuccess(asesores);
                });
    }

    public ListenerRegistration observeSeparacionesPendientes(
            FirestoreListCallback<com.example.inmia.admin.SeparacionPendiente> callback) {
        return db.collection("separaciones")
                .whereEqualTo("estado", "aprobado")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    if (value == null) {
                        callback.onSuccess(new ArrayList<>());
                        return;
                    }

                    final List<com.example.inmia.admin.SeparacionPendiente> items = new ArrayList<>();
                    final List<com.google.firebase.firestore.QueryDocumentSnapshot> docs = new ArrayList<>();
                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                        docs.add(doc);
                    }

                    if (docs.isEmpty()) {
                        callback.onSuccess(items);
                        return;
                    }

                    final java.util.concurrent.atomic.AtomicInteger pendientes = new java.util.concurrent.atomic.AtomicInteger(docs.size());
                    final java.util.Map<String, String> nombresCache = new java.util.HashMap<>();
                    final java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : docs) {
                        String clienteId = doc.getString("clienteId");
                        String nombreProyecto = doc.getString("nombreProyecto");
                        String ubicacion = doc.getString("ubicacion");
                        String tipologia = doc.getString("tipologia");
                        Long montoLong = doc.getLong("montoSeparacion");
                        com.google.firebase.Timestamp ts = doc.getTimestamp("fechaCreacion");

                        String fechaStr = ts != null ? sdf.format(ts.toDate()) : "—";
                        String montoStr = montoLong != null
                                ? "S/ " + String.format(Locale.getDefault(), "%,d", montoLong)
                                : "—";

                        if (clienteId == null || clienteId.isEmpty()) {
                            items.add(new com.example.inmia.admin.SeparacionPendiente(
                                    doc.getId(), nombreProyecto, ubicacion,
                                    "", "—", tipologia, montoStr, fechaStr));
                            if (pendientes.decrementAndGet() == 0) callback.onSuccess(items);
                            continue;
                        }

                        String cached = nombresCache.get(clienteId);
                        if (cached != null) {
                            items.add(new com.example.inmia.admin.SeparacionPendiente(
                                    doc.getId(), nombreProyecto, ubicacion,
                                    clienteId, cached, tipologia, montoStr, fechaStr));
                            if (pendientes.decrementAndGet() == 0) callback.onSuccess(items);
                            continue;
                        }

                        db.collection("usuarios").document(clienteId).get()
                                .addOnSuccessListener(userDoc -> {
                                    String nom = userDoc.getString("nombres");
                                    String ape = userDoc.getString("apellidos");
                                    String full = ((nom != null ? nom : "") + " " + (ape != null ? ape : "")).trim();
                                    if (full.isEmpty()) full = "Cliente";
                                    nombresCache.put(clienteId, full);
                                    items.add(new com.example.inmia.admin.SeparacionPendiente(
                                            doc.getId(), nombreProyecto, ubicacion,
                                            clienteId, full, tipologia, montoStr, fechaStr));
                                    if (pendientes.decrementAndGet() == 0) callback.onSuccess(items);
                                })
                                .addOnFailureListener(e -> {
                                    items.add(new com.example.inmia.admin.SeparacionPendiente(
                                            doc.getId(), nombreProyecto, ubicacion,
                                            clienteId, "Cliente", tipologia, montoStr, fechaStr));
                                    if (pendientes.decrementAndGet() == 0) callback.onSuccess(items);
                                });
                    }
                });
    }

    public void aprobarSeparacion(String docId, FirestoreCallback<Void> callback) {
        if (docId == null || docId.isEmpty()) {
            callback.onError(new IllegalArgumentException("docId vacío"));
            return;
        }
        Map<String, Object> upd = new HashMap<>();
        upd.put("estado", "Aprobada");
        upd.put("fechaAprobacionAdmin", com.google.firebase.firestore.FieldValue.serverTimestamp());
        db.collection("separaciones").document(docId)
                .update(upd)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    public void rechazarSeparacion(String docId, FirestoreCallback<Void> callback) {
        if (docId == null || docId.isEmpty()) {
            callback.onError(new IllegalArgumentException("docId vacío"));
            return;
        }
        Map<String, Object> upd = new HashMap<>();
        upd.put("estado", "Rechazada");
        upd.put("fechaRechazoAdmin", com.google.firebase.firestore.FieldValue.serverTimestamp());
        db.collection("separaciones").document(docId)
                .update(upd)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    public void getReportePorProyecto(String companyId,
                                     long desdeMillis,
                                     long hastaMillis,
                                     String distritoFiltro,
                                     FirestoreListCallback<com.example.inmia.admin.ReporteProyectoItem> callback) {
        if (companyId == null || companyId.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("companyId vacío"));
            return;
        }

        db.collection("proyectos")
                .whereEqualTo("inmobiliariaId", companyId)
                .get()
                .addOnSuccessListener(proyectosSnap -> {
                    final List<com.example.inmia.admin.ReporteProyectoItem> resultados = new ArrayList<>();
                    if (proyectosSnap == null || proyectosSnap.isEmpty()) {
                        callback.onSuccess(resultados);
                        return;
                    }

                    final java.util.Map<String, com.google.firebase.firestore.QueryDocumentSnapshot> proyectosById = new java.util.HashMap<>();
                    final java.util.Map<String, java.util.List<String>> asesoresByProyecto = new java.util.HashMap<>();
                    final java.util.Set<String> asesorIdsGlobal = new java.util.HashSet<>();

                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : proyectosSnap) {
                        String id = doc.getId();
                        proyectosById.put(id, doc);
                        List<String> aids = castStringList(doc.get("asesoresIds"));
                        asesoresByProyecto.put(id, aids);
                        asesorIdsGlobal.addAll(aids);
                    }

                    db.collection("usuarios")
                            .whereEqualTo("rol", "asesor")
                            .whereEqualTo("inmobiliariaId", companyId)
                            .get()
                            .addOnSuccessListener(asesoresSnap -> {
                                final java.util.Map<String, String> nombresAsesores = new java.util.HashMap<>();
                                if (asesoresSnap != null) {
                                    for (com.google.firebase.firestore.QueryDocumentSnapshot a : asesoresSnap) {
                                        String nom = a.getString("nombres");
                                        String ape = a.getString("apellidos");
                                        String full = ((nom != null ? nom : "") + " " + (ape != null ? ape : "")).trim();
                                        if (full.isEmpty()) full = "Sin nombre";
                                        nombresAsesores.put(a.getId(), full);
                                    }
                                }

                                long hastaSeguro = hastaMillis;
                                if (hastaSeguro <= 0 || hastaSeguro > 4102444800000L) {
                                    hastaSeguro = System.currentTimeMillis();
                                }
                                long desdeSeguro = desdeMillis;
                                if (desdeSeguro < 0) desdeSeguro = 0;
                                Log.d("AdminReporte", "Query separaciones: desde=" + desdeSeguro + " hasta=" + hastaSeguro);
                                final com.google.firebase.Timestamp desdeTs = new com.google.firebase.Timestamp(new java.util.Date(desdeSeguro));
                                final com.google.firebase.Timestamp hastaTs = new com.google.firebase.Timestamp(new java.util.Date(hastaSeguro));

                                db.collection("separaciones")
                                        .whereGreaterThanOrEqualTo("fechaCreacion", desdeTs)
                                        .whereLessThanOrEqualTo("fechaCreacion", hastaTs)
                                        .get()
                                        .addOnSuccessListener(separacionesSnap -> {
                                            final java.util.Map<String, int[]> contadores = new java.util.HashMap<>();
                                            final java.util.Map<String, Double> montos = new java.util.HashMap<>();
                                            if (separacionesSnap != null) {
                                                for (com.google.firebase.firestore.QueryDocumentSnapshot s : separacionesSnap) {
                                                    String estado = safeString(s.getString("estado"), "");
                                                    if (!"Aprobada".equalsIgnoreCase(estado)
                                                            && !"Pagada".equalsIgnoreCase(estado)
                                                            && !"En proceso".equalsIgnoreCase(estado)
                                                            && !"aprobado".equalsIgnoreCase(estado)
                                                            && !"Rechazada".equalsIgnoreCase(estado)) {
                                                        continue;
                                                    }
                                                    String proyectoId = s.getString("proyectoId");
                                                    if (proyectoId == null) continue;

                                                    com.google.firebase.firestore.QueryDocumentSnapshot proyDoc = proyectosById.get(proyectoId);
                                                    if (proyDoc == null) continue;

                                                    if (distritoFiltro != null && !distritoFiltro.isEmpty()
                                                            && !distritoFiltro.equalsIgnoreCase(safeString(proyDoc.getString("distrito"), ""))) {
                                                        continue;
                                                    }

                                                    int[] cont = contadores.computeIfAbsent(proyectoId, k -> new int[4]);
                                                    if ("Aprobada".equalsIgnoreCase(estado) || "aprobado".equalsIgnoreCase(estado)) cont[0]++;
                                                    else if ("Pagada".equalsIgnoreCase(estado)) cont[1]++;
                                                    else if ("En proceso".equalsIgnoreCase(estado)) cont[2]++;
                                                    else if ("Rechazada".equalsIgnoreCase(estado)) cont[3]++;

                                                    Long monto = s.getLong("montoSeparacion");
                                                    if (monto != null && "Pagada".equalsIgnoreCase(estado)) {
                                                        montos.merge(proyectoId, (double) monto, Double::sum);
                                                    }
                                                }
                                            }

                                            for (java.util.Map.Entry<String, com.google.firebase.firestore.QueryDocumentSnapshot> entry : proyectosById.entrySet()) {
                                                String proyectoId = entry.getKey();
                                                com.google.firebase.firestore.QueryDocumentSnapshot doc = entry.getValue();

                                                if (distritoFiltro != null && !distritoFiltro.isEmpty()
                                                        && !distritoFiltro.equalsIgnoreCase(safeString(doc.getString("distrito"), ""))) {
                                                    continue;
                                                }

                                                int[] cont = contadores.getOrDefault(proyectoId, new int[4]);
                                                List<String> aids = asesoresByProyecto.getOrDefault(proyectoId, new ArrayList<>());
                                                StringBuilder nombres = new StringBuilder();
                                                for (int i = 0; i < aids.size(); i++) {
                                                    if (i > 0) nombres.append(", ");
                                                    String n = nombresAsesores.get(aids.get(i));
                                                    nombres.append(n != null ? n : aids.get(i));
                                                }
                                                resultados.add(new com.example.inmia.admin.ReporteProyectoItem(
                                                        proyectoId,
                                                        doc.getString("nombre"),
                                                        doc.getString("distrito"),
                                                        aids.size(),
                                                        nombres.toString(),
                                                        cont[0],
                                                        cont[1],
                                                        cont[2],
                                                        cont[3],
                                                        montos.getOrDefault(proyectoId, 0.0)
                                                ));
                                            }

                                            resultados.sort((a, b) -> Double.compare(b.getMontoTotal(), a.getMontoTotal()));
                                            callback.onSuccess(resultados);
                                        })
                                        .addOnFailureListener(callback::onError);
                            })
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getReportePorAsesor(String companyId,
                                   long desdeMillis,
                                   long hastaMillis,
                                   String distritoFiltro,
                                   String asesorIdFiltro,
                                   FirestoreListCallback<com.example.inmia.admin.ReporteAsesorItem> callback) {
        if (companyId == null || companyId.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("companyId vacío"));
            return;
        }

        db.collection("usuarios")
                .whereEqualTo("rol", "asesor")
                .whereEqualTo("inmobiliariaId", companyId)
                .get()
                .addOnSuccessListener(asesoresSnap -> {
                    if (asesoresSnap == null || asesoresSnap.isEmpty()) {
                        callback.onSuccess(new ArrayList<>());
                        return;
                    }

                    final List<com.example.inmia.admin.ReporteAsesorItem> resultados = new ArrayList<>();
                    final java.util.Map<String, com.google.firebase.firestore.QueryDocumentSnapshot> asesoresById = new java.util.HashMap<>();
                    for (com.google.firebase.firestore.QueryDocumentSnapshot a : asesoresSnap) {
                        asesoresById.put(a.getId(), a);
                    }

                    db.collection("proyectos")
                            .whereEqualTo("inmobiliariaId", companyId)
                            .get()
                            .addOnSuccessListener(proyectosSnap -> {
                                final java.util.Map<String, String> proyectoNombreById = new java.util.HashMap<>();
                                final java.util.Map<String, java.util.List<String>> asesoresByProyecto = new java.util.HashMap<>();
                                if (proyectosSnap != null) {
                                    for (com.google.firebase.firestore.QueryDocumentSnapshot p : proyectosSnap) {
                                        proyectoNombreById.put(p.getId(), safeString(p.getString("nombre"), "Sin nombre"));
                                        if (distritoFiltro != null && !distritoFiltro.isEmpty()
                                                && !distritoFiltro.equalsIgnoreCase(safeString(p.getString("distrito"), ""))) {
                                            continue;
                                        }
                                        List<String> aids = castStringList(p.get("asesoresIds"));
                                        asesoresByProyecto.put(p.getId(), aids);
                                    }
                                }

                                long hastaSeguro = hastaMillis;
                                if (hastaSeguro <= 0 || hastaSeguro > 4102444800000L) {
                                    hastaSeguro = System.currentTimeMillis();
                                }
                                long desdeSeguro = desdeMillis;
                                if (desdeSeguro < 0) desdeSeguro = 0;
                                Log.d("AdminReporte", "Query separaciones: desde=" + desdeSeguro + " hasta=" + hastaSeguro);
                                final com.google.firebase.Timestamp desdeTs = new com.google.firebase.Timestamp(new java.util.Date(desdeSeguro));
                                final com.google.firebase.Timestamp hastaTs = new com.google.firebase.Timestamp(new java.util.Date(hastaSeguro));

                                db.collection("separaciones")
                                        .whereGreaterThanOrEqualTo("fechaCreacion", desdeTs)
                                        .whereLessThanOrEqualTo("fechaCreacion", hastaTs)
                                        .get()
                                        .addOnSuccessListener(separacionesSnap -> {
                                            final java.util.Map<String, int[]> contadores = new java.util.HashMap<>();
                                            final java.util.Map<String, Double> montos = new java.util.HashMap<>();
                                            if (separacionesSnap != null) {
                                                for (com.google.firebase.firestore.QueryDocumentSnapshot s : separacionesSnap) {
                                                    String estado = safeString(s.getString("estado"), "");
                                                    if (!"Aprobada".equalsIgnoreCase(estado)
                                                            && !"Pagada".equalsIgnoreCase(estado)
                                                            && !"En proceso".equalsIgnoreCase(estado)
                                                            && !"aprobado".equalsIgnoreCase(estado)
                                                            && !"Rechazada".equalsIgnoreCase(estado)) {
                                                        continue;
                                                    }
                                                    String proyectoId = s.getString("proyectoId");
                                                    if (proyectoId == null) continue;
                                                    List<String> aids = asesoresByProyecto.get(proyectoId);
                                                    if (aids == null || aids.isEmpty()) continue;

                                                    Long monto = s.getLong("montoSeparacion");

                                                    for (String aid : aids) {
                                                        if (asesorIdFiltro != null && !asesorIdFiltro.isEmpty() && !aid.equals(asesorIdFiltro)) continue;
                                                        int[] cont = contadores.computeIfAbsent(aid, k -> new int[4]);
                                                        if ("Aprobada".equalsIgnoreCase(estado) || "aprobado".equalsIgnoreCase(estado)) cont[0]++;
                                                        else if ("Pagada".equalsIgnoreCase(estado)) cont[1]++;
                                                        else if ("En proceso".equalsIgnoreCase(estado)) cont[2]++;
                                                        else if ("Rechazada".equalsIgnoreCase(estado)) cont[3]++;
                                                        if (monto != null && "Pagada".equalsIgnoreCase(estado)) {
                                                            montos.merge(aid, (double) monto, Double::sum);
                                                        }
                                                    }
                                                }
                                            }

                                            db.collection("citas")
                                                    .whereGreaterThanOrEqualTo("fecha", desdeTs)
                                                    .whereLessThanOrEqualTo("fecha", hastaTs)
                                                    .get()
                                                    .addOnSuccessListener(citasSnap -> {
                                                        final java.util.Map<String, Integer> citasPorAsesor = new java.util.HashMap<>();
                                                        if (citasSnap != null) {
                                                            for (com.google.firebase.firestore.QueryDocumentSnapshot c : citasSnap) {
                                                                String aid = c.getString("asesorId");
                                                                if (aid == null) continue;
                                                                if (asesorIdFiltro != null && !asesorIdFiltro.isEmpty() && !aid.equals(asesorIdFiltro)) continue;
                                                                citasPorAsesor.merge(aid, 1, Integer::sum);
                                                            }
                                                        }

                                                        for (java.util.Map.Entry<String, com.google.firebase.firestore.QueryDocumentSnapshot> entry : asesoresById.entrySet()) {
                                                            String aid = entry.getKey();
                                                            if (asesorIdFiltro != null && !asesorIdFiltro.isEmpty() && !aid.equals(asesorIdFiltro)) continue;

                                                            int[] cont = contadores.getOrDefault(aid, new int[4]);
                                                            List<String> proyectosAsesor = new ArrayList<>();
                                                            for (java.util.Map.Entry<String, java.util.List<String>> e : asesoresByProyecto.entrySet()) {
                                                                if (e.getValue() != null && e.getValue().contains(aid)) {
                                                                    String n = proyectoNombreById.get(e.getKey());
                                                                    if (n != null) proyectosAsesor.add(n);
                                                                }
                                                            }
                                                            StringBuilder nombres = new StringBuilder();
                                                            for (int i = 0; i < proyectosAsesor.size(); i++) {
                                                                if (i > 0) nombres.append(", ");
                                                                if (i == 2 && proyectosAsesor.size() > 3) {
                                                                    nombres.append("y ").append(proyectosAsesor.size() - 2).append(" m\u00e1s...");
                                                                    break;
                                                                }
                                                                nombres.append(proyectosAsesor.get(i));
                                                            }

                                                            com.google.firebase.firestore.QueryDocumentSnapshot aDoc = entry.getValue();
                                                            String nom = aDoc.getString("nombres");
                                                            String ape = aDoc.getString("apellidos");
                                                            String full = ((nom != null ? nom : "") + " " + (ape != null ? ape : "")).trim();
                                                            if (full.isEmpty()) full = "Sin nombre";
                                                            String zona = safeString(aDoc.getString("zonaTrabajo"), "");

                                                            resultados.add(new com.example.inmia.admin.ReporteAsesorItem(
                                                                    aid, full, zona,
                                                                    proyectosAsesor.size(), nombres.toString(),
                                                                    cont[0], cont[1], cont[2], cont[3],
                                                                    montos.getOrDefault(aid, 0.0),
                                                                    citasPorAsesor.getOrDefault(aid, 0)
                                                            ));
                                                        }

                                                        resultados.sort((a, b) -> Double.compare(b.getMontoTotal(), a.getMontoTotal()));
                                                        callback.onSuccess(resultados);
                                                    })
                                                    .addOnFailureListener(callback::onError);
                                        })
                                        .addOnFailureListener(callback::onError);
                            })
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(callback::onError);
    }

    public void saveAsesor(String nombres, String apellidos, String correo, String telefono,
                           String documento, String zonaTrabajo, String companyId,
                           FirestoreCallback<String> callback) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("nombres", nombres);
        datos.put("apellidos", apellidos);
        datos.put("correo", correo);
        datos.put("telefono", telefono);
        datos.put("numeroDocumento", documento);
        datos.put("zonaTrabajo", zonaTrabajo);
        datos.put("domicilio", zonaTrabajo);
        datos.put("rol", "asesor");
        datos.put("activo", true);
        datos.put("inmobiliariaId", companyId);
        datos.put("metaVentasMensual", 0);
        datos.put("metaCitasMensual", 0);
        datos.put("metaGananciasMensual", 0);
        datos.put("ventasMensualActual", 0);
        datos.put("citasMensualActual", 0);
        datos.put("gananciasMensualActual", 0);

        db.collection("usuarios")
                .add(datos)
                .addOnSuccessListener(docRef -> callback.onSuccess(docRef.getId()))
                .addOnFailureListener(callback::onError);
    }

    public void updateAsesorMetas(String asesorId, int metaVentas, int metaCitas, int metaGanancias, FirestoreCallback<Void> callback) {
        if (asesorId == null || asesorId.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("asesorId vacío"));
            return;
        }

        Map<String, Object> datos = new HashMap<>();
        datos.put("metaVentasMensual", metaVentas);
        datos.put("metaCitasMensual", metaCitas);
        datos.put("metaGananciasMensual", metaGanancias);

        db.collection("usuarios").document(asesorId)
                .update(datos)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    public void updateAsesorDistritos(String asesorId, List<String> distritos, FirestoreCallback<Void> callback) {
        if (asesorId == null || asesorId.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("asesorId vacío"));
            return;
        }

        Map<String, Object> datos = new HashMap<>();
        datos.put("distritos", distritos != null ? distritos : new ArrayList<>());
        if (distritos != null && !distritos.isEmpty()) {
            datos.put("distrito", distritos.get(0));
        }

        db.collection("usuarios").document(asesorId)
                .update(datos)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    public void updateProjectDistrito(String projectId, String distrito, FirestoreCallback<Void> callback) {
        if (projectId == null || projectId.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("projectId vacío"));
            return;
        }
        db.collection("proyectos").document(projectId)
                .update("distrito", distrito != null ? distrito : "")
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    public void updateProjectAsesores(String projectId, List<String> asesoresIds, FirestoreCallback<Void> callback) {
        if (projectId == null || projectId.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("projectId vacío"));
            return;
        }
        db.collection("proyectos").document(projectId)
                .update("asesoresIds", asesoresIds != null ? asesoresIds : new ArrayList<>())
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    public void assignAllProjectsByDistritos(String asesorId, List<String> distritos, String companyId, FirestoreCallback<Integer> callback) {
        if (asesorId == null || asesorId.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("asesorId vacío"));
            return;
        }
        if (distritos == null || distritos.isEmpty()) {
            callback.onSuccess(0);
            return;
        }
        if (companyId == null || companyId.trim().isEmpty()) {
            callback.onSuccess(0);
            return;
        }

        db.collection("proyectos")
                .whereEqualTo("inmobiliariaId", companyId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot == null || querySnapshot.isEmpty()) {
                        callback.onSuccess(0);
                        return;
                    }

                    final int[] count = {0};
                    List<QueryDocumentSnapshot> toUpdate = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String projectDistrito = safeString(doc.getString("distrito"), "");
                        if (distritos.contains(projectDistrito)) {
                            List<String> asesoresIds = castStringList(doc.get("asesoresIds"));
                            if (!asesoresIds.contains(asesorId)) {
                                toUpdate.add(doc);
                            }
                        }
                    }

                    if (toUpdate.isEmpty()) {
                        callback.onSuccess(0);
                        return;
                    }

                    final int[] updated = {0};
                    for (QueryDocumentSnapshot doc : toUpdate) {
                        List<String> asesoresIds = castStringList(doc.get("asesoresIds"));
                        asesoresIds.add(asesorId);
                        db.collection("proyectos").document(doc.getId())
                                .update("asesoresIds", asesoresIds)
                                .addOnSuccessListener(aVoid -> {
                                    updated[0]++;
                                    if (updated[0] == toUpdate.size()) {
                                        callback.onSuccess(updated[0]);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    updated[0]++;
                                    if (updated[0] == toUpdate.size()) {
                                        callback.onSuccess(updated[0]);
                                    }
                                });
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    public void observeAdvisorById(String advisorId, FirestoreCallback<Asesor> callback) {
        if (advisorId == null || advisorId.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("advisorId vacío"));
            return;
        }

        db.collection("usuarios").document(advisorId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    if (value == null || !value.exists()) {
                        callback.onError(new IllegalStateException("No se encontró el asesor"));
                        return;
                    }
                    callback.onSuccess(mapAsesor(value));
                });
    }

    public void observeCitasByAdvisor(String advisorId, FirestoreListCallback<CitaAsesor> callback) {
        if (advisorId == null || advisorId.trim().isEmpty()) {
            callback.onSuccess(new ArrayList<>());
            return;
        }

        db.collection("citas")
                .whereEqualTo("asesorId", advisorId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }

                    List<CitaAsesor> citas = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            citas.add(mapCitaAsesor(doc));
                        }
                    }
                    callback.onSuccess(citas);
                });
    }

    public void observeMonthlyReportByCompany(String companyId, String period, FirestoreCallback<ReportSnapshot> callback) {
        if (companyId == null || companyId.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("companyId vacío"));
            return;
        }

        String normalizedPeriod = period != null && !period.trim().isEmpty()
                ? period.trim()
                : new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        String reportId = companyId + "_mensual_" + normalizedPeriod;

        db.collection("reportes").document(reportId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }

                    if (value == null || !value.exists()) {
                        callback.onSuccess(new ReportSnapshot(
                                normalizedPeriod,
                                new ArrayList<>(),
                                0,
                                0,
                                0
                        ));
                        return;
                    }

                    callback.onSuccess(mapReportSnapshot(value, normalizedPeriod));
                });
    }

    public void saveMonthlyReportHardcoded(String companyId, String period, FirestoreCallback<Void> callback) {
        String reportId = companyId + "_mensual_" + period;

        Map<String, Object> totales = new HashMap<>();
        totales.put("separaciones", 45);
        totales.put("ventas", 28);
        totales.put("citas", 62);
        totales.put("montoTotal", 8400000);

        Map<String, Object> asesor1 = new HashMap<>();
        asesor1.put("nombre", "Carlos Mendoza");
        asesor1.put("ventas", 12);
        asesor1.put("citas", 28);
        asesor1.put("monto", 3600000);

        Map<String, Object> asesor2 = new HashMap<>();
        asesor2.put("nombre", "Maria Garcia");
        asesor2.put("ventas", 9);
        asesor2.put("citas", 20);
        asesor2.put("monto", 2700000);

        Map<String, Object> asesor3 = new HashMap<>();
        asesor3.put("nombre", "Juan Perez");
        asesor3.put("ventas", 7);
        asesor3.put("citas", 14);
        asesor3.put("monto", 2100000);

        Map<String, Object> porAsesor = new HashMap<>();
        porAsesor.put("asesor1", asesor1);
        porAsesor.put("asesor2", asesor2);
        porAsesor.put("asesor3", asesor3);

        Map<String, Object> reportData = new HashMap<>();
        reportData.put("periodo", period);
        reportData.put("totales", totales);
        reportData.put("porAsesor", porAsesor);

        db.collection("reportes").document(reportId)
                .set(reportData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    public void observeProjectStatsByCompany(String companyId, FirestoreCallback<ProjectStats> callback) {
        if (companyId == null || companyId.trim().isEmpty()) {
            callback.onSuccess(new ProjectStats(0, 0, 0));
            return;
        }

        db.collection("proyectos")
                .whereEqualTo("inmobiliariaId", companyId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }

                    int enPlanos = 0;
                    int enConstruccion = 0;
                    int entregados = 0;

                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            String estado = safeString(doc.getString("estado"), "").toLowerCase(Locale.getDefault());
                            if (estado.contains("planos")) {
                                enPlanos++;
                            } else if (estado.contains("construccion") || estado.contains("obra")) {
                                enConstruccion++;
                            } else if (estado.contains("entregado") || estado.contains("entrega") || estado.contains("completado")) {
                                entregados++;
                            } else {
                                enPlanos++;
                            }
                        }
                    }

                    callback.onSuccess(new ProjectStats(enPlanos, enConstruccion, entregados));
                });
    }

    public void saveProject(Proyecto proyecto, String companyId, FirestoreCallback<String> callback) {
        if (proyecto == null) {
            callback.onError(new IllegalArgumentException("Proyecto no puede ser null"));
            return;
        }

        Map<String, Object> projectData = new HashMap<>();
        projectData.put("nombre", proyecto.getNombre() != null ? proyecto.getNombre() : "");
        projectData.put("distrito", proyecto.getDistrito() != null ? proyecto.getDistrito() : "");
        projectData.put("descripcion", proyecto.getDescripcion() != null ? proyecto.getDescripcion() : "");
        projectData.put("estado", proyecto.getEstadoProyecto() != null ? proyecto.getEstadoProyecto() : "En planos");
        projectData.put("inmobiliariaId", companyId);
        projectData.put("inmobiliariaNombre", proyecto.getInmobiliaria() != null ? proyecto.getInmobiliaria() : "");
        projectData.put("conAscensor", proyecto.isConAscensor());
        projectData.put("petFriendly", proyecto.isPetFriendly());
        projectData.put("antiguedad", proyecto.getAntiguedad() != null ? proyecto.getAntiguedad() : "");
        projectData.put("fechaEntregaEstimada", proyecto.getFechaLanzamiento() != null ? proyecto.getFechaLanzamiento() : "");
        projectData.put("qrUrl", proyecto.getQrCode() != null ? proyecto.getQrCode() : "");
        projectData.put("areasComunes", proyecto.getExtras() != null ? proyecto.getExtras() : new ArrayList<>());
        projectData.put("asesoresIds", proyecto.getVendedores() != null ? proyecto.getVendedores() : new ArrayList<>());
        projectData.put("imagenesUrls", proyecto.getImagenesUrls() != null ? proyecto.getImagenesUrls() : new ArrayList<>());

        Map<String, Object> ubicacion = new HashMap<>();
        ubicacion.put("direccion", proyecto.getUbicacion() != null ? proyecto.getUbicacion() : "");
        ubicacion.put("latitud", proyecto.getLatitud());
        ubicacion.put("longitud", proyecto.getLongitud());
        projectData.put("ubicacion", ubicacion);

        List<Map<String, Object>> tipologiasList = new ArrayList<>();
        if (proyecto.getTipologias() != null) {
            for (Tipologia tip : proyecto.getTipologias()) {
                Map<String, Object> tipologiaData = new HashMap<>();
                tipologiaData.put("metraje", tip.getArea() != null ? tip.getArea().replace(" m²", "").replace("m²", "").replace(" m", "") : "0");
                tipologiaData.put("numeroCuartos", tip.getDormitorios() != null ? tip.getDormitorios() : "0");
                tipologiaData.put("precio", tip.getPrecio() != null ? tip.getPrecio().replaceAll("[^\\d.]", "") : "0");
                tipologiaData.put("numeroBanos", tip.getBanos() != null ? tip.getBanos() : "0");
                tipologiaData.put("estacionamiento", tip.getEstacionamiento() != null ? tip.getEstacionamiento() : "");
                tipologiaData.put("descripcion", tip.getDescripcion() != null ? tip.getDescripcion() : "");
                tipologiaData.put("nombre", tip.getNombre() != null ? tip.getNombre() : "");
                tipologiaData.put("estado", tip.getEstado() != null ? tip.getEstado() : "Disponible");
                tipologiaData.put("certificadoEnergetico", tip.getCertificadoEnergetico() != null ? tip.getCertificadoEnergetico() : "B");
                tipologiaData.put("tipoPiso", tip.getTipoPiso() != null ? tip.getTipoPiso() : "estandar");
                tipologiaData.put("ventilacion", tip.getVentilacion() != null ? tip.getVentilacion() : "natural");
                tipologiaData.put("tipoAcabados", tip.getTipoAcabados() != null ? tip.getTipoAcabados() : "basico");
                tipologiaData.put("patio", tip.isPatio());
                tipologiaData.put("terraza", tip.isTerraza());
                tipologiaData.put("balcon", tip.isBalcon());
                tipologiaData.put("aireAcondicionado", tip.isAireAcondicionado());
                tipologiaData.put("cocinaIntegrada", tip.isCocinaIntegrada());
                tipologiaData.put("amueblado", tip.isAmueblado());
                tipologiaData.put("persianasAutomaticas", tip.isPersianasAutomaticas());
                tipologiaData.put("closets", tip.getClosets());
                tipologiaData.put("imagenesUrls", tip.getImagenesUrls() != null ? tip.getImagenesUrls() : new ArrayList<>());
                tipologiasList.add(tipologiaData);
            }
        }
        projectData.put("tipologias", tipologiasList);

        db.collection("proyectos")
                .add(projectData)
                .addOnSuccessListener(documentReference -> callback.onSuccess(documentReference.getId()))
                .addOnFailureListener(callback::onError);
    }

    public void updateProject(String projectId, Proyecto proyecto, String companyId, FirestoreCallback<Void> callback) {
        if (projectId == null || projectId.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("projectId no puede ser vacío"));
            return;
        }
        if (proyecto == null) {
            callback.onError(new IllegalArgumentException("Proyecto no puede ser null"));
            return;
        }

        Log.d("AdminFirestore", "updateProject called with projectId: " + projectId);
        Log.d("AdminFirestore", "Proyecto nombre: " + proyecto.getNombre() + ", tipologias count: " + (proyecto.getTipologias() != null ? proyecto.getTipologias().size() : 0));

        Map<String, Object> projectData = new HashMap<>();
        projectData.put("nombre", proyecto.getNombre() != null ? proyecto.getNombre() : "");
        projectData.put("distrito", proyecto.getDistrito() != null ? proyecto.getDistrito() : "");
        projectData.put("descripcion", proyecto.getDescripcion() != null ? proyecto.getDescripcion() : "");
        projectData.put("estado", proyecto.getEstadoProyecto() != null ? proyecto.getEstadoProyecto() : "En planos");
        projectData.put("inmobiliariaId", companyId);
        projectData.put("inmobiliariaNombre", proyecto.getInmobiliaria() != null ? proyecto.getInmobiliaria() : "");
        projectData.put("conAscensor", proyecto.isConAscensor());
        projectData.put("petFriendly", proyecto.isPetFriendly());
        projectData.put("antiguedad", proyecto.getAntiguedad() != null ? proyecto.getAntiguedad() : "");
        projectData.put("fechaEntregaEstimada", proyecto.getFechaLanzamiento() != null ? proyecto.getFechaLanzamiento() : "");
        projectData.put("qrUrl", proyecto.getQrCode() != null ? proyecto.getQrCode() : "");
        projectData.put("areasComunes", proyecto.getExtras() != null ? proyecto.getExtras() : new ArrayList<>());
        projectData.put("asesoresIds", proyecto.getVendedores() != null ? proyecto.getVendedores() : new ArrayList<>());
        projectData.put("imagenesUrls", proyecto.getImagenesUrls() != null ? proyecto.getImagenesUrls() : new ArrayList<>());

        Map<String, Object> ubicacion = new HashMap<>();
        ubicacion.put("direccion", proyecto.getUbicacion() != null ? proyecto.getUbicacion() : "");
        ubicacion.put("latitud", proyecto.getLatitud());
        ubicacion.put("longitud", proyecto.getLongitud());
        projectData.put("ubicacion", ubicacion);

        List<Map<String, Object>> tipologiasList = new ArrayList<>();
        if (proyecto.getTipologias() != null) {
            for (Tipologia tip : proyecto.getTipologias()) {
                Map<String, Object> tipologiaData = new HashMap<>();
                tipologiaData.put("metraje", tip.getArea() != null ? tip.getArea().replace(" m²", "").replace("m²", "").replace(" m", "") : "0");
                tipologiaData.put("numeroCuartos", tip.getDormitorios() != null ? tip.getDormitorios() : "0");
                tipologiaData.put("precio", tip.getPrecio() != null ? tip.getPrecio().replaceAll("[^\\d.]", "") : "0");
                tipologiaData.put("numeroBanos", tip.getBanos() != null ? tip.getBanos() : "0");
                tipologiaData.put("estacionamiento", tip.getEstacionamiento() != null ? tip.getEstacionamiento() : "");
                tipologiaData.put("descripcion", tip.getDescripcion() != null ? tip.getDescripcion() : "");
                tipologiaData.put("nombre", tip.getNombre() != null ? tip.getNombre() : "");
                tipologiaData.put("estado", tip.getEstado() != null ? tip.getEstado() : "Disponible");
                tipologiaData.put("certificadoEnergetico", tip.getCertificadoEnergetico() != null ? tip.getCertificadoEnergetico() : "B");
                tipologiaData.put("tipoPiso", tip.getTipoPiso() != null ? tip.getTipoPiso() : "estandar");
                tipologiaData.put("ventilacion", tip.getVentilacion() != null ? tip.getVentilacion() : "natural");
                tipologiaData.put("tipoAcabados", tip.getTipoAcabados() != null ? tip.getTipoAcabados() : "basico");
                tipologiaData.put("patio", tip.isPatio());
                tipologiaData.put("terraza", tip.isTerraza());
                tipologiaData.put("balcon", tip.isBalcon());
                tipologiaData.put("aireAcondicionado", tip.isAireAcondicionado());
                tipologiaData.put("cocinaIntegrada", tip.isCocinaIntegrada());
                tipologiaData.put("amueblado", tip.isAmueblado());
                tipologiaData.put("persianasAutomaticas", tip.isPersianasAutomaticas());
                tipologiaData.put("closets", tip.getClosets());
                tipologiasList.add(tipologiaData);
            }
        }
        projectData.put("tipologias", tipologiasList);

        db.collection("proyectos").document(projectId)
                .set(projectData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d("AdminFirestore", "updateProject success for projectId: " + projectId);
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminFirestore", "updateProject failed: " + e.getMessage());
                    callback.onError(e);
                });
    }

    private ReportSnapshot mapReportSnapshot(DocumentSnapshot doc, String fallbackPeriod) {
        Map<String, Object> totales = getMap(doc.get("totales"));
        int separaciones = getInt(totales, "separaciones", 0);
        int ventas = getInt(totales, "ventas", 0);
        int citas = getInt(totales, "citas", 0);
        int montoTotal = getInt(totales, "montoTotal", 0);
        String periodo = safeString(doc.getString("periodo"), fallbackPeriod);

        Map<String, Object> porAsesor = getMap(doc.get("porAsesor"));
        Map<String, Object> mejorAsesor = findBestAdvisor(porAsesor);
        String nombreMejorAsesor = mejorAsesor.isEmpty()
                ? "Asesor destacado"
                : safeString(mejorAsesor.get("nombre"), "Asesor destacado");
        int ventasMejorAsesor = getInt(mejorAsesor, "ventas", ventas);
        int citasMejorAsesor = getInt(mejorAsesor, "citas", citas);
        int montoMejorAsesor = getInt(mejorAsesor, "monto", montoTotal);

        int totalBase = Math.max(1, separaciones + ventas + citas);
        int metaVentasPct = Math.min(100, Math.round((ventas * 100f) / totalBase));
        int tasaCierrePct = Math.min(100, Math.round((ventas * 100f) / Math.max(1, citas)));
        int leadsPct = Math.min(100, Math.round((separaciones * 100f) / totalBase));

        List<ReporteItem> items = new ArrayList<>();
        items.add(ReporteItem.media(ventas, citas, formatSoles(montoTotal)));
        items.add(ReporteItem.estado(ventas, Math.max(1, porAsesor.size()), Math.max(0, separaciones - ventas)));

        return new ReportSnapshot(periodo, items, metaVentasPct, tasaCierrePct, leadsPct);
    }

    private Proyecto mapProyecto(DocumentSnapshot doc, String companyId) {
        Proyecto proyecto = new Proyecto();
        proyecto.setId(doc.getId());
        proyecto.setNombre(safeString(doc.getString("nombre"), "Proyecto sin nombre"));
        proyecto.setDistrito(safeString(doc.getString("distrito"), ""));
        proyecto.setDescripcion(safeString(doc.getString("descripcion"), ""));
        Map<String, Object> ubicacionMap = getMap(doc.get("ubicacion"));
        proyecto.setUbicacion(extractDireccion(ubicacionMap));
        proyecto.setLatitud(getDouble(ubicacionMap, "latitud", 0.0));
        proyecto.setLongitud(getDouble(ubicacionMap, "longitud", 0.0));
        proyecto.setEstadoProyecto(mapEstadoProyecto(doc.getString("estado")));
        proyecto.setImagenHeroPrincipal(R.drawable.onboarding1);
        proyecto.setInmobiliaria(safeString(doc.getString("inmobiliariaNombre"), companyId));
        proyecto.setConAscensor(getBoolean(doc.get("conAscensor"), false));
        proyecto.setAntiguedad(safeString(doc.getString("antiguedad"), ""));
        proyecto.setFechaLanzamiento(safeString(doc.getString("fechaEntregaEstimada"), ""));
        proyecto.setReferencia(safeString(doc.getString("qrUrl"), doc.getId()));
        proyecto.setPetFriendly(getBoolean(doc.get("petFriendly"), false));
        proyecto.setExtras(castStringList(doc.get("areasComunes")));
        proyecto.setVendedores(castStringList(doc.get("asesoresIds")));
        proyecto.setImagenesUrls(castStringList(doc.get("imagenesUrls")));

        List<Tipologia> tipologias = new ArrayList<>();
        List<Map<String, Object>> tipologiasData = castMapList(doc.get("tipologias"));
        for (int i = 0; i < tipologiasData.size(); i++) {
            tipologias.add(mapTipologia(tipologiasData.get(i), i));
        }
        proyecto.setTipologias(tipologias);
        if (!tipologias.isEmpty()) {
            proyecto.setTipologiaPrincipal(tipologias.get(0));
        }
        return proyecto;
    }

    private Tipologia mapTipologia(Map<String, Object> data, int index) {
        double metraje = getDouble(data, "metraje", 0);
        int dormitorios = getInt(data, "numeroCuartos", 0);
        double precio = getDouble(data, "precio", 0);
        int closets = dormitorios > 0 ? dormitorios : 0;
        String nombre = String.format(Locale.getDefault(), "%.0f m² · %dd", metraje, dormitorios);
        String descripcion = String.format(Locale.getDefault(), "Tipología de %.0f m² con %d dormitorios.", metraje, dormitorios);

        Tipologia tipologia = new Tipologia(
                "tip_" + (index + 1),
                nombre,
                descripcion,
                String.format(Locale.getDefault(), "%.0f m²", metraje),
                String.valueOf(dormitorios),
                "1",
                "Sin estacionamiento",
                String.format(Locale.getDefault(), "S/ %,.0f", precio),
                "Disponible",
                R.drawable.onboarding1,
                new int[0],
                false,
                "A",
                false,
                false,
                false,
                false,
                closets,
                "Laminado",
                false,
                "Natural",
                false,
                "Estándar"
        );

        List<String> imgUrls = castStringList(data.get("imagenesUrls"));
        if (!imgUrls.isEmpty()) {
            tipologia.setImagenesUrls(imgUrls);
        }

        return tipologia;
    }

    private Asesor mapAsesor(DocumentSnapshot doc) {
        String nombres = safeString(doc.getString("nombres"), "Asesor");
        String apellidos = safeString(doc.getString("apellidos"), "");
        String nombre = joinNames(nombres, apellidos, "Asesor");
        String telefono = safeString(doc.getString("telefono"), "—");
        String correo = safeString(doc.getString("correo"), "—");
        String numeroDocumento = safeString(doc.getString("numeroDocumento"), safeString(doc.getString("documento"), "—"));
        String zonaTrabajo = safeString(doc.getString("zonaTrabajo"), safeString(doc.getString("domicilio"), "—"));
        String estado = getBoolean(doc.get("activo"), true) ? "Activo" : "Inactivo";
        String rol = safeString(doc.getString("rol"), "Asesor inmobiliario");
        String distrito = safeString(doc.getString("distrito"), zonaTrabajo);
        List<String> distritos = castStringList(doc.get("distritos"));
        if (distritos.isEmpty() && !"—".equals(distrito)) {
            distritos = Collections.singletonList(distrito);
        }

        int metaVentas = getInt(doc.get("metaVentasMensual"), 0);
        int metaCitas = getInt(doc.get("metaCitasMensual"), 0);
        int metaGanancias = getInt(doc.get("metaGananciasMensual"), 0);
        int ventasActual = getInt(doc.get("ventasMensualActual"), 0);
        int citasActual = getInt(doc.get("citasMensualActual"), 0);
        int gananciasActual = getInt(doc.get("gananciasMensualActual"), 0);

        Asesor asesor = new Asesor(
                doc.getId(),
                nombre,
                distrito,
                distritos,
                rol,
                correo,
                telefono,
                numeroDocumento,
                estado,
                zonaTrabajo,
                metaVentas,
                metaCitas,
                metaGanancias,
                ventasActual,
                citasActual,
                gananciasActual,
                R.drawable.avatar_asesor_1
        );
        asesor.setFotoUrl(safeString(doc.getString("fotoUrl"), ""));
        return asesor;
    }

    private CitaAsesor mapCitaAsesor(DocumentSnapshot doc) {
        String cliente = safeString(doc.getString("clienteNombre"), safeString(doc.getString("clienteId"), "Cliente"));
        String proyecto = safeString(doc.getString("proyectoNombre"), safeString(doc.getString("proyectoId"), "Proyecto"));
        String estado = safeString(doc.getString("estado"), "Pendiente");
        String fecha = "Sin fecha";
        String hora = "--:--";

        Timestamp inicio = doc.getTimestamp("fechaHoraInicio");
        if (inicio != null) {
            Date fechaInicio = inicio.toDate();
            SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm", Locale.getDefault());
            fecha = sdfFecha.format(fechaInicio);
            hora = sdfHora.format(fechaInicio);
        }

        return new CitaAsesor(cliente, fecha, hora, proyecto, estado);
    }

    private Map<String, Object> findBestAdvisor(Map<String, Object> porAsesor) {
        if (porAsesor == null || porAsesor.isEmpty()) {
            return new java.util.HashMap<>();
        }

        Map<String, Object> winner = new java.util.HashMap<>();
        int maxVentas = Integer.MIN_VALUE;
        for (Map.Entry<String, Object> entry : porAsesor.entrySet()) {
            Map<String, Object> data = getMap(entry.getValue());
            int ventas = getInt(data, "ventas", 0);
            if (ventas > maxVentas) {
                maxVentas = ventas;
                winner = new java.util.HashMap<>(data);
                winner.putIfAbsent("nombre", entry.getKey());
            }
        }
        return winner;
    }

    private String extractDireccion(Map<String, Object> ubicacion) {
        if (ubicacion == null || ubicacion.isEmpty()) {
            return "Ubicación no disponible";
        }
        return safeString(ubicacion.get("direccion"), "Ubicación no disponible");
    }

    private String mapEstadoProyecto(String rawEstado) {
        if (rawEstado == null) {
            return "En venta";
        }
        String normalized = rawEstado.trim().toLowerCase(Locale.getDefault());
        if (normalized.contains("plan")) {
            return "En planos";
        }
        if (normalized.contains("pre")) {
            return "En preventa";
        }
        if (normalized.contains("entreg")) {
            return "Entregado";
        }
        return "En venta";
    }

    private List<Map<String, Object>> castMapList(Object value) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (!(value instanceof List)) {
            return result;
        }
        for (Object item : (List<?>) value) {
            if (item instanceof Map) {
                //noinspection unchecked
                result.add((Map<String, Object>) item);
            }
        }
        return result;
    }

    private List<String> castStringList(Object value) {
        List<String> result = new ArrayList<>();
        if (!(value instanceof List)) {
            return result;
        }
        for (Object item : (List<?>) value) {
            if (item != null) {
                result.add(String.valueOf(item));
            }
        }
        return result;
    }

    private Map<String, Object> getMap(Object value) {
        if (value instanceof Map) {
            //noinspection unchecked
            return (Map<String, Object>) value;
        }
        return new java.util.HashMap<>();
    }

    private String safeString(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private String safeString(Object value, String fallback) {
        if (value == null) {
            return fallback;
        }
        return safeString(String.valueOf(value), fallback);
    }

    private String joinNames(String first, String second, String fallback) {
        StringBuilder builder = new StringBuilder();
        if (first != null && !first.trim().isEmpty()) {
            builder.append(first.trim());
        }
        if (second != null && !second.trim().isEmpty()) {
            if (builder.length() > 0) builder.append(' ');
            builder.append(second.trim());
        }
        return builder.length() > 0 ? builder.toString() : fallback;
    }

    private boolean getBoolean(Object value, boolean fallback) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return fallback;
    }

    private int getInt(Map<String, Object> map, String key, int fallback) {
        if (map == null) {
            return fallback;
        }
        return getInt(map.get(key), fallback);
    }

    private int getInt(Object value, int fallback) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt(((String) value).trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return fallback;
    }

    private double getDouble(Map<String, Object> map, String key, double fallback) {
        if (map == null) {
            return fallback;
        }
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble(((String) value).trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return fallback;
    }

    private String formatSoles(int value) {
        return String.format(Locale.getDefault(), "S/ %,.0f", (double) value);
    }
}



