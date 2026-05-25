package com.example.inmia.cliente;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;

public class ClientePagoExitosoActivity extends AppCompatActivity {
    private MaterialButton btnVolverInicio;
    private MaterialButton btnDescargarComprobante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_pago_exitoso_cliente);
        btnVolverInicio = findViewById(R.id.btnVolverInicio);
        btnDescargarComprobante = findViewById(R.id.btnDescargarComprobante);

        if (btnVolverInicio != null) {
            btnVolverInicio.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClienteHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        if (btnDescargarComprobante != null) {
            btnDescargarComprobante.setOnClickListener(v -> {
                descargarComprobante();
            });
        }
    }

    private void descargarComprobante() {
        String urlPdf = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf";
        String nombreArchivo = "Comprobante_Pago_INMIA.pdf";

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlPdf));
        request.setTitle("Comprobante de Pago");
        request.setDescription("Guardando tu recibo en PDF");

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nombreArchivo);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            manager.enqueue(request);
            Toast.makeText(this, "Descarga iniciada", Toast.LENGTH_SHORT).show();
        }
    }


}