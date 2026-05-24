package com.example.inmia.models;

public class Solicitud {

    private int    roomId;        // -1 = demo hardcodeado, >0 = viene de Room
    private String nombre;
    private String inmobiliaria;
    private String iniciales;
    private String correo;
    private String telefono;
    private String tiempoEspera;
    private String documento;
    private String fechaNac;
    private String domicilio;

    // Constructor original (datos hardcodeados de demo)
    public Solicitud(String nombre, String inmobiliaria,
                     String iniciales, String correo,
                     String telefono, String tiempoEspera,
                     String documento, String fechaNac,
                     String domicilio) {
        this.roomId       = -1;
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

    // Constructor desde Room
    public Solicitud(int roomId, String nombre, String inmobiliaria,
                     String iniciales, String correo,
                     String telefono, String tiempoEspera,
                     String documento, String fechaNac,
                     String domicilio) {
        this.roomId       = roomId;
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
    public int    getRoomId()       { return roomId; }
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