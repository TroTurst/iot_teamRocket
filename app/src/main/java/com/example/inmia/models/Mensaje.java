package com.example.inmia.models;

public class Mensaje {
    private String texto;
    private String hora;
    private boolean enviadoPorMi;

    public Mensaje(String texto, String hora, boolean enviadoPorMi) {
        this.texto = texto;
        this.hora = hora;
        this.enviadoPorMi = enviadoPorMi;
    }

    public String getTexto() { return texto; }
    public String getHora() { return hora; }
    public boolean isEnviadoPorMi() { return enviadoPorMi; }
}