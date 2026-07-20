package com.example.inmia.asesor;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.inmia.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class AsesorNotificacionStore {

    public static final String TIPO_CITA_CONFIRMADA = "cita_confirmada";
    public static final String TIPO_CITA_CANCELADA = "cita_cancelada";
    public static final String TIPO_SEPARACION_SOLICITADA = "separacion_solicitada";
    public static final String TIPO_SEPARACION_APROBADA = "separacion_aprobada";
    public static final String TIPO_SEPARACION_CANCELADA = "separacion_cancelada";
    public static final String TIPO_PAGO_RECIBIDO = "pago_recibido";
    public static final String TIPO_CHAT_ELIMINADO = "chat_eliminado";
    public static final String TIPO_CHAT_RESPUESTA = "chat_respuesta";
    public static final String TARGET_CITA_DETAIL = "cita_detail";
    public static final String TARGET_SEPARACION_DETAIL = "separacion_detail";
    public static final String TARGET_CHAT_DETAIL = "chat_detail";

    private static final String PREFS_NAME = "inmia_asesor_local_storage";
    private static final String KEY_BADGE_COUNT = "asesor_badge_count";
    private static final String KEY_NOTIFICATIONS = "asesor_notifications_json";

    private AsesorNotificacionStore() {
    }

    // Ya no genera notificaciones de ejemplo hardcodeadas. Se conserva el método
    // (llamado desde varias pantallas del asesor) como no-op para no romper esos
    // puntos de entrada; la generación real de notificaciones ocurre en
    // AsesorNotificacionSyncer, a partir de datos reales de Firestore.
    public static void seedIfEmpty(Context context) {
        // Intencionalmente vacío.
    }

    public static int getBadgeCount(Context context) {
        return prefs(context).getInt(KEY_BADGE_COUNT, 0);
    }

    public static void clearBadge(Context context) {
        prefs(context).edit().putInt(KEY_BADGE_COUNT, 0).apply();
        markAllAsRead(context);
    }

    public static void addNotification(Context context, String titulo, String descripcion, String tipo) {
        addNotification(context, titulo, descripcion, tipo, null, null);
    }

    public static void addNotification(Context context, String titulo, String descripcion, String tipo, String targetType, String targetId) {
        List<AsesorNotificacionRegistro> notifications = getNotifications(context);
        notifications.add(0, new AsesorNotificacionRegistro(
            titulo,
            descripcion,
            tipo,
            targetType,
            targetId,
            System.currentTimeMillis(),
            false
        ));
        saveNotifications(context, notifications);

        int badgeCount = prefs(context).getInt(KEY_BADGE_COUNT, 0);
        prefs(context).edit().putInt(KEY_BADGE_COUNT, badgeCount + 1).apply();
    }

    public static List<AsesorNotificacionRegistro> getNotifications(Context context) {
        String raw = prefs(context).getString(KEY_NOTIFICATIONS, "[]");
        List<AsesorNotificacionRegistro> notifications = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                notifications.add(new AsesorNotificacionRegistro(
                    json.optString("titulo"),
                    json.optString("descripcion"),
                    json.optString("tipo"),
                    json.optString("targetType", null),
                    json.optString("targetId", null),
                    json.optLong("timestamp"),
                    json.optBoolean("leida", false)
                ));
            }
        } catch (JSONException ignored) {
        }
        return notifications;
    }

    public static List<NotificacionItem> getNotificacionesHoy(Context context) {
        return buildItemsForDay(context, 0);
    }

    public static List<NotificacionItem> getNotificacionesAyer(Context context) {
        return buildItemsForDay(context, 1);
    }

    public static String formatRelativeTime(Context context, long timestamp) {
        Calendar target = Calendar.getInstance();
        target.setTimeInMillis(timestamp);

        Calendar today = Calendar.getInstance();
        if (isSameDay(today, target)) {
            return "Hoy " + timeFormat().format(new Date(timestamp));
        }

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        if (isSameDay(yesterday, target)) {
            return "Ayer " + timeFormat().format(new Date(timestamp));
        }

        return dateFormat().format(new Date(timestamp));
    }

    private static List<NotificacionItem> buildItemsForDay(Context context, int dayOffset) {
        List<NotificacionItem> items = new ArrayList<>();
        Calendar expected = Calendar.getInstance();
        expected.add(Calendar.DAY_OF_YEAR, -dayOffset);

        for (AsesorNotificacionRegistro registro : getNotifications(context)) {
            Calendar recordCalendar = Calendar.getInstance();
            recordCalendar.setTimeInMillis(registro.getTimestamp());
            if (isSameDay(expected, recordCalendar)) {
                items.add(toItem(context, registro));
            }
        }
        return items;
    }

    private static NotificacionItem toItem(Context context, AsesorNotificacionRegistro registro) {
        int statusColorRes;
        int iconResId;
        int iconBackgroundColorRes;
        int lineColorRes;
        float lineAlpha;

        String status = registro.getTitulo();

        switch (registro.getTipo()) {
            case TIPO_CITA_CONFIRMADA:
                statusColorRes = R.color.inmia_info;
                iconResId = R.drawable.ic_citas;
                iconBackgroundColorRes = R.color.inmia_info;
                lineColorRes = R.color.inmia_teal_light;
                lineAlpha = 0.8f;
                break;
            case TIPO_CITA_CANCELADA:
                statusColorRes = R.color.inmia_danger;
                iconResId = R.drawable.ic_citas;
                iconBackgroundColorRes = R.color.inmia_danger;
                lineColorRes = R.color.inmia_line;
                lineAlpha = 1f;
                break;
            case TIPO_SEPARACION_APROBADA:
                statusColorRes = R.color.inmia_success;
                iconResId = R.drawable.ic_separaciones;
                iconBackgroundColorRes = R.color.inmia_success;
                lineColorRes = R.color.inmia_teal_light;
                lineAlpha = 0.8f;
                break;
            case TIPO_SEPARACION_CANCELADA:
                statusColorRes = R.color.inmia_danger;
                iconResId = R.drawable.ic_separaciones;
                iconBackgroundColorRes = R.color.inmia_danger;
                lineColorRes = R.color.inmia_line;
                lineAlpha = 1f;
                break;
            case TIPO_PAGO_RECIBIDO:
                statusColorRes = R.color.inmia_warning;
                iconResId = R.drawable.ic_reportes;
                iconBackgroundColorRes = R.color.inmia_warning;
                lineColorRes = R.color.inmia_line;
                lineAlpha = 1f;
                break;
            case TIPO_CHAT_ELIMINADO:
            case TIPO_CHAT_RESPUESTA:
                statusColorRes = R.color.inmia_info;
                iconResId = R.drawable.ic_chat;
                iconBackgroundColorRes = R.color.inmia_info;
                lineColorRes = R.color.inmia_teal_light;
                lineAlpha = 0.8f;
                break;
            case TIPO_SEPARACION_SOLICITADA:
            default:
                statusColorRes = R.color.inmia_teal_dark;
                iconResId = R.drawable.ic_separaciones;
                iconBackgroundColorRes = R.color.inmia_teal_dark;
                lineColorRes = R.color.inmia_teal_light;
                lineAlpha = 0.8f;
                break;
        }

        return new NotificacionItem(
            registro.getDescripcion(),
            status,
            formatRelativeTime(context, registro.getTimestamp()),
            registro.getTargetType(),
            registro.getTargetId(),
            statusColorRes,
            iconResId,
            iconBackgroundColorRes,
            lineColorRes,
            lineAlpha
        );
    }

    private static void markAllAsRead(Context context) {
        List<AsesorNotificacionRegistro> notifications = getNotifications(context);
        if (notifications.isEmpty()) {
            return;
        }

        List<AsesorNotificacionRegistro> updated = new ArrayList<>();
        for (AsesorNotificacionRegistro registro : notifications) {
            updated.add(new AsesorNotificacionRegistro(
                registro.getTitulo(),
                registro.getDescripcion(),
                registro.getTipo(),
                registro.getTargetType(),
                registro.getTargetId(),
                registro.getTimestamp(),
                true
            ));
        }
        saveNotifications(context, updated);
    }

    private static void saveNotifications(Context context, List<AsesorNotificacionRegistro> notifications) {
        JSONArray array = new JSONArray();
        for (AsesorNotificacionRegistro registro : notifications) {
            JSONObject json = new JSONObject();
            try {
                json.put("titulo", registro.getTitulo());
                json.put("descripcion", registro.getDescripcion());
                json.put("tipo", registro.getTipo());
                json.put("targetType", registro.getTargetType());
                json.put("targetId", registro.getTargetId());
                json.put("timestamp", registro.getTimestamp());
                json.put("leida", registro.isLeida());
            } catch (JSONException ignored) {
            }
            array.put(json);
        }
        prefs(context).edit().putString(KEY_NOTIFICATIONS, array.toString()).apply();
    }

    private static boolean isSameDay(Calendar first, Calendar second) {
        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR)
            && first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR);
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static SimpleDateFormat timeFormat() {
        return new SimpleDateFormat("h:mm a", Locale.getDefault());
    }

    private static SimpleDateFormat dateFormat() {
        return new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault());
    }
}