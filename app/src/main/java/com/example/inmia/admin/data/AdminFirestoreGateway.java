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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        public AdminContext(String userId,
                            String email,
                            String displayName,
                            String phone,
                            String address,
                            String companyId,
                            String companyName,
                            String companyPhone,
                            String role,
                            boolean active) {
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

    public void resolveAdminContextByEmail(String email, FirestoreCallback<AdminContext> callback) {
        if (email == null || email.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("El email no puede estar vacío"));
            return;
        }

        db.collection("usuarios")
                .whereEqualTo("correo", email.trim())
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        callback.onError(new IllegalStateException("No se encontró el usuario admin"));
                        return;
                    }

                    DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                    String userId = userDoc.getId();
                    String displayName = joinNames(userDoc.getString("nombres"), userDoc.getString("apellidos"), "Administrador");
                    String phone = safeString(userDoc.getString("telefono"), "—");
                    String address = safeString(userDoc.getString("domicilio"), "—");
                    String role = safeString(userDoc.getString("rol"), "admin");
                    boolean active = getBoolean(userDoc.get("activo"), true);
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
                                        active
                                ));
                            })
                            .addOnFailureListener(callback::onError);
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

public void observeProjectById(String projectId, FirestoreCallback<Proyecto> callback) {
        if (projectId == null || projectId.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("projectId vacío"));
            return;
        }

        db.collection("proyectos").document(projectId)
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

        Map<String, Object> ubicacion = new HashMap<>();
        ubicacion.put("direccion", proyecto.getUbicacion() != null ? proyecto.getUbicacion() : "");
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

        Map<String, Object> ubicacion = new HashMap<>();
        ubicacion.put("direccion", proyecto.getUbicacion() != null ? proyecto.getUbicacion() : "");
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
        proyecto.setDescripcion(safeString(doc.getString("descripcion"), ""));
        proyecto.setUbicacion(extractDireccion(doc.get("ubicacion")));
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

        return new Tipologia(
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

        int metaVentas = getInt(doc.get("metaVentasMensual"), 0);
        int metaCitas = getInt(doc.get("metaCitasMensual"), 0);
        int metaGanancias = getInt(doc.get("metaGananciasMensual"), 0);
        int ventasActual = getInt(doc.get("ventasMensualActual"), 0);
        int citasActual = getInt(doc.get("citasMensualActual"), 0);
        int gananciasActual = getInt(doc.get("gananciasMensualActual"), 0);

        return new Asesor(
                doc.getId(),
                nombre,
                distrito,
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

    private String extractDireccion(Object value) {
        Map<String, Object> ubicacion = getMap(value);
        if (ubicacion.isEmpty()) {
            return "Ubicación no disponible";
        }
        return safeString(ubicacion.get("direccion"), "Ubicación no disponible");
    }

    private String mapEstadoProyecto(String rawEstado) {
        if (rawEstado == null) {
            return "Venta";
        }
        String normalized = rawEstado.trim().toLowerCase(Locale.getDefault());
        if (normalized.contains("plan")) {
            return "En planos";
        }
        if (normalized.contains("pre")) {
            return "En preventa";
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



