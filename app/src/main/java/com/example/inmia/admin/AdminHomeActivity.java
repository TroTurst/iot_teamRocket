package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.inmia.R;
import com.example.inmia.admin.data.AdminFirestoreGateway;
import com.example.inmia.admin.data.AdminFirestoreGateway.AdminContext;
import com.example.inmia.models.Proyecto;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private TextView tvNombreEmpresa;
    private TextView tvEstadoProyecto;
    private TextView tvNombreProyecto;
    private TextView tvDescripcionProyecto;
    private TextView tvCountProyectos;
    private TextView tvCountAsesores;
    private ImageView imgFotoRef1, imgFotoRef2;

    private AdminFirestoreGateway gateway;
    private String companyId;
    private int totalNotificaciones = 5;
    private Proyecto proyectoDestacado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_home);

        gateway = new AdminFirestoreGateway();

        bottomNav           = findViewById(R.id.bottomNavAdmin);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif        = findViewById(R.id.tvBadgeNotif);
        tvNombreEmpresa     = findViewById(R.id.tvNombreEmpresa);
        tvEstadoProyecto    = findViewById(R.id.tvEstadoProyecto);
        tvNombreProyecto    = findViewById(R.id.tvNombreProyecto);
        tvDescripcionProyecto = findViewById(R.id.tvDescripcionProyecto);
        tvCountProyectos    = findViewById(R.id.tvCountProyectos);
        tvCountAsesores     = findViewById(R.id.tvCountAsesores);
        imgFotoRef1         = findViewById(R.id.imgFotoRef1);
        imgFotoRef2         = findViewById(R.id.imgFotoRef2);

        findViewById(R.id.btnVerMasProyecto).setOnClickListener(v -> {
            if (proyectoDestacado != null) {
                Intent intent = new Intent(this, AdminProyectoDetalleActivity.class);
                intent.putExtra("proyecto_id", proyectoDestacado.getId());
                startActivity(intent);
            }
        });

        configurarBadge();
        bottomNav.setSelectedItemId(R.id.nav_inicio);

        gateway.resolveAdminContextByUserId(FirebaseAuth.getInstance().getUid(), new AdminFirestoreGateway.FirestoreCallback<AdminContext>() {
            @Override
            public void onSuccess(AdminContext context) {
                companyId = context.getCompanyId();
                if (tvNombreEmpresa != null) {
                    tvNombreEmpresa.setText(context.getCompanyName());
                }

                // Load reference photos from inmobiliaria
                if (companyId != null && !companyId.isEmpty()) {
                    FirebaseFirestore.getInstance().collection("inmobiliarias")
                            .document(companyId).get()
                            .addOnSuccessListener(doc -> {
                                if (!doc.exists()) return;
                                List<String> fotos = (List<String>) doc.get("fotosPromocionales");
                                if (fotos == null || fotos.isEmpty()) return;
                                if (fotos.size() > 0 && fotos.get(0) != null && !fotos.get(0).isEmpty()) {
                                    imgFotoRef1.setVisibility(View.VISIBLE);
                                    Glide.with(AdminHomeActivity.this).load(fotos.get(0))
                                            .centerCrop().placeholder(R.drawable.ic_add).into(imgFotoRef1);
                                }
                                if (fotos.size() > 1 && fotos.get(1) != null && !fotos.get(1).isEmpty()) {
                                    imgFotoRef2.setVisibility(View.VISIBLE);
                                    Glide.with(AdminHomeActivity.this).load(fotos.get(1))
                                            .centerCrop().placeholder(R.drawable.ic_add).into(imgFotoRef2);
                                }
                            });
                }

                gateway.observeUnreadNotifications(context.getUserId(), new AdminFirestoreGateway.FirestoreCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer count) {
                        totalNotificaciones = count != null ? count : 0;
                        configurarBadge();
                    }

                    @Override
                    public void onError(Exception e) {
                        totalNotificaciones = 0;
                        configurarBadge();
                    }
                });

                gateway.observeProjectsByCompany(companyId, context.getCompanyName(), new AdminFirestoreGateway.FirestoreListCallback<Proyecto>() {
                    @Override
                    public void onSuccess(List<Proyecto> proyectos) {
                        runOnUiThread(() -> {
                            tvCountProyectos.setText(String.valueOf(proyectos.size()));
                            if (!proyectos.isEmpty()) {
                                proyectoDestacado = proyectos.get(0);
                                tvEstadoProyecto.setText(proyectoDestacado.getEstadoProyecto());
                                tvNombreProyecto.setText(proyectoDestacado.getNombre());
                                tvDescripcionProyecto.setText(proyectoDestacado.getDescripcion());
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() -> tvCountProyectos.setText("0"));
                    }
                });

                gateway.observeAdvisorsByCompany(companyId, new AdminFirestoreGateway.FirestoreListCallback<com.example.inmia.models.Asesor>() {
                    @Override
                    public void onSuccess(List<com.example.inmia.models.Asesor> value) {
                        runOnUiThread(() -> {
                            tvCountAsesores.setText(String.valueOf(value != null ? value.size() : 0));
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() -> tvCountAsesores.setText("0"));
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminHomeActivity.this,
                        "No se pudo cargar el perfil del admin",
                        Toast.LENGTH_SHORT).show();
            }
        });

        frameNotificaciones.setOnClickListener(v -> {
            Toast.makeText(this,
                    "Tienes " + totalNotificaciones + " notificaciones",
                    Toast.LENGTH_SHORT).show();
            limpiarBadge();
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                return true;
            } else if (id == R.id.nav_proyectos) {
                navegarATab(AdminProyectosActivity.class);
                return true;
            } else if (id == R.id.nav_asesores) {
                navegarATab(AdminAsesoresActivity.class);
                return true;
            } else if (id == R.id.nav_reportes) {
                navegarATab(AdminReportesActivity.class);
                return true;
            } else if (id == R.id.nav_perfil) {
                navegarATab(AdminPerfilActivity.class);
                return true;
            }

            return false;
        });
    }

    private void configurarBadge() {
        if (totalNotificaciones > 0) {
            tvBadgeNotif.setText(String.valueOf(totalNotificaciones));
            tvBadgeNotif.setVisibility(View.VISIBLE);
        } else {
            tvBadgeNotif.setVisibility(View.GONE);
        }
    }

    private void limpiarBadge() {
        totalNotificaciones = 0;
        tvBadgeNotif.setVisibility(View.GONE);
    }

    private void navegarATab(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}