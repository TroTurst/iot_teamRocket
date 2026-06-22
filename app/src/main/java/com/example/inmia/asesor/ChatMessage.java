package com.example.inmia.asesor;

public class ChatMessage {

    private final String message;
    private final String time;
    private final boolean outgoing;
    private final String attachmentUrl;
    private final String attachmentType;

    public ChatMessage(String message, String time, boolean outgoing) {
        this(message, time, outgoing, "", "");
    }

    public ChatMessage(String message, String time, boolean outgoing, String attachmentUrl) {
        this(message, time, outgoing, attachmentUrl, "");
    }

    public ChatMessage(String message, String time, boolean outgoing,
                       String attachmentUrl, String attachmentType) {
        this.message = message;
        this.time = time;
        this.outgoing = outgoing;
        this.attachmentUrl = attachmentUrl == null ? "" : attachmentUrl;
        this.attachmentType = attachmentType == null ? "" : attachmentType;
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

    public String getAttachmentUrl() { return attachmentUrl; }
    public boolean isImageAttachment() { return attachmentType.startsWith("image/"); }
}
