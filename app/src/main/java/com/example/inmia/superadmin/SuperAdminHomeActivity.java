package com.example.inmia.superadmin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Calendar;

public class SuperAdminHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private MaterialCardView cardSolicitudes;
    private MaterialButton btnVerSolicitudes;
    private TextView tvGreeting;

    // Activos
    private TextView tvClientesActivos;
    private TextView tvAsesoresActivos;
    private TextView tvAdminsActivos;

    // Totales
    private TextView tvContadorInmobiliarias;
    private TextView tvContadorReservas;
    private TextView tvContadorCitas;

    // Solicitudes
    private TextView tvTotalSolicitudes;
    private TextView tvDescSolicitudes;
    private TextView tvSolicitudesDecoracion;

    private static final int COLOR_SOLICITUDES_ALERTA = 0xFFD97706;
    private static final int COLOR_SOLICITUDES_ALERTA_DECORACION = 0xFFB45F04;
    private static final int COLOR_SOLICITUDES_ALERTA_DESC = 0xFFFDE9C8;
    private static final int COLOR_SOLICITUDES_OK = 0xFF1B8D50;
    private static final int COLOR_SOLICITUDES_OK_DECORACION = 0xFF146B3D;
    private static final int COLOR_SOLICITUDES_OK_DESC = 0xFFD2F0DF;

    // Nuevos este mes
    private TextView tvNuevosEsteMes;
    private TextView tvSubtituloNuevos;

    private int totalSolicitudes = 0;

    private FirebaseFirestore db;
    private ListenerRegistration solicitudesListener;
    private boolean primerConteoSolicitudes = true;

    private final ActivityResultLauncher<String> permisosLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> NotificacionHelper.crearCanal(this)
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.sa_activity_home_superadmin);

        db = FirebaseFirestore.getInstance();

        // Vincular vistas
        bottomNav               = findViewById(R.id.bottomNavSuperAdmin);
        cardSolicitudes         = findViewById(R.id.cardSolicitudes);
        btnVerSolicitudes       = findViewById(R.id.btnVerSolicitudes);
        tvGreeting              = findViewById(R.id.tvGreeting);
        tvClientesActivos       = findViewById(R.id.tvClientesActivos);
        tvAsesoresActivos       = findViewById(R.id.tvAsesoresActivos);
        tvAdminsActivos         = findViewById(R.id.tvAdminsActivos);
        tvContadorInmobiliarias = findViewById(R.id.tvContadorInmobiliarias);
        tvContadorReservas      = findViewById(R.id.tvContadorReservas);
        tvContadorCitas         = findViewById(R.id.tvContadorCitas);
        tvTotalSolicitudes      = findViewById(R.id.tvTotalSolicitudes);
        tvDescSolicitudes       = findViewById(R.id.tvDescSolicitudes);
        tvSolicitudesDecoracion = findViewById(R.id.tvSolicitudesDecoracion);
        tvNuevosEsteMes         = findViewById(R.id.tvNuevosEsteMes);
        tvSubtituloNuevos       = findViewById(R.id.tvSubtituloNuevos);

        solicitarPermisoNotificaciones();
        configurarSubtituloMes();

        cargarNombreSuperAdmin();
        cargarDashboard();
        escucharSolicitudes();

        cardSolicitudes.setOnClickListener(v -> irAGestionUsuarios());
        btnVerSolicitudes.setOnClickListener(v -> irSolicitudes());

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                return true;
            } else if (id == R.id.nav_usuarios) {
                irAGestionUsuarios();
                return true;
            } else if (id == R.id.nav_reportes) {
                startActivity(new Intent(this, ReportesActivity.class));
                return true;
            } else if (id == R.id.nav_logs) {
                startActivity(new Intent(this, LogsActivity.class));
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class));
                return true;
            }
            return false;
        });
    }

    // ── FIREBASE ──────────────────────────────────────────────────────────────

    private void cargarDashboard() {
        // Usuarios: activos por rol + nuevos este mes (una sola consulta)
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        final Timestamp inicioMes = new Timestamp(cal.getTime());

        db.collection("usuarios").get()
                .addOnSuccessListener(query -> {
                    int clientes = 0, asesores = 0, admins = 0, nuevos = 0;
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        String rol    = doc.getString("rol");
                        Boolean activo = doc.getBoolean("activo");
                        Timestamp fc  = doc.getTimestamp("fechaCreacion");

                        boolean esActivo = Boolean.TRUE.equals(activo);
                        if ("cliente".equals(rol) && esActivo) clientes++;
                        else if ("asesor".equals(rol) && esActivo) asesores++;
                        else if ("admin".equals(rol)  && esActivo) admins++;

                        if (fc != null && !fc.toDate().before(inicioMes.toDate())) nuevos++;
                    }
                    tvClientesActivos.setText(String.valueOf(clientes));
                    tvAsesoresActivos.setText(String.valueOf(asesores));
                    tvAdminsActivos.setText(String.valueOf(admins));
                    tvNuevosEsteMes.setText(String.valueOf(nuevos));
                })
                .addOnFailureListener(e -> {
                    tvClientesActivos.setText("—");
                    tvAsesoresActivos.setText("—");
                    tvAdminsActivos.setText("—");
                    tvNuevosEsteMes.setText("—");
                });

        // Inmobiliarias
        db.collection("inmobiliarias").get()
                .addOnSuccessListener(q -> tvContadorInmobiliarias.setText(String.valueOf(q.size())))
                .addOnFailureListener(e -> tvContadorInmobiliarias.setText("—"));

        // Separaciones (reservaciones)
        db.collection("separaciones").get()
                .addOnSuccessListener(q -> tvContadorReservas.setText(String.valueOf(q.size())))
                .addOnFailureListener(e -> tvContadorReservas.setText("—"));

        // Citas
        db.collection("citas").get()
                .addOnSuccessListener(q -> tvContadorCitas.setText(String.valueOf(q.size())))
                .addOnFailureListener(e -> tvContadorCitas.setText("—"));
    }

    private void escucharSolicitudes() {
        solicitudesListener = db.collection("solicitudes")
                .whereEqualTo("estado", "pendiente")
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null) return;

                    totalSolicitudes = snapshot.size();
                    configurarSolicitudes();

                    // Notificar al dispositivo solo cuando entra una solicitud NUEVA
                    // (se omite el primer snapshot, que trae las ya existentes).
                    if (!primerConteoSolicitudes) {
                        for (DocumentChange dc : snapshot.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                NotificacionHelper.notificarSistema(
                                        this,
                                        "Nueva solicitud de asesor",
                                        "Tienes una solicitud pendiente por revisar.");
                            }
                        }
                    }
                    primerConteoSolicitudes = false;
                });
    }

    private void cargarNombreSuperAdmin() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String nombres = doc.getString("nombres");
                        if (nombres != null && !nombres.isEmpty()) {
                            tvGreeting.setText("¡Hola, " + nombres + "!");
                        }
                    }
                });
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private void configurarSubtituloMes() {
        String[] meses = {
            "enero", "febrero", "marzo", "abril", "mayo", "junio",
            "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
        };
        int mesActual = Calendar.getInstance().get(Calendar.MONTH);
        tvSubtituloNuevos.setText("registrados en " + meses[mesActual]);
    }

    private void configurarSolicitudes() {
        cardSolicitudes.setVisibility(View.VISIBLE);

        if (totalSolicitudes == 0) {
            tvTotalSolicitudes.setText("✓");
            tvDescSolicitudes.setText("No tienes solicitudes\npendientes");
            cardSolicitudes.setCardBackgroundColor(COLOR_SOLICITUDES_OK);
            tvSolicitudesDecoracion.setTextColor(COLOR_SOLICITUDES_OK_DECORACION);
            tvDescSolicitudes.setTextColor(COLOR_SOLICITUDES_OK_DESC);
            btnVerSolicitudes.setVisibility(View.GONE);
            return;
        }

        btnVerSolicitudes.setVisibility(View.VISIBLE);
        tvTotalSolicitudes.setText(String.valueOf(totalSolicitudes));
        tvDescSolicitudes.setText(totalSolicitudes == 1
                ? "asesor de ventas\nespera ser habilitado"
                : "asesores de ventas\nesperan ser habilitados");
        cardSolicitudes.setCardBackgroundColor(COLOR_SOLICITUDES_ALERTA);
        tvSolicitudesDecoracion.setTextColor(COLOR_SOLICITUDES_ALERTA_DECORACION);
        tvDescSolicitudes.setTextColor(COLOR_SOLICITUDES_ALERTA_DESC);
    }

    private void irAGestionUsuarios() {
        startActivity(new Intent(this, GestionUsuariosActivity.class));
    }

    private void irSolicitudes() {
        startActivity(new Intent(this, SolicitudesActivity.class));
    }

    private void solicitarPermisoNotificaciones() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            NotificacionHelper.crearCanal(this);
        } else {
            permisosLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (solicitudesListener != null) solicitudesListener.remove();
    }
}
