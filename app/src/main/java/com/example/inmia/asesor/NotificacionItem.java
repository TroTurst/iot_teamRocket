package com.example.inmia.asesor;

public class NotificacionItem {

    private final String message;
    private final String status;
    private final String time;
    private final int statusColorRes;
    private final int iconResId;
    private final int iconBackgroundColorRes;
    private final int lineColorRes;
    private final float lineAlpha;

    public NotificacionItem(
        String message,
        String status,
        String time,
        int statusColorRes,
        int iconResId,
        int iconBackgroundColorRes,
        int lineColorRes,
        float lineAlpha
    ) {
        this.message = message;
        this.status = status;
        this.time = time;
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
