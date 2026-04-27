package com.example.inmia.models;

public class Solicitud {

    private String nombre;
    private String inmobiliaria;
    private String iniciales;
    private String correo;
    private String telefono;
    private String tiempoEspera;
    private String documento;
    private String fechaNac;
    private String domicilio;

    public Solicitud(String nombre, String inmobiliaria,
                     String iniciales, String correo,
                     String telefono, String tiempoEspera,
                     String documento, String fechaNac,
                     String domicilio) {
        this.nombre       = nombre;
        this.inmobiliaria = inmobiliaria;
        this.iniciales    = iniciales;
        this.correo       = correo;
        this.telefono     = telefono;
        this.tiempoEspera = tiempoEspera;
        this.documento    = documento;
        this.fechaNac     = fechaNac;
        this.domicilio    = domicilio;
    }

    // Getters
    public String getNombre()       { return nombre; }
    public String getInmobiliaria() { return inmobiliaria; }
    public String getIniciales()    { return iniciales; }
    public String getCorreo()       { return correo; }
    public String getTelefono()     { return telefono; }
    public String getTiempoEspera() { return tiempoEspera; }
    public String getDocumento()    { return documento; }
    public String getFechaNac()     { return fechaNac; }
    public String getDomicilio()    { return domicilio; }
}