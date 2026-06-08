package com.example.inmia.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Solicitud;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SolicitudesActivity extends AppCompatActivity
        implements SolicitudAdapter.OnSolicitudListener {

    private RecyclerView recyclerSolicitudes;
    private SolicitudAdapter adapter;
    private List<Solicitud> listaSolicitudes;
    private TextView tvContador;
    private BottomNavigationView bottomNav;
    private View layoutEmpty;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.sa_activity_solicitudes);

        db = FirebaseFirestore.getInstance();

        recyclerSolicitudes = findViewById(R.id.recyclerSolicitudes);
        tvContador          = findViewById(R.id.tvContador);
        bottomNav           = findViewById(R.id.bottomNavSuperAdmin);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        listaSolicitudes = new ArrayList<>();
        recyclerSolicitudes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SolicitudAdapter(this, listaSolicitudes, this);
        recyclerSolicitudes.setAdapter(adapter);

        bottomNav.setSelectedItemId(R.id.nav_usuarios);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio)   { finish(); return true; }
            if (id == R.id.nav_usuarios) { irAGestionUsuarios(); return true; }
            if (id == R.id.nav_reportes) { startActivity(new Intent(this, ReportesActivity.class)); return true; }
            if (id == R.id.nav_logs)     { startActivity(new Intent(this, LogsActivity.class)); return true; }
            if (id == R.id.nav_perfil)   { startActivity(new Intent(this, PerfilActivity.class)); return true; }
            return false;
        });

        cargarSolicitudes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarSolicitudes();
    }

    // ── FIRESTORE ─────────────────────────────────────────────────────────────

    private void cargarSolicitudes() {
        db.collection("solicitudes")
                .whereEqualTo("estado", "pendiente")
                .get()
                .addOnSuccessListener(query -> {
                    listaSolicitudes.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        listaSolicitudes.add(docToSolicitud(doc));
                    }
                    adapter.notifyDataSetChanged();
                    actualizarContador();
                    if (layoutEmpty != null) {
                        layoutEmpty.setVisibility(listaSolicitudes.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar solicitudes", Toast.LENGTH_SHORT).show());
    }

    private Solicitud docToSolicitud(QueryDocumentSnapshot doc) {
        String nombres    = str(doc, "nombres");
        String apellidos  = str(doc, "apellidos");
        String oficina    = str(doc, "oficina");
        String inmobId    = str(doc, "inmobiliariaId");
        String inmobNombre = str(doc, "inmobiliariaNombre");
        String correo     = str(doc, "correo");
        String telefono   = str(doc, "telefono");
        String tipoDoc    = str(doc, "tipoDocumento");
        String numDoc     = str(doc, "numeroDocumento");
        String fechaNac   = str(doc, "fechaNacimiento");
        String domicilio  = str(doc, "domicilio");
        String fotoUrl    = str(doc, "fotoUrl");
        String adminId    = str(doc, "adminId");
        Timestamp ts      = doc.getTimestamp("fechaSolicitud");
        String espera     = calcularTiempoEspera(ts);

        return new Solicitud(doc.getId(), nombres, apellidos, oficina, inmobId,
                inmobNombre, correo, telefono, espera, tipoDoc, numDoc, fechaNac, domicilio, fotoUrl, adminId);
    }

    private String str(QueryDocumentSnapshot doc, String campo) {
        String val = doc.getString(campo);
        return val != null ? val : "";
    }

    private String calcularTiempoEspera(Timestamp ts) {
        if (ts == null) return "Recién enviado";
        long diff  = System.currentTimeMillis() - ts.toDate().getTime();
        long horas = diff / (1000 * 60 * 60);
        if (horas < 1)  return "Hace menos de 1 hora";
        if (horas == 1) return "Hace 1 hora";
        if (horas < 24) return "Hace " + horas + " horas";
        long dias = horas / 24;
        return dias == 1 ? "Ayer" : "Hace " + dias + " días";
    }

    // ── CALLBACKS DEL ADAPTER ─────────────────────────────────────────────────

    @Override
    public void onHabilitar(Solicitud solicitud, int position) {
        habilitarAsesor(solicitud, position);
    }

    @Override
    public void onRechazar(Solicitud solicitud, int position) {
        rechazarSolicitud(solicitud, position);
    }

    @Override
    public void onVerPerfil(Solicitud solicitud) {
        Intent intent = new Intent(this, PerfilAsesorActivity.class);
        intent.putExtra(PerfilAsesorActivity.EXTRA_FIRESTORE_ID,    solicitud.getFirestoreId());
        intent.putExtra(PerfilAsesorActivity.EXTRA_NOMBRE,          solicitud.getNombre() + " " + solicitud.getApellidos());
        intent.putExtra(PerfilAsesorActivity.EXTRA_APELLIDOS,       solicitud.getApellidos());
        intent.putExtra(PerfilAsesorActivity.EXTRA_INMOBILIARIA,    solicitud.getInmobiliariaNombre());
        intent.putExtra(PerfilAsesorActivity.EXTRA_INMOBILIARIA_ID, solicitud.getInmobiliariaId());
        intent.putExtra(PerfilAsesorActivity.EXTRA_OFICINA,         solicitud.getOficina());
        intent.putExtra(PerfilAsesorActivity.EXTRA_DOCUMENTO,       solicitud.getDocumento());
        intent.putExtra(PerfilAsesorActivity.EXTRA_FECHA_NAC,       solicitud.getFechaNac());
        intent.putExtra(PerfilAsesorActivity.EXTRA_CORREO,          solicitud.getCorreo());
        intent.putExtra(PerfilAsesorActivity.EXTRA_TELEFONO,        solicitud.getTelefono());
        intent.putExtra(PerfilAsesorActivity.EXTRA_DOMICILIO,       solicitud.getDomicilio());
        intent.putExtra(PerfilAsesorActivity.EXTRA_FOTO_URL,        solicitud.getFotoUrl());
        intent.putExtra(PerfilAsesorActivity.EXTRA_TIPO_DOCUMENTO,  solicitud.getTipoDocumento());
        intent.putExtra(PerfilAsesorActivity.EXTRA_NUM_DOCUMENTO,   solicitud.getNumeroDocumento());
        startActivity(intent);
    }

    // ── HABILITAR ─────────────────────────────────────────────────────────────

    private void habilitarAsesor(Solicitud solicitud, int position) {
        String correo = solicitud.getCorreo();
        if (correo.isEmpty()) {
            Toast.makeText(this, "El asesor no tiene correo registrado", Toast.LENGTH_SHORT).show();
            return;
        }

        String tempPassword = UUID.randomUUID().toString().substring(0, 12) + "!A1";

        FirebaseApp secondaryApp;
        try {
            secondaryApp = FirebaseApp.initializeApp(this,
                    FirebaseApp.getInstance().getOptions(), "creacion_asesor");
        } catch (IllegalStateException e) {
            secondaryApp = FirebaseApp.getInstance("creacion_asesor");
        }

        FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(secondaryApp);
        final FirebaseApp appRef = secondaryApp;

        secondaryAuth.createUserWithEmailAndPassword(correo, tempPassword)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();
                    secondaryAuth.signOut();
                    try { appRef.delete(); } catch (Exception ignored) {}

                    FirebaseAuth.getInstance().sendPasswordResetEmail(correo);
                    guardarAsesorEnUsuarios(uid, solicitud, position);
                })
                .addOnFailureListener(e -> {
                    try { appRef.delete(); } catch (Exception ignored) {}
                    String msg = e.getMessage() != null ? e.getMessage() : "";
                    if (msg.contains("already in use")) {
                        Toast.makeText(this, "Este correo ya tiene cuenta registrada", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error al crear cuenta: " + msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void guardarAsesorEnUsuarios(String uid, Solicitud solicitud, int position) {
        String inmobId = solicitud.getInmobiliariaId();
        String adminId = solicitud.getAdminId();

        if (!inmobId.isEmpty() || adminId.isEmpty()) {
            escribirAsesorEnFirestore(uid, solicitud, inmobId, position);
        } else {
            // Fallback: solicitud antigua sin inmobiliariaId, se recupera desde el doc del admin
            db.collection("usuarios").document(adminId).get()
                    .addOnSuccessListener(adminDoc -> {
                        String resolvedId = adminDoc.getString("inmobiliariaId");
                        escribirAsesorEnFirestore(uid, solicitud,
                                resolvedId != null ? resolvedId : "", position);
                    })
                    .addOnFailureListener(e ->
                            escribirAsesorEnFirestore(uid, solicitud, "", position));
        }
    }

    private void escribirAsesorEnFirestore(String uid, Solicitud solicitud,
                                           String inmobiliariaId, int position) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("nombres",         solicitud.getNombre());
        datos.put("apellidos",       solicitud.getApellidos());
        datos.put("tipoDocumento",   solicitud.getTipoDocumento());
        datos.put("numeroDocumento", solicitud.getNumeroDocumento());
        datos.put("fechaNacimiento", solicitud.getFechaNac());
        datos.put("correo",          solicitud.getCorreo());
        datos.put("telefono",        solicitud.getTelefono());
        datos.put("domicilio",       solicitud.getDomicilio());
        datos.put("oficina",         solicitud.getOficina());
        datos.put("fotoUrl",         solicitud.getFotoUrl());
        datos.put("inmobiliariaId",  inmobiliariaId);
        datos.put("rol",             "asesor");
        datos.put("activo",          true);
        datos.put("fechaCreacion",   FieldValue.serverTimestamp());

        db.collection("usuarios").document(uid).set(datos)
                .addOnSuccessListener(unused -> {
                    actualizarEstadoSolicitud(solicitud.getFirestoreId(), "aprobado");
                    adapter.eliminarItem(position);
                    actualizarContador();

                    NotificacionHelper.enviar(this,
                            "Asesor habilitado",
                            solicitud.getNombre() + " " + solicitud.getApellidos()
                                    + " ha sido habilitado como asesor.",
                            NotificacionHelper.TIPO_ASESOR_HABILITADO);

                    Toast.makeText(this,
                            solicitud.getNombre() + " habilitado. Se envió correo para establecer contraseña.",
                            Toast.LENGTH_LONG).show();

                    if (listaSolicitudes.isEmpty()) irAGestionUsuarios();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar asesor: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }

    // ── RECHAZAR ──────────────────────────────────────────────────────────────

    private void rechazarSolicitud(Solicitud solicitud, int position) {
        actualizarEstadoSolicitud(solicitud.getFirestoreId(), "rechazado");
        adapter.eliminarItem(position);
        actualizarContador();

        NotificacionHelper.enviar(this,
                "Solicitud rechazada",
                "La solicitud de " + solicitud.getNombre() + " ha sido rechazada.",
                NotificacionHelper.TIPO_ASESOR_RECHAZADO);

        Toast.makeText(this, "Solicitud rechazada.", Toast.LENGTH_SHORT).show();

        if (listaSolicitudes.isEmpty()) irAGestionUsuarios();
    }

    private void actualizarEstadoSolicitud(String firestoreId, String estado) {
        if (firestoreId == null || firestoreId.isEmpty()) return;
        db.collection("solicitudes").document(firestoreId).update("estado", estado);
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private void actualizarContador() {
        int total = listaSolicitudes.size();
        tvContador.setText(total + (total == 1
                ? " solicitud pendiente"
                : " solicitudes pendientes"));
    }

    private void irAGestionUsuarios() {
        Intent intent = new Intent(this, GestionUsuariosActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
