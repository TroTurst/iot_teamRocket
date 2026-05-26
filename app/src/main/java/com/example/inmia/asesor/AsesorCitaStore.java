package com.example.inmia.asesor;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class AsesorCitaStore {

    private static final String PREFS_NAME = "inmia_asesor_cita_store";
    private static final String KEY_CITAS = "citas";

    private AsesorCitaStore() {
    }

    public static void seedIfEmpty(Context context) {
        List<CitaRecord> records = getRecords(context);
        boolean changed = false;

        String today = todayIso();
        String yesterday = offsetIso(-1);
        String tomorrow = offsetIso(1);

        changed |= ensureRecord(records, new CitaRecord("juan_perez_edificio_catalina_sky", "Juan Perez", "Edificio Catalina Sky", "10:00", "AM", "Pendiente", false, today, "Av. Los Alamos 214", "Catalina Sky Inmobiliaria"));
        changed |= ensureRecord(records, new CitaRecord("sofia_martinez_catalina_sky", "Sofia Martinez", "Edificio Catalina Sky", "12:30", "PM", "Pendiente", false, today, "Av. Los Alamos 214", "Catalina Sky Inmobiliaria"));
        changed |= ensureRecord(records, new CitaRecord("roberto_chavez_san_isidro", "Roberto Chavez", "San Isidro Prime", "05:15", "PM", "Pendiente", false, tomorrow, "Calle Rivera 540", "Prime House SAC"));

        changed |= ensureRecord(records, new CitaRecord("maria_garcia_condominio_pueblo_libre", "Maria Garcia", "Condominio Pueblo Libre", "02:00", "PM", "Confirmada", true, today, "Jr. Libertad 120", "Pueblo Libre Homes"));
        changed |= ensureRecord(records, new CitaRecord("ana_torres_miraflores", "Ana Torres", "Miraflores Life", "11:00", "AM", "Confirmada", true, yesterday, "Av. Larco 980", "Miraflores Life SAC"));
        changed |= ensureRecord(records, new CitaRecord("luis_rodriguez_san_isidro", "Luis Rodriguez", "San Isidro Heights", "03:45", "PM", "Confirmada", true, today, "Calle Las Camelias 775", "Heights Group"));

        changed |= ensureRecord(records, new CitaRecord("carlos_ruiz_pueblo_libre", "Carlos Ruiz", "Pueblo Libre Vista", "09:30", "AM", "Terminada", false, yesterday, "Jr. Independencia 300", "Vista Inmobiliaria"));
        changed |= ensureRecord(records, new CitaRecord("elena_pardo_miraflores", "Elena Pardo", "Miraflores Bay", "06:20", "PM", "Terminada", false, today, "Av. La Paz 456", "Bay Homes"));
        changed |= ensureRecord(records, new CitaRecord("diego_flores_san_isidro", "Diego Flores", "San Isidro Center", "07:10", "PM", "Terminada", false, tomorrow, "Calle Los Pinos 88", "Center Inmobiliaria"));

        if (changed) {
            saveRecords(context, records);
        }
    }

    public static List<HomeCita> getItems(Context context) {
        List<HomeCita> items = new ArrayList<>();
        for (CitaRecord record : getRecords(context)) {
            items.add(record.toHomeCita());
        }
        return items;
    }

    public static List<HomeCita> getTodayItems(Context context) {
        List<HomeCita> items = new ArrayList<>();
        String today = todayIso();
        for (CitaRecord record : getRecords(context)) {
            if (today.equals(record.date)) {
                items.add(record.toHomeCita());
            }
        }
        return items;
    }

    public static List<CitaItem> getCitaItems(Context context) {
        List<CitaItem> items = new ArrayList<>();
        for (CitaRecord record : getRecords(context)) {
            items.add(record.toCitaItem());
        }
        return items;
    }

    public static CitaRecord getRecordByKey(Context context, String key) {
        if (key == null) {
            return null;
        }
        for (CitaRecord record : getRecords(context)) {
            if (key.equals(record.key)) {
                return record;
            }
        }
        return null;
    }

    public static String buildKey(String client, String project) {
        return normalize(client) + "_" + normalize(project);
    }

    public static void updateStatus(Context context, String key, String status, boolean confirmed) {
        List<CitaRecord> records = getRecords(context);
        List<CitaRecord> updated = new ArrayList<>();
        for (CitaRecord record : records) {
            if (key != null && key.equals(record.key)) {
                updated.add(new CitaRecord(record.key, record.client, record.project, record.time, record.meridian, status, confirmed, record.date, record.location, record.company));
            } else {
                updated.add(record);
            }
        }
        saveRecords(context, updated);
    }

    private static List<CitaRecord> getRecords(Context context) {
        List<CitaRecord> records = new ArrayList<>();
        String raw = prefs(context).getString(KEY_CITAS, "[]");
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                records.add(new CitaRecord(
                    json.optString("key"),
                    json.optString("client"),
                    json.optString("project"),
                    json.optString("time"),
                    json.optString("meridian"),
                    json.optString("status"),
                    json.optBoolean("confirmed", false),
                    json.optString("date", todayIso()),
                    json.optString("location", defaultLocationForProject(json.optString("project"))),
                    json.optString("company", defaultCompanyForProject(json.optString("project")))
                ));
            }
        } catch (JSONException ignored) {
        }
        return records;
    }

    private static void saveRecords(Context context, List<CitaRecord> records) {
        JSONArray array = new JSONArray();
        for (CitaRecord record : records) {
            JSONObject json = new JSONObject();
            try {
                json.put("key", record.key);
                json.put("client", record.client);
                json.put("project", record.project);
                json.put("time", record.time);
                json.put("meridian", record.meridian);
                json.put("status", record.status);
                json.put("confirmed", record.confirmed);
                json.put("date", record.date);
                json.put("location", record.location);
                json.put("company", record.company);
            } catch (JSONException ignored) {
            }
            array.put(json);
        }
        prefs(context).edit().putString(KEY_CITAS, array.toString()).apply();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase().replace(' ', '_');
    }

    private static String todayIso() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    private static String offsetIso(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.getTime());
    }

    private static String defaultLocationForProject(String project) {
        if (project == null) {
            return "Ubicación pendiente";
        }
        String normalized = project.toLowerCase(Locale.US);
        if (normalized.contains("catalina")) {
            return "Surco, Lima";
        }
        if (normalized.contains("san isidro")) {
            return "San Isidro, Lima";
        }
        if (normalized.contains("miraflores")) {
            return "Miraflores, Lima";
        }
        if (normalized.contains("pueblo libre")) {
            return "Pueblo Libre, Lima";
        }
        return "Ubicación pendiente";
    }

    private static String defaultCompanyForProject(String project) {
        if (project == null) {
            return "Inmobiliaria";
        }
        String normalized = project.toLowerCase(Locale.US);
        if (normalized.contains("catalina")) {
            return "Catalina Sky Inmobiliaria";
        }
        if (normalized.contains("san isidro")) {
            return "Heights Group";
        }
        if (normalized.contains("miraflores")) {
            return "Miraflores Life SAC";
        }
        if (normalized.contains("pueblo libre")) {
            return "Pueblo Libre Homes";
        }
        return "Inmobiliaria";
    }

    public static final class CitaRecord {
        final String key;
        final String client;
        final String project;
        final String time;
        final String meridian;
        final String status;
        final boolean confirmed;
        final String date;
        final String location;
        final String company;

        CitaRecord(String key, String client, String project, String time, String meridian, String status, boolean confirmed, String date, String location, String company) {
            this.key = key;
            this.client = client;
            this.project = project;
            this.time = time;
            this.meridian = meridian;
            this.status = status;
            this.confirmed = confirmed;
            this.date = date == null || date.trim().isEmpty() ? todayIso() : date;
            this.location = location == null || location.trim().isEmpty() ? defaultLocationForProject(project) : location;
            this.company = company == null || company.trim().isEmpty() ? defaultCompanyForProject(project) : company;
        }

        HomeCita toHomeCita() {
            return new HomeCita(time, meridian, client, project, status, confirmed, date);
        }

        CitaItem toCitaItem() {
            int colorRes;
            float alpha;

            if ("Confirmada".equalsIgnoreCase(status)) {
                colorRes = com.example.inmia.R.color.inmia_success;
                alpha = 1f;
            } else if ("Terminada".equalsIgnoreCase(status)) {
                colorRes = com.example.inmia.R.color.inmia_neutral;
                alpha = 0.7f;
            } else if ("Cancelada".equalsIgnoreCase(status)) {
                colorRes = com.example.inmia.R.color.inmia_danger;
                alpha = 0.8f;
            } else {
                colorRes = com.example.inmia.R.color.inmia_warning;
                alpha = 1f;
            }

            return new CitaItem(
                status,
                colorRes,
                alpha,
                project,
                client,
                location,
                "Fecha " + displayDate(date) + " - " + time + meridian
            );
        }

        private String displayDate(String isoDate) {
            if (isoDate == null || isoDate.trim().isEmpty()) {
                return "Pendiente";
            }
            return isoDate.replace('-', '/');
        }
    }

    private static boolean ensureRecord(List<CitaRecord> records, CitaRecord candidate) {
        for (int i = 0; i < records.size(); i++) {
            CitaRecord current = records.get(i);
            if (candidate.key.equals(current.key)) {
                return false;
            }
        }
        records.add(candidate);
        return true;
    }
}