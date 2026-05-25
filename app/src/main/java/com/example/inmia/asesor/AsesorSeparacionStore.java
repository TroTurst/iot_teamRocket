package com.example.inmia.asesor;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class AsesorSeparacionStore {

    private static final String PREFS_NAME = "inmia_asesor_separacion_store";
    private static final String KEY_SEPARACIONES = "separaciones";

    private AsesorSeparacionStore() {
    }

    public static void seedIfEmpty(Context context) {
        List<SeparacionRecord> records = getRecords(context);
        boolean changed = false;

        changed |= ensureRecord(records, new SeparacionRecord("palm_living", "Palm Living", "San Isidro, Lima", "Galeon Inmobiliaria", "Adrian", "Por aprobar", false));
        changed |= ensureRecord(records, new SeparacionRecord("andes_residences", "Andes Residences", "Surco, Lima", "Andes Inmobiliaria", "Maria Paredes", "Por aprobar", false));
        changed |= ensureRecord(records, new SeparacionRecord("sol_living", "Sol Living", "Miraflores, Lima", "Sol Inmobiliaria", "Carlos Medina", "Por aprobar", false));

        changed |= ensureRecord(records, new SeparacionRecord("san_borja_view", "San Borja View", "San Borja, Lima", "Horizonte Inmobiliaria", "Maria Quispe", "Aprobada", true));
        changed |= ensureRecord(records, new SeparacionRecord("catalina_tower", "Catalina Tower", "Lince, Lima", "Catalina Group", "Juan Flores", "Aprobada", true));
        changed |= ensureRecord(records, new SeparacionRecord("urbana_center", "Urbana Center", "Jesús María, Lima", "Urbana SAC", "Laura Castillo", "Aprobada", true));

        changed |= ensureRecord(records, new SeparacionRecord("avista_del_valle", "Avista del Valle", "Pueblo Libre, Lima", "Avista Inmobiliaria", "Pedro Ruiz", "Terminada", true));
        changed |= ensureRecord(records, new SeparacionRecord("oceano_pacifico", "Océano Pacífico", "Barranco, Lima", "Pacific Homes", "Elena Torres", "Terminada", true));
        changed |= ensureRecord(records, new SeparacionRecord("jardines_del_sol", "Jardines del Sol", "La Molina, Lima", "Jardines SAC", "Diego Rojas", "Terminada", true));

        if (changed) {
            saveRecords(context, records);
        }
    }

    public static List<SeparacionItem> getItems(Context context) {
        List<SeparacionItem> items = new ArrayList<>();
        for (SeparacionRecord record : getRecords(context)) {
            items.add(record.toItem());
        }
        return items;
    }

    public static SeparacionRecord getRecordByKey(Context context, String key) {
        if (key == null) {
            return null;
        }
        for (SeparacionRecord record : getRecords(context)) {
            if (key.equals(record.key)) {
                return record;
            }
        }
        return null;
    }

    public static void updateStatus(Context context, String key, String status, boolean confirmed) {
        List<SeparacionRecord> records = getRecords(context);
        List<SeparacionRecord> updated = new ArrayList<>();
        for (SeparacionRecord record : records) {
            if (key != null && key.equals(record.key)) {
                updated.add(new SeparacionRecord(record.key, record.project, record.location, record.company, record.client, status, confirmed));
            } else {
                updated.add(record);
            }
        }
        saveRecords(context, updated);
    }

    public static String buildKey(String project) {
        if (project == null) {
            return "";
        }
        return project.trim().toLowerCase().replace(' ', '_');
    }

    private static List<SeparacionRecord> getRecords(Context context) {
        List<SeparacionRecord> records = new ArrayList<>();
        String raw = prefs(context).getString(KEY_SEPARACIONES, "[]");
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                records.add(new SeparacionRecord(
                    json.optString("key"),
                    json.optString("project"),
                    json.optString("location"),
                    json.optString("company"),
                    json.optString("client"),
                    json.optString("status"),
                    json.optBoolean("confirmed", false)
                ));
            }
        } catch (JSONException ignored) {
        }
        return records;
    }

    private static void saveRecords(Context context, List<SeparacionRecord> records) {
        JSONArray array = new JSONArray();
        for (SeparacionRecord record : records) {
            JSONObject json = new JSONObject();
            try {
                json.put("key", record.key);
                json.put("project", record.project);
                json.put("location", record.location);
                json.put("company", record.company);
                json.put("client", record.client);
                json.put("status", record.status);
                json.put("confirmed", record.confirmed);
            } catch (JSONException ignored) {
            }
            array.put(json);
        }
        prefs(context).edit().putString(KEY_SEPARACIONES, array.toString()).apply();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static final class SeparacionRecord {
        final String key;
        final String project;
        final String location;
        final String company;
        final String client;
        final String status;
        final boolean confirmed;

        SeparacionRecord(String key, String project, String location, String company, String client, String status, boolean confirmed) {
            this.key = key;
            this.project = project;
            this.location = location;
            this.company = company;
            this.client = client;
            this.status = status;
            this.confirmed = confirmed;
        }

        SeparacionItem toItem() {
            int colorRes;
            String actionLabel;

            if ("Cancelada".equalsIgnoreCase(status)) {
                colorRes = com.example.inmia.R.color.inmia_danger;
                actionLabel = "Detalles";
            } else if ("Aprobada".equalsIgnoreCase(status)) {
                colorRes = com.example.inmia.R.color.inmia_info;
                actionLabel = "Detalles";
            } else if ("Terminada".equalsIgnoreCase(status)) {
                colorRes = com.example.inmia.R.color.inmia_neutral;
                actionLabel = "Detalles";
            } else {
                colorRes = com.example.inmia.R.color.inmia_warning;
                actionLabel = "Confirmar";
            }

            String displayStatus = "Por confirmar".equalsIgnoreCase(status) ? "Por aprobar" : status;

            return new SeparacionItem(
                displayStatus,
                colorRes,
                project,
                location,
                company,
                actionLabel,
                confirmed
            );
        }
    }

    private static boolean ensureRecord(List<SeparacionRecord> records, SeparacionRecord candidate) {
        for (SeparacionRecord current : records) {
            if (candidate.key.equals(current.key)) {
                return false;
            }
        }
        records.add(candidate);
        return true;
    }
}