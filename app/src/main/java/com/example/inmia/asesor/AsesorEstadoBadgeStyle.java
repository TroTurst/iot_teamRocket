package com.example.inmia.asesor;

import android.content.Context;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.inmia.R;

public final class AsesorEstadoBadgeStyle {

    private AsesorEstadoBadgeStyle() {
    }

    public static void apply(Context context, TextView badge, String status) {
        if (context == null || badge == null) {
            return;
        }

        String normalized = status == null ? "" : status.trim().toLowerCase();
        int backgroundRes;
        int textColorRes;

        if (normalized.contains("cancel")) {
            backgroundRes = R.drawable.bg_badge_red_circle;
            textColorRes = R.color.inmia_white;
        } else if (normalized.contains("confirm") || normalized.contains("aprobad")) {
            backgroundRes = R.drawable.bg_badge_green;
            textColorRes = R.color.inmia_white;
        } else if (normalized.contains("pend") || normalized.contains("aprobar")) {
            backgroundRes = R.drawable.bg_badge_orange;
            textColorRes = R.color.badge_orange_text;
        } else if (normalized.contains("termin")) {
            backgroundRes = R.drawable.badge_outline_inactive;
            textColorRes = R.color.inmia_neutral;
        } else {
            backgroundRes = R.drawable.badge_outline;
            textColorRes = R.color.inmia_teal_dark;
        }

        badge.setBackgroundResource(backgroundRes);
        badge.setTextColor(ContextCompat.getColor(context, textColorRes));
    }
}