package com.example.inmia.superadmin;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DialogHelper {

    public static void mostrarDialogoAccion(
            Context context,
            String titulo,
            String mensaje,
            String textoConfirmar,
            String textoCancelar,
            int colorConfirmarRes,
            int iconoFondoRes,
            Runnable onConfirmar
    ) {
        mostrarDialogoAccion(context, titulo, mensaje, textoConfirmar, textoCancelar,
                colorConfirmarRes, iconoFondoRes, onConfirmar, null);
    }

    public static void mostrarDialogoAccion(
            Context context,
            String titulo,
            String mensaje,
            String textoConfirmar,
            String textoCancelar,
            int colorConfirmarRes,
            int iconoFondoRes,
            Runnable onConfirmar,
            Runnable onCancelar
    ) {
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_confirmar_eliminar_chat, null);

        TextView tvDialogTitle   = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvDialogMessage = dialogView.findViewById(R.id.tvDialogMessage);
        MaterialButton btnCancelar  = dialogView.findViewById(R.id.btnCancelarDialogo);
        MaterialButton btnConfirmar = dialogView.findViewById(R.id.btnEliminarDialogo);
        FrameLayout frameIcon    = dialogView.findViewById(R.id.frameDialogIcon);
        ImageView imgIcon        = dialogView.findViewById(R.id.imgDialogIcon);

        tvDialogTitle.setText(titulo);
        tvDialogMessage.setText(mensaje);
        btnCancelar.setText(textoCancelar);
        btnConfirmar.setText(textoConfirmar);
        btnConfirmar.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(context, colorConfirmarRes)
        ));

        if (frameIcon != null) {
            frameIcon.setBackgroundResource(iconoFondoRes);
        }
        if (imgIcon != null) {
            imgIcon.setImageResource(android.R.drawable.ic_dialog_alert);
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(onCancelar == null)
                .create();

        btnCancelar.setOnClickListener(v -> {
            if (onCancelar != null) {
                onCancelar.run();
            }
            dialog.dismiss();
        });
        btnConfirmar.setOnClickListener(v -> {
            if (onConfirmar != null) {
                onConfirmar.run();
            }
            dialog.dismiss();
        });

        dialog.show();
    }
}
