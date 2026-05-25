package com.example.inmia.asesor;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.inmia.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class AsesorChatStore {

    private static final String PREFS_NAME = "inmia_asesor_chat_store";
    private static final String KEY_THREADS = "chat_threads";
    private static final String KEY_MESSAGES_PREFIX = "chat_messages_";

    private AsesorChatStore() {
    }

    public static void seedIfEmpty(Context context) {
        SharedPreferences prefs = prefs(context);
        if (!getThreads(context).isEmpty()) {
            return;
        }

        List<ChatThread> threads = new ArrayList<>();
        threads.add(new ChatThread("chat_1", "Maria R.", "Hola, quiero info del proyecto Catalina Sky", "10:02", R.drawable.ic_perfil));
        threads.add(new ChatThread("chat_2", "Carlos M.", "Gracias, tambien quiero agendar visita", "10:04", R.drawable.ic_perfil));
        threads.add(new ChatThread("chat_3", "Luisa T.", "Me puedes enviar el brochure del proyecto", "09:45", R.drawable.ic_perfil));
        threads.add(new ChatThread("chat_4", "Javier P.", "Estoy interesado en separar un departamento", "Ayer", R.drawable.ic_perfil));
        threads.add(new ChatThread("chat_5", "Ana L.", "Podemos ver opciones de financiamiento", "Ayer", R.drawable.ic_perfil));
        saveThreads(context, threads);

        saveMessages(context, "chat_1", buildMessages(
            new ChatMessage("Hola, quiero info del proyecto Catalina Sky", "10:02", false),
            new ChatMessage("Hola, claro. Te envio las opciones disponibles", "10:03", true),
            new ChatMessage("Gracias, tambien quiero agendar visita", "10:04", false)
        ));
        saveMessages(context, "chat_2", buildMessages(
            new ChatMessage("Gracias, tambien quiero agendar visita", "10:04", false),
            new ChatMessage("Perfecto, te propongo dos horarios disponibles", "10:05", true)
        ));
        saveMessages(context, "chat_3", buildMessages(
            new ChatMessage("Me puedes enviar el brochure del proyecto", "09:45", false),
            new ChatMessage("Claro, te lo envio ahora mismo", "09:46", true)
        ));
        saveMessages(context, "chat_4", buildMessages(
            new ChatMessage("Estoy interesado en separar un departamento", "Ayer", false),
            new ChatMessage("Excelente, revisemos los pasos", "Ayer", true)
        ));
        saveMessages(context, "chat_5", buildMessages(
            new ChatMessage("Podemos ver opciones de financiamiento", "Ayer", false),
            new ChatMessage("Si, te comparto las alternativas", "Ayer", true)
        ));
    }

    public static List<ChatThread> getThreads(Context context) {
        List<ChatThread> threads = new ArrayList<>();
        String raw = prefs(context).getString(KEY_THREADS, "[]");
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                threads.add(new ChatThread(
                    json.optString("id"),
                    json.optString("name"),
                    json.optString("lastMessage"),
                    json.optString("time"),
                    json.optInt("avatarResId", R.drawable.ic_perfil)
                ));
            }
        } catch (JSONException ignored) {
        }
        return threads;
    }

    public static ChatThread getThreadById(Context context, String id) {
        if (id == null) {
            return null;
        }
        for (ChatThread thread : getThreads(context)) {
            if (id.equals(thread.getId())) {
                return thread;
            }
        }
        return null;
    }

    public static List<ChatMessage> getMessages(Context context, String threadId) {
        List<ChatMessage> messages = new ArrayList<>();
        String raw = prefs(context).getString(KEY_MESSAGES_PREFIX + threadId, "[]");
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                messages.add(new ChatMessage(
                    json.optString("message"),
                    json.optString("time"),
                    json.optBoolean("outgoing", false)
                ));
            }
        } catch (JSONException ignored) {
        }
        return messages;
    }

    public static void sendMessage(Context context, String threadId, String text) {
        if (threadId == null || text == null || text.trim().isEmpty()) {
            return;
        }

        List<ChatMessage> messages = getMessages(context, threadId);
        String now = timeFormat().format(System.currentTimeMillis());
        messages.add(new ChatMessage(text.trim(), now, true));

        String replyText = buildAutoReply(text.trim());
        String replyTime = timeFormat().format(System.currentTimeMillis() + 60000L);
        messages.add(new ChatMessage(replyText, replyTime, false));
        saveMessages(context, threadId, messages);

        List<ChatThread> threads = getThreads(context);
        ArrayList<ChatThread> updated = new ArrayList<>();
        ChatThread matched = null;
        for (ChatThread thread : threads) {
            if (threadId.equals(thread.getId())) {
                matched = new ChatThread(thread.getId(), thread.getName(), replyText, replyTime, thread.getAvatarResId());
                updated.add(matched);
            } else {
                updated.add(thread);
            }
        }
        saveThreads(context, updated);

        ChatThread notifyThread = matched != null ? matched : getThreadById(context, threadId);
        String name = notifyThread != null ? notifyThread.getName() : "Chat";
        AsesorNotificacionHelper.enviar(
            context,
            "Nuevo mensaje de " + name,
            replyText,
            AsesorNotificacionStore.TIPO_CHAT_RESPUESTA,
            AsesorNotificacionStore.TARGET_CHAT_DETAIL,
            threadId
        );
    }

    public static void deleteThread(Context context, String threadId) {
        List<ChatThread> threads = getThreads(context);
        ArrayList<ChatThread> updated = new ArrayList<>();
        for (ChatThread thread : threads) {
            if (!threadId.equals(thread.getId())) {
                updated.add(thread);
            }
        }
        saveThreads(context, updated);
        prefs(context).edit().remove(KEY_MESSAGES_PREFIX + threadId).apply();
    }

    private static List<ChatMessage> buildMessages(ChatMessage... messages) {
        List<ChatMessage> items = new ArrayList<>();
        for (ChatMessage message : messages) {
            items.add(message);
        }
        return items;
    }

    private static String buildAutoReply(String text) {
        String lower = text.toLowerCase(Locale.US);
        if (lower.contains("brochure")) {
            return "Claro, te envio el brochure en este chat.";
        }
        if (lower.contains("visita")) {
            return "Perfecto, te propongo dos horarios disponibles.";
        }
        if (lower.contains("separar")) {
            return "Excelente, te explico el proceso de separacion.";
        }
        if (lower.contains("financiamiento")) {
            return "Si, te comparto las alternativas de financiamiento.";
        }
        return "Gracias, lo reviso y te respondo en breve.";
    }

    private static void saveThreads(Context context, List<ChatThread> threads) {
        JSONArray array = new JSONArray();
        for (ChatThread thread : threads) {
            JSONObject json = new JSONObject();
            try {
                json.put("id", thread.getId());
                json.put("name", thread.getName());
                json.put("lastMessage", thread.getLastMessage());
                json.put("time", thread.getTime());
                json.put("avatarResId", thread.getAvatarResId());
            } catch (JSONException ignored) {
            }
            array.put(json);
        }
        prefs(context).edit().putString(KEY_THREADS, array.toString()).apply();
    }

    private static void saveMessages(Context context, String threadId, List<ChatMessage> messages) {
        JSONArray array = new JSONArray();
        for (ChatMessage message : messages) {
            JSONObject json = new JSONObject();
            try {
                json.put("message", message.getMessage());
                json.put("time", message.getTime());
                json.put("outgoing", message.isOutgoing());
            } catch (JSONException ignored) {
            }
            array.put(json);
        }
        prefs(context).edit().putString(KEY_MESSAGES_PREFIX + threadId, array.toString()).apply();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static SimpleDateFormat timeFormat() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault());
    }
}