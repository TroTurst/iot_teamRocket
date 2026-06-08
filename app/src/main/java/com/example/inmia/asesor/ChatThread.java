package com.example.inmia.asesor;

public class ChatThread {

    private final String id;
    private final String name;
    private final String lastMessage;
    private final String time;
    private final int avatarResId;
    private String fotoUrl;

    public ChatThread(String id, String name, String lastMessage, String time, int avatarResId, String fotoUrl) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.time = time;
        this.avatarResId = avatarResId;
        this.fotoUrl =fotoUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getTime() {
        return time;
    }

    public int getAvatarResId() {
        return avatarResId;
    }
    public String getFotoUrl() { return fotoUrl; }
}
