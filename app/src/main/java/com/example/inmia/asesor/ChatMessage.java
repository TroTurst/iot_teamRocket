package com.example.inmia.asesor;

public class ChatMessage {

    private final String message;
    private final String time;
    private final boolean outgoing;

    public ChatMessage(String message, String time, boolean outgoing) {
        this.message = message;
        this.time = time;
        this.outgoing = outgoing;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public boolean isOutgoing() {
        return outgoing;
    }
}
