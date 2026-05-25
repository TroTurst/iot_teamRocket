package com.example.inmia.asesor;

public class AsesorNotificacionRegistro {

    private final String titulo;
    private final String descripcion;
    private final String tipo;
    private final String targetType;
    private final String targetId;
    private final long timestamp;
    private final boolean leida;

    public AsesorNotificacionRegistro(
        String titulo,
        String descripcion,
        String tipo,
        String targetType,
        String targetId,
        long timestamp,
        boolean leida
    ) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.targetType = targetType;
        this.targetId = targetId;
        this.timestamp = timestamp;
        this.leida = leida;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isLeida() {
        return leida;
    }
}