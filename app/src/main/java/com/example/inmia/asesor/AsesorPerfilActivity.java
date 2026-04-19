package com.example.inmia.asesor;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;

public class AsesorPerfilActivity extends AppCompatActivity {

    private FrameLayout frameNotificaciones;
    private FrameLayout framePerfil;
    private TextView tvBadgeNotif;
    private ActivityResultLauncher<String> pickPhotoLauncher;

    private int totalNotificaciones = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_asesor_perfil);

        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        framePerfil = findViewById(R.id.framePerfil);
        tvBadgeNotif = findViewById(R.id.tvBadgeNotif);

        configurarBadge();

        pickPhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    Toast.makeText(this, "Foto seleccionada (no se guardara aun)", Toast.LENGTH_SHORT).show();
                }
            }
        );

        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorNotificacionesActivity.class));
        });

        if (framePerfil != null) {
            framePerfil.setEnabled(false);
            framePerfil.setAlpha(0.6f);
        }
    }

    private void configurarBadge() {
        if (totalNotificaciones > 0) {
            tvBadgeNotif.setText(String.valueOf(totalNotificaciones));
            tvBadgeNotif.setVisibility(android.view.View.VISIBLE);
        } else {
            tvBadgeNotif.setVisibility(android.view.View.GONE);
        }
    }

    public void onSubirFoto(android.view.View view) {
        showSubirFotoDialog();
    }

    private void showSubirFotoDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_subir_foto);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        FrameLayout backdrop = dialog.findViewById(R.id.dialogBackdrop);
        TextView btnSeleccionar = dialog.findViewById(R.id.btnSeleccionarFoto);
        TextView btnCancelar = dialog.findViewById(R.id.btnCancelarFoto);

        if (backdrop != null) {
            backdrop.setOnClickListener(v -> dialog.dismiss());
        }

        if (btnSeleccionar != null) {
            btnSeleccionar.setOnClickListener(v -> {
                dialog.dismiss();
                pickPhotoLauncher.launch("image/*");
            });
        }

        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }
}
