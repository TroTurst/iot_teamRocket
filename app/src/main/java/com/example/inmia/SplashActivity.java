package com.example.inmia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.inmia.util.RolRouter;
import com.example.inmia.util.SesionLocal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    // Duración del splash en milisegundos (2.5 segundos)
    private static final int SPLASH_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ocultar la ActionBar para pantalla completa
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_splash);

        solicitarPermisoNotificaciones();
        abrirAjustesSiNotificacionesDeshabilitadas();

        // Navegar según el estado de sesión después del tiempo definido
        new Handler(Looper.getMainLooper()).postDelayed(this::continuarFlujo, SPLASH_DURATION);
    }

    /** Si ya hay una sesión de Firebase Auth activa y la cuenta sigue habilitada, salta directo
     *  a su pantalla de inicio. Si no, sigue el flujo normal de Onboarding → Login. */
    private void continuarFlujo() {
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioActual == null || !SesionLocal.estaActiva(this)) {
            // Si FirebaseAuth aún reporta un usuario pero la app marcó la sesión como
            // cerrada explícitamente, se fuerza el signOut por si quedó un estado
            // desincronizado en disco (ver SesionLocal).
            if (usuarioActual != null) {
                FirebaseAuth.getInstance().signOut();
            }
            irAOnboarding();
            return;
        }

        FirebaseFirestore.getInstance().collection("usuarios").document(usuarioActual.getUid()).get()
                .addOnSuccessListener(doc -> {
                    boolean activo = doc.exists() && Boolean.TRUE.equals(doc.getBoolean("activo"));
                    Intent intent = activo
                            ? RolRouter.resolverIntentDestino(this, doc, usuarioActual.getEmail())
                            : null;

                    if (intent == null) {
                        FirebaseAuth.getInstance().signOut();
                        irAOnboarding();
                        return;
                    }

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> irAOnboarding());
    }

    private void irAOnboarding() {
        Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
        startActivity(intent);
        finish(); // Destruye el Splash para que no vuelva con "atrás"
    }

    private void solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                1001);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 1001) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        abrirAjustesNotificaciones();
    }

    private void abrirAjustesSiNotificacionesDeshabilitadas() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }
        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            return;
        }
        abrirAjustesNotificaciones();
    }

    private void abrirAjustesNotificaciones() {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (Exception ex) {
            Intent fallback = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            fallback.setData(Uri.fromParts("package", getPackageName(), null));
            fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(fallback);
        }
    }
}