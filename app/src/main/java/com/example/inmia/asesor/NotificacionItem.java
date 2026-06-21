package com.example.inmia.asesor;

public class NotificacionItem {

    private final String message;
    private final String status;
    private final String time;
    private final String targetType;
    private final String targetId;
    private final int statusColorRes;
    private final int iconResId;
    private final int iconBackgroundColorRes;
    private final int lineColorRes;
    private final float lineAlpha;

    public NotificacionItem(
        String message,
        String status,
        String time,
        String targetType,
        String targetId,
        int statusColorRes,
        int iconResId,
        int iconBackgroundColorRes,
        int lineColorRes,
        float lineAlpha
    ) {
        this.message = message;
        this.status = status;
        this.time = time;
        this.targetType = targetType;
        this.targetId = targetId;
        this.statusColorRes = statusColorRes;
        this.iconResId = iconResId;
        this.iconBackgroundColorRes = iconBackgroundColorRes;
        this.lineColorRes = lineColorRes;
        this.lineAlpha = lineAlpha;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public int getStatusColorRes() {
        return statusColorRes;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getIconBackgroundColorRes() {
        return iconBackgroundColorRes;
    }

    public int getLineColorRes() {
        return lineColorRes;
    }

    public float getLineAlpha() {
        return lineAlpha;
    }
}
