package com.example.inmia.models;

import com.google.firebase.Timestamp;

public class Notificacion {

    private String id;
    private String usuarioId;
    private String tipo;
    private String titulo;
    private String texto;
    private boolean leido;
    private Timestamp fechaCreacion;
    private String referenciaId;

    public Notificacion() {}

    public Notificacion(String texto, String fechaHora, String tipo) {
        this.texto = texto;
        this.tipo = tipo;
    }

    public String getId() { return id; }
    public String getUsuarioId() { return usuarioId; }
    public String getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public String getTexto() { return texto; }
    public boolean isLeido() { return leido; }
    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public String getReferenciaId() { return referenciaId; }

    public void setId(String id) { this.id = id; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setTexto(String texto) { this.texto = texto; }
    public void setLeido(boolean leido) { this.leido = leido; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public void setReferenciaId(String referenciaId) { this.referenciaId = referenciaId; }

    public String getFechaFormateada() {
        if (fechaCreacion == null) return "";
        java.util.Date date = fechaCreacion.toDate();
        java.util.Date now = new java.util.Date();
        long diffMs = now.getTime() - date.getTime();
        long diffMin = diffMs / 60000;
        long diffHrs = diffMin / 60;
        long diffDays = diffHrs / 24;

        if (diffMin < 1)   return "Ahora";
        if (diffMin < 60)  return "Hace " + diffMin + " min";
        if (diffHrs < 24)  return "Hace " + diffHrs + "h";
        if (diffDays == 1) return "Ayer";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        return sdf.format(date);
    }
}