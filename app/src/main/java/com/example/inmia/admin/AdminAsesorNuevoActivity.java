package com.example.inmia.admin;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.inmia.R;
import com.example.inmia.models.Log;
import com.example.inmia.util.LogHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminAsesorNuevoActivity extends AppCompatActivity {

    private ImageView imgFoto;
    private TextInputEditText etNombre, etApellido, etDni, etFechaNac;
    private TextInputEditText etEmail, etTelefono, etDomicilio, etOficina;
    private AutoCompleteTextView actvTipoDocumento;
    private MaterialButton btnCrear;

    private Uri fotoUri = null;
    private FirebaseFirestore db;

    private final ActivityResultLauncher<String> fotoLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    fotoUri = uri;
                    imgFoto.setPadding(0, 0, 0, 0);
                    imgFoto.clearColorFilter();
                    Glide.with(this).load(uri).circleCrop().into(imgFoto);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_admin_asesor_nuevo);

        db = FirebaseFirestore.getInstance();

        imgFoto           = findViewById(R.id.imgFoto);
        etNombre          = findViewById(R.id.etNombreAsesor);
        etApellido        = findViewById(R.id.etApellidoAsesor);
        actvTipoDocumento = findViewById(R.id.actvTipoDocumento);
        etDni             = findViewById(R.id.etDniAsesor);
        etFechaNac        = findViewById(R.id.etFechaNac);
        etEmail           = findViewById(R.id.etEmailAsesor);
        etTelefono        = findViewById(R.id.etTelefonoAsesor);
        etDomicilio       = findViewById(R.id.etDomicilioAsesor);
        etOficina         = findViewById(R.id.etOficinaAsesor);
        btnCrear          = findViewById(R.id.btnCrearAsesor);

        String[] tipos = {"DNI", "Carnet de Extranjería"};
        actvTipoDocumento.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, tipos));

        findViewById(R.id.frameAvatar).setOnClickListener(v -> fotoLauncher.launch("image/*"));

        etFechaNac.setOnClickListener(v -> mostrarDatePicker());
        ((com.google.android.material.textfield.TextInputLayout)
                findViewById(R.id.tilFechaNac)).setEndIconOnClickListener(v -> mostrarDatePicker());

        findViewById(R.id.btnBackNuevoAsesor).setOnClickListener(v -> finish());

        btnCrear.setOnClickListener(v -> enviarSolicitud());
    }

    private void mostrarDatePicker() {
        Calendar hoy = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(this,
                (view, anio, mes, dia) ->
                        etFechaNac.setText(String.format("%02d/%02d/%04d", dia, mes + 1, anio)),
                hoy.get(Calendar.YEAR) - 25,
                hoy.get(Calendar.MONTH),
                hoy.get(Calendar.DAY_OF_MONTH));
        picker.getDatePicker().setMaxDate(System.currentTimeMillis());
        picker.show();
    }

    private void enviarSolicitud() {
        String nombre    = texto(etNombre);
        String apellido  = texto(etApellido);
        String tipoDoc   = actvTipoDocumento.getText().toString().trim();
        String numDoc    = texto(etDni);
        String fechaNac  = texto(etFechaNac);
        String email     = texto(etEmail);
        String telefono  = texto(etTelefono);
        String domicilio = texto(etDomicilio);
        String oficina   = texto(etOficina);

        if (nombre.isEmpty() || apellido.isEmpty() || numDoc.isEmpty()
                || email.isEmpty() || oficina.isEmpty()) {
            Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        btnCrear.setEnabled(false);
        btnCrear.setText("Enviando...");

        String adminId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";

        String tipoDocFinal = tipoDoc.isEmpty() ? "DNI" : tipoDoc;
        if (adminId.isEmpty()) {
            btnCrear.setEnabled(true);
            btnCrear.setText("Enviar solicitud");
            Toast.makeText(this, "Error: sesión no válida. Vuelve a iniciar sesión.", Toast.LENGTH_LONG).show();
            return;
        }

        db.collection("usuarios").document(adminId).get()
                .addOnSuccessListener(adminDoc -> {
                    String inmobId = (adminDoc.exists()
                            && adminDoc.getString("inmobiliariaId") != null)
                            ? adminDoc.getString("inmobiliariaId") : "";
                    if (inmobId.isEmpty()) {
                        btnCrear.setEnabled(true);
                        btnCrear.setText("Enviar solicitud");
                        Toast.makeText(this,
                                "Tu cuenta no tiene una inmobiliaria asociada. Contacta al superadmin.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    db.collection("inmobiliarias").document(inmobId).get()
                            .addOnSuccessListener(inmobDoc -> {
                                String nomInmob = (inmobDoc.exists()
                                        && inmobDoc.getString("nombre") != null)
                                        ? inmobDoc.getString("nombre") : "";
                                escribirSolicitud(nombre, apellido, tipoDocFinal, numDoc,
                                        fechaNac, email, telefono, domicilio, oficina,
                                        inmobId, nomInmob, adminId);
                            })
                            .addOnFailureListener(e ->
                                    // Tenemos el ID aunque falle leer el nombre
                                    escribirSolicitud(nombre, apellido, tipoDocFinal, numDoc,
                                            fechaNac, email, telefono, domicilio, oficina,
                                            inmobId, "", adminId));
                })
                .addOnFailureListener(e -> {
                    btnCrear.setEnabled(true);
                    btnCrear.setText("Enviar solicitud");
                    Toast.makeText(this,
                            "Error al verificar tu cuenta. Intenta de nuevo.",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void escribirSolicitud(String nombre, String apellido, String tipoDoc,
            String numDoc, String fechaNac, String email, String telefono,
            String domicilio, String oficina, String inmobiliariaId,
            String inmobiliariaNombre, String adminId) {

        Map<String, Object> solicitud = new HashMap<>();
        solicitud.put("nombres",             nombre);
        solicitud.put("apellidos",           apellido);
        solicitud.put("tipoDocumento",       tipoDoc);
        solicitud.put("numeroDocumento",     numDoc);
        solicitud.put("fechaNacimiento",     fechaNac);
        solicitud.put("correo",              email);
        solicitud.put("telefono",            telefono);
        solicitud.put("domicilio",           domicilio);
        solicitud.put("oficina",             oficina);
        solicitud.put("fotoUrl",             "");
        solicitud.put("inmobiliariaId",      inmobiliariaId);
        solicitud.put("inmobiliariaNombre",  inmobiliariaNombre);
        solicitud.put("adminId",             adminId);
        solicitud.put("fechaSolicitud",      Timestamp.now());
        solicitud.put("estado",              "pendiente");

        db.collection("solicitudes").add(solicitud)
                .addOnSuccessListener(ref -> {
                    LogHelper.registrar(
                            "Se solicitó habilitar a " + nombre + " " + apellido + " como asesor"
                                    + (inmobiliariaNombre != null && !inmobiliariaNombre.isEmpty()
                                        ? " de " + inmobiliariaNombre : ""),
                            Log.TIPO_SOLICITUD,
                            LogHelper.ROL_ADMIN);
                    Toast.makeText(this,
                            "Solicitud enviada. Esperando aprobación del superadmin.",
                            Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnCrear.setEnabled(true);
                    btnCrear.setText("Enviar solicitud");
                    Toast.makeText(this,
                            "Error al enviar solicitud. Intenta de nuevo.",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private String texto(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
