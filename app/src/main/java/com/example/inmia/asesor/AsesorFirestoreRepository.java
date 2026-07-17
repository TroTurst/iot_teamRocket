package com.example.inmia.asesor;

import android.net.Uri;

import com.example.inmia.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class AsesorFirestoreRepository {

    private static AsesorFirestoreRepository instance;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    private AsesorFirestoreRepository() {}

    public static AsesorFirestoreRepository get() {
        if (instance == null) instance = new AsesorFirestoreRepository();
        return instance;
    }

    public String getUid() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
    }

    // ── INTERFACES ────────────────────────────────────────────────────────
    public interface CitasCallback       { void onLoaded(List<CitaItem> items); }
    public interface HomeCitasCallback   { void onLoaded(List<HomeCita> items); }
    public interface SeparacionesCallback{ void onLoaded(List<SeparacionItem> items); }
    public interface ChatsCallback       { void onLoaded(List<ChatThread> threads); }
    public interface MessagesCallback    { void onLoaded(List<ChatMessage> messages); }
    public interface PerfilCallback      { void onLoaded(Map<String, Object> data); }
    public interface IntCallback         { void onResult(int count); }
    public interface NombresCallback     { void onLoaded(Map<String, String> nombrePorId); }
    public interface ChatDetailsCallback { void onLoaded(String phone, boolean favorite); }
    public interface OperationCallback   { void onComplete(boolean success, String message); }
    public interface ChatReadyCallback   { void onReady(boolean success, String chatId, String message); }

    // Resuelve una lista de clienteIds a nombres reales desde usuarios/{uid}.nombres
    private void resolveNombresClientes(List<String> clienteIds, NombresCallback callback) {
        List<String> unique = new ArrayList<>();
        for (String id : clienteIds) {
            if (id != null && !id.isEmpty() && !id.equals("—") && !unique.contains(id)) unique.add(id);
        }
        if (unique.isEmpty()) { callback.onLoaded(new HashMap<>()); return; }
        Map<String, String> nombres = new HashMap<>();
        int[] remaining = {unique.size()};
        for (String uid : unique) {
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(doc -> {
                    String nombre = doc.getString("nombres");
                    if (nombre != null && !nombre.trim().isEmpty()) nombres.put(uid, nombre.trim());
                    if (--remaining[0] == 0) callback.onLoaded(nombres);
                })
                .addOnFailureListener(e -> { if (--remaining[0] == 0) callback.onLoaded(nombres); });
        }
    }

    private String resolveNombre(String uid, Map<String, String> map) {
        if (uid == null || uid.isEmpty()) return "—";
        return (map != null && map.containsKey(uid)) ? map.get(uid) : safe(uid);
    }

    // ── CITAS ─────────────────────────────────────────────────────────────

    public void getCitas(CitasCallback callback) {
        db.collection("citas")
            .whereEqualTo("asesorId", getUid())
            .get()
            .addOnSuccessListener(snapshots -> {
                List<QueryDocumentSnapshot> docs = new ArrayList<>();
                List<String> clienteIds = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshots) {
                    docs.add(doc);
                    String cid = doc.getString("clienteId");
                    if (cid != null && !cid.isEmpty()) clienteIds.add(cid);
                }
                resolveNombresClientes(clienteIds, nombresMap -> {
                    List<CitaItem> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : docs) items.add(citaItemFromDoc(doc, nombresMap));
                    callback.onLoaded(items);
                });
            })
            .addOnFailureListener(e -> callback.onLoaded(new ArrayList<>()));
    }

    public void getCitasHoy(HomeCitasCallback callback) {
        String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        db.collection("citas")
            .whereEqualTo("asesorId", getUid())
            .get()
            .addOnSuccessListener(snapshots -> {
                List<QueryDocumentSnapshot> docs = new ArrayList<>();
                List<String> clienteIds = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshots) {
                    Timestamp ts = doc.getTimestamp("fechaHoraInicio");
                    if (ts != null) {
                        String docDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(ts.toDate());
                        if (hoy.equals(docDate)) {
                            docs.add(doc);
                            String cid = doc.getString("clienteId");
                            if (cid != null && !cid.isEmpty()) clienteIds.add(cid);
                        }
                    }
                }
                resolveNombresClientes(clienteIds, nombresMap -> {
                    List<HomeCita> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : docs) items.add(homeCitaFromDoc(doc, nombresMap));
                    callback.onLoaded(items);
                });
            })
            .addOnFailureListener(e -> callback.onLoaded(new ArrayList<>()));
    }

    public void updateCitaEstado(String docId, String estado) {
        if (docId == null || docId.isEmpty()) return;
        Map<String, Object> upd = new HashMap<>();
        upd.put("estado", estado);
        db.collection("citas").document(docId).update(upd);
    }

    private CitaItem citaItemFromDoc(QueryDocumentSnapshot doc, Map<String, String> nombresMap) {
        String estado          = doc.getString("estado");
        String nombreProyecto  = doc.getString("nombreProyecto");
        String clienteId       = doc.getString("clienteId");
        String tipologia       = doc.getString("tipologiaSeleccionada");
        String inmobiliariaId  = doc.getString("inmobiliariaId");
        Timestamp tsInicio     = doc.getTimestamp("fechaHoraInicio");

        String fechaHora = tsInicio != null
            ? new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(tsInicio.toDate())
            : "—";

        return new CitaItem(
            capitalizarEstado(estado),
            colorForEstado(estado),
            alphaForEstado(estado),
            safe(nombreProyecto),
            resolveNombre(clienteId, nombresMap),
            safe(tipologia),
            fechaHora,
            doc.getId(),
            safe(inmobiliariaId)
        );
    }

    private HomeCita homeCitaFromDoc(QueryDocumentSnapshot doc, Map<String, String> nombresMap) {
        String estado         = doc.getString("estado");
        String nombreProyecto = doc.getString("nombreProyecto");
        String clienteId      = doc.getString("clienteId");
        Timestamp tsInicio    = doc.getTimestamp("fechaHoraInicio");

        String hora     = "—";
        String meridian = "";
        String fecha    = "";
        if (tsInicio != null) {
            Date d   = tsInicio.toDate();
            hora     = new SimpleDateFormat("hh:mm", Locale.getDefault()).format(d);
            meridian = new SimpleDateFormat("a", Locale.US).format(d);
            fecha    = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(d);
        }
        boolean confirmed = "confirmada".equalsIgnoreCase(estado);
        return new HomeCita(hora, meridian, resolveNombre(clienteId, nombresMap), safe(nombreProyecto),
            capitalizarEstado(estado), confirmed, fecha, doc.getId());
    }

    // ── SEPARACIONES ──────────────────────────────────────────────────────

    public void getSeparaciones(SeparacionesCallback callback) {
        db.collection("separaciones")
            .get()
            .addOnSuccessListener(snapshots -> {
                List<QueryDocumentSnapshot> docs = new ArrayList<>();
                List<String> clienteIds = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshots) {
                    docs.add(doc);
                    String cid = doc.getString("clienteId");
                    if (cid != null && !cid.isEmpty()) clienteIds.add(cid);
                }
                resolveNombresClientes(clienteIds, nombresMap -> {
                    List<SeparacionItem> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : docs) items.add(separacionItemFromDoc(doc, nombresMap));
                    callback.onLoaded(items);
                });
            })
            .addOnFailureListener(e -> callback.onLoaded(new ArrayList<>()));
    }

    public void updateSeparacionEstado(String docId, String estado) {
        if (docId == null || docId.isEmpty()) return;
        Map<String, Object> upd = new HashMap<>();
        upd.put("estado", estado);
        db.collection("separaciones").document(docId).update(upd);
    }

    private SeparacionItem separacionItemFromDoc(QueryDocumentSnapshot doc, Map<String, String> nombresMap) {
        String estado            = doc.getString("estado");
        String nombreProyecto    = doc.getString("nombreProyecto");
        String ubicacion         = doc.getString("ubicacion");
        String inmobiliariaNombre= doc.getString("inmobiliariaNombre");
        String clienteId         = doc.getString("clienteId");
        String tipologia         = doc.getString("tipologia");
        Timestamp ts             = doc.getTimestamp("fechaCreacion");
        Long monto               = doc.getLong("montoSeparacion");

        String estadoDisplay = capitalizarEstado(estado);
        boolean confirmed    = !"Pendiente".equals(estadoDisplay);
        String actionLabel   = confirmed ? "Detalles" : "Confirmar";
        String fechaStr      = ts != null
            ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(ts.toDate()) : "—";
        String montoStr      = monto != null ? "S/ " + monto : "—";

        return new SeparacionItem(
            estadoDisplay, colorForEstado(estado),
            safe(nombreProyecto), safe(ubicacion), safe(inmobiliariaNombre),
            actionLabel, confirmed, doc.getId(),
            resolveNombre(clienteId, nombresMap),
            safe(tipologia), fechaStr, montoStr
        );
    }

    // ── CHATS ─────────────────────────────────────────────────────────────
    // La colección "chats" usa "asesorNombre" para identificar al asesor.
    // Primero obtenemos el nombre del asesor desde "usuarios/{uid}", luego filtramos.

    public interface ListenerReadyCallback { void onReady(ListenerRegistration reg); }

    public void listenChats(ChatsCallback callback, ListenerReadyCallback onReady) {
        db.collection("usuarios").document(getUid()).get()
            .addOnSuccessListener(userDoc -> {
                // El campo en Firestore es "nombres" (no "nombre")
                String nombre = userDoc.getString("nombres");
                if (nombre == null || nombre.trim().isEmpty()) {
                    callback.onLoaded(new ArrayList<>());
                    return;
                }
                ListenerRegistration reg = db.collection("chats")
                    .whereEqualTo("asesorNombre", nombre.trim())
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener((snapshots, e) -> {
                        if (snapshots == null) return;
                        List<ChatThread> threads = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            threads.add(chatThreadFromDoc(doc));
                        }
                        callback.onLoaded(threads);
                    });
                onReady.onReady(reg);
            })
            .addOnFailureListener(e -> callback.onLoaded(new ArrayList<>()));
    }

    private ChatThread chatThreadFromDoc(QueryDocumentSnapshot doc) {
        String nombre     = doc.getString("clienteNombre");
        if (nombre == null || nombre.isEmpty()) nombre = safe(doc.getString("clienteId"));
        String ultimoMsg  = safe(doc.getString("ultimoMensaje"));
        String fotoUrl    = safe(doc.getString("fotoAsesorUrl"));
        Timestamp ts      = doc.getTimestamp("timestamp");
        String time       = ts != null
            ? new SimpleDateFormat("HH:mm", Locale.getDefault()).format(ts.toDate())
            : "";
        return new ChatThread(doc.getId(), nombre, ultimoMsg, time, R.drawable.ic_perfil, fotoUrl);
    }

    // ── MENSAJES ──────────────────────────────────────────────────────────

    public ListenerRegistration listenMessages(String chatId, MessagesCallback callback) {
        return db.collection("chats").document(chatId)
            .collection("mensajes")
            .orderBy("timestamp")
            .addSnapshotListener((snapshots, e) -> {
                if (snapshots == null) return;
                List<ChatMessage> messages = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshots) {
                    String texto    = safe(doc.getString("texto"));
                    String senderId = doc.getString("senderId");
                    if (senderId == null || senderId.isEmpty()) senderId = doc.getString("emisorId");
                    boolean outgoing= getUid().equals(senderId);
                    Timestamp ts    = doc.getTimestamp("timestamp");
                    String time     = ts != null
                        ? new SimpleDateFormat("HH:mm", Locale.getDefault()).format(ts.toDate())
                        : "";
                    String attachmentUrl = doc.getString("attachmentUrl");
                    String attachmentType = doc.getString("attachmentType");
                    messages.add(new ChatMessage(texto, time, outgoing, attachmentUrl, attachmentType));
                }
                callback.onLoaded(messages);
            });
    }

    public void sendMessage(String chatId, String texto, OperationCallback callback) {
        if (chatId == null || chatId.isEmpty() || texto == null || texto.trim().isEmpty()) {
            callback.onComplete(false, "No se pudo enviar el mensaje");
            return;
        }
        Map<String, Object> msg = new HashMap<>();
        msg.put("texto", texto.trim());
        msg.put("senderId", getUid());
        msg.put("emisorId", getUid());
        msg.put("timestamp", FieldValue.serverTimestamp());
        db.collection("chats").document(chatId).collection("mensajes").add(msg)
            .addOnSuccessListener(document -> {
                Map<String, Object> chatUpd = new HashMap<>();
                chatUpd.put("ultimoMensaje", texto.trim());
                chatUpd.put("timestamp", FieldValue.serverTimestamp());
                db.collection("chats").document(chatId).update(chatUpd);
                callback.onComplete(true, "Mensaje enviado");
            })
            .addOnFailureListener(e -> callback.onComplete(false, "No se pudo enviar. Revisa tu conexión"));
    }

    public void ensureChat(String currentChatId, String clientName, ChatReadyCallback callback) {
        if (currentChatId != null && !currentChatId.trim().isEmpty()) {
            callback.onReady(true, currentChatId.trim(), "");
            return;
        }
        String advisorId = getUid();
        if (advisorId.isEmpty()) {
            callback.onReady(false, "", "Debes iniciar sesión nuevamente");
            return;
        }
        String cleanName = clientName == null ? "" : clientName.trim();
        if (cleanName.isEmpty() || "Cliente".equalsIgnoreCase(cleanName)) {
            callback.onReady(false, "", "No se pudo identificar al cliente");
            return;
        }
        db.collection("usuarios").document(advisorId).get()
            .addOnSuccessListener(advisor -> {
                String advisorName = advisor.getString("nombres");
                if (advisorName == null || advisorName.trim().isEmpty()) {
                    callback.onReady(false, "", "Tu perfil de asesor no tiene nombre");
                    return;
                }
                String finalAdvisorName = advisorName.trim();
                db.collection("chats").whereEqualTo("asesorNombre", finalAdvisorName).get()
                    .addOnSuccessListener(chats -> {
                        for (QueryDocumentSnapshot chat : chats) {
                            if (cleanName.equalsIgnoreCase(chat.getString("clienteNombre"))) {
                                callback.onReady(true, chat.getId(), "");
                                return;
                            }
                        }
                        findClientAndCreateChat(cleanName, advisorId, finalAdvisorName,
                            safePhoto(advisor), callback);
                    })
                    .addOnFailureListener(e -> callback.onReady(false, "",
                        "No se pudo buscar la conversación"));
            })
            .addOnFailureListener(e -> callback.onReady(false, "", "No se pudo cargar tu perfil"));
    }

    private void findClientAndCreateChat(String clientName, String advisorId, String advisorName,
                                         String advisorPhoto, ChatReadyCallback callback) {
        db.collection("usuarios").whereEqualTo("nombres", clientName).limit(1).get()
            .addOnSuccessListener(users -> {
                if (users.isEmpty()) {
                    callback.onReady(false, "", "No se encontró la cuenta de " + clientName);
                    return;
                }
                DocumentSnapshot client = users.getDocuments().get(0);
                Map<String, Object> chat = new HashMap<>();
                chat.put("clienteId", client.getId());
                chat.put("clienteNombre", clientName);
                chat.put("clienteTelefono", client.getString("telefono"));
                chat.put("asesorId", advisorId);
                chat.put("asesorNombre", advisorName);
                chat.put("fotoAsesorUrl", advisorPhoto);
                chat.put("ultimoMensaje", "");
                chat.put("favoritoAsesor", false);
                chat.put("timestamp", FieldValue.serverTimestamp());
                db.collection("chats").add(chat)
                    .addOnSuccessListener(ref -> callback.onReady(true, ref.getId(), "Conversación creada"))
                    .addOnFailureListener(e -> callback.onReady(false, "",
                        "No se pudo crear la conversación"));
            })
            .addOnFailureListener(e -> callback.onReady(false, "", "No se pudo identificar al cliente"));
    }

    private String safePhoto(DocumentSnapshot user) {
        String photo = user.getString("fotoUrl");
        if (photo == null || photo.isEmpty()) photo = user.getString("fotoPerfilUrl");
        return photo == null ? "" : photo;
    }

    public void getChatDetails(String chatId, ChatDetailsCallback callback) {
        if (chatId == null || chatId.isEmpty()) { callback.onLoaded("", false); return; }
        db.collection("chats").document(chatId).get()
            .addOnSuccessListener(chat -> {
                boolean favorite = Boolean.TRUE.equals(chat.getBoolean("favoritoAsesor"));
                String clienteId = chat.getString("clienteId");
                String phoneInChat = chat.getString("clienteTelefono");
                if (clienteId == null || clienteId.isEmpty()) {
                    callback.onLoaded(phoneInChat == null ? "" : phoneInChat, favorite);
                    return;
                }
                db.collection("usuarios").document(clienteId).get()
                    .addOnSuccessListener(user -> {
                        String phone = user.getString("telefono");
                        callback.onLoaded(phone == null ? "" : phone, favorite);
                    })
                    .addOnFailureListener(e -> callback.onLoaded(phoneInChat == null ? "" : phoneInChat, favorite));
            })
            .addOnFailureListener(e -> callback.onLoaded("", false));
    }

    public void setChatFavorite(String chatId, boolean favorite, OperationCallback callback) {
        if (chatId == null || chatId.isEmpty()) { callback.onComplete(false, "Chat no disponible"); return; }
        db.collection("chats").document(chatId).update("favoritoAsesor", favorite)
            .addOnSuccessListener(unused -> callback.onComplete(true,
                favorite ? "Chat marcado como favorito" : "Chat quitado de favoritos"))
            .addOnFailureListener(e -> callback.onComplete(false, "No se pudo actualizar el favorito"));
    }

    public void sendAttachment(String chatId, Uri uri, String fileName, String mimeType,
                               OperationCallback callback) {
        if (chatId == null || chatId.isEmpty() || uri == null) {
            callback.onComplete(false, "No se pudo adjuntar el archivo"); return;
        }
        String safeName = (fileName == null || fileName.trim().isEmpty()) ? "archivo" : fileName.trim();
        StorageReference ref = storage.getReference().child("chat_attachments/" + chatId + "/"
            + UUID.randomUUID() + "_" + safeName.replaceAll("[^a-zA-Z0-9._-]", "_"));
        ref.putFile(uri).continueWithTask(task -> {
            if (!task.isSuccessful() && task.getException() != null) throw task.getException();
            return ref.getDownloadUrl();
        }).addOnSuccessListener(downloadUri -> {
            Map<String, Object> msg = new HashMap<>();
            msg.put("texto", "📎 " + safeName);
            msg.put("senderId", getUid());
            msg.put("emisorId", getUid());
            msg.put("timestamp", FieldValue.serverTimestamp());
            msg.put("attachmentUrl", downloadUri.toString());
            msg.put("attachmentName", safeName);
            msg.put("attachmentType", mimeType == null ? "application/octet-stream" : mimeType);
            db.collection("chats").document(chatId).collection("mensajes").add(msg)
                .addOnSuccessListener(doc -> {
                    Map<String, Object> chatUpd = new HashMap<>();
                    chatUpd.put("ultimoMensaje", "📎 " + safeName);
                    chatUpd.put("timestamp", FieldValue.serverTimestamp());
                    db.collection("chats").document(chatId).update(chatUpd);
                    callback.onComplete(true, "Archivo enviado");
                }).addOnFailureListener(e -> callback.onComplete(false, "No se pudo enviar el archivo"));
        }).addOnFailureListener(e -> callback.onComplete(false, "No se pudo subir el archivo"));
    }

    // ── PERFIL ────────────────────────────────────────────────────────────

    public void getPerfil(PerfilCallback callback) {
        db.collection("usuarios").document(getUid()).get()
            .addOnSuccessListener(doc -> {
                if (doc.exists() && doc.getData() != null) {
                    callback.onLoaded(doc.getData());
                } else {
                    callback.onLoaded(new HashMap<>());
                }
            })
            .addOnFailureListener(e -> callback.onLoaded(new HashMap<>()));
    }

    // ── INMOBILIARIA ──────────────────────────────────────────────────────

    public void getInmobiliaria(String inmobiliariaId, PerfilCallback callback) {
        if (inmobiliariaId == null || inmobiliariaId.isEmpty()) {
            callback.onLoaded(new HashMap<>());
            return;
        }
        db.collection("inmobiliarias").document(inmobiliariaId).get()
            .addOnSuccessListener(doc -> {
                if (doc.exists() && doc.getData() != null) {
                    callback.onLoaded(doc.getData());
                } else {
                    callback.onLoaded(new HashMap<>());
                }
            })
            .addOnFailureListener(e -> callback.onLoaded(new HashMap<>()));
    }

    // ── HELPERS ───────────────────────────────────────────────────────────

    private int colorForEstado(String estado) {
        if (estado == null) return R.color.inmia_warning;
        switch (estado.toLowerCase(Locale.US)) {
            case "confirmada": case "aprobada": return R.color.inmia_success;
            case "cancelada":                   return R.color.inmia_danger;
            case "terminada":                   return R.color.inmia_neutral;
            default:                            return R.color.inmia_warning;
        }
    }

    private float alphaForEstado(String estado) {
        if ("cancelada".equalsIgnoreCase(estado)) return 0.8f;
        if ("terminada".equalsIgnoreCase(estado)) return 0.7f;
        return 1f;
    }

    private String capitalizarEstado(String s) {
        if (s == null || s.isEmpty()) return "—";
        String lower = s.toLowerCase(Locale.US);
        switch (lower) {
            case "pendiente": case "en proceso": return "Pendiente";
            case "aprobada":  case "aprobado":   case "confirmada": return "Aprobado";
            case "cancelada": case "cancelado":  return "Cancelado";
            case "terminada": case "terminado":  return "Terminado";
            default: return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
        }
    }

    private String safe(String s) {
        return (s != null && !s.isEmpty()) ? s : "—";
    }
}
