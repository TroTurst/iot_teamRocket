package com.example.inmia.asesor;

public class ChatThread {

    private final String id;
    private final String name;
    private final String lastMessage;
    private final String time;
    private final int avatarResId;

    public ChatThread(String id, String name, String lastMessage, String time, int avatarResId) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.time = time;
        this.avatarResId = avatarResId;
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
}
