package com.example.inmia.models;

public class Log {

    // Tipos de log
    public static final String TIPO_USUARIO  = "usuario";
    public static final String TIPO_ADMIN    = "admin";
    public static final String TIPO_RESERVA  = "reserva";

    private String descripcion;
    private String fecha;
    private String tipo;

    public Log (String descripcion, String fecha, String tipo) {
        this.descripcion = descripcion;
        this.fecha       = fecha;
        this.tipo        = tipo;
    }

    // Getters
    public String getDescripcion() { return descripcion; }
    public String getFecha()       { return fecha; }
    public String getTipo()        { return tipo; }
}