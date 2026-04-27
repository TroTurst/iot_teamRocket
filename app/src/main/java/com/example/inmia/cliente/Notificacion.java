package com.example.inmia.cliente;


public class Notificacion {
    private String texto;
    private String fechaHora;
    private String tipo;

    public Notificacion(String texto, String fechaHora, String tipo) {
        this.texto = texto;
        this.fechaHora = fechaHora;
        this.tipo = tipo;
    }

    public String getTexto() { return texto; }
    public String getFechaHora() { return fechaHora; }
    public String getTipo() { return tipo; }
}