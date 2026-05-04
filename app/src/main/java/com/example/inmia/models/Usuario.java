package com.example.inmia.models;

public class Usuario {

    private String nombre;
    private String empresa;
    private String iniciales;
    private boolean activo;
    private String rol;
    private String tiempoRegistro; // ← campo nuevo

    // Constructor completo — para gestión de usuarios
    public Usuario(String nombre, String empresa,
                   String iniciales, boolean activo, String rol) {
        this.nombre         = nombre;
        this.empresa        = empresa;
        this.iniciales      = iniciales;
        this.activo         = activo;
        this.rol            = rol;
        this.tiempoRegistro = "";
    }

    // Constructor con tiempoRegistro — para nuevos usuarios home
    public Usuario(String nombre, String empresa,
                   String iniciales, boolean activo,
                   String rol, String tiempoRegistro) {
        this.nombre         = nombre;
        this.empresa        = empresa;
        this.iniciales      = iniciales;
        this.activo         = activo;
        this.rol            = rol;
        this.tiempoRegistro = tiempoRegistro;
    }

    // Getters
    public String getNombre()         { return nombre; }
    public String getEmpresa()        { return empresa; }
    public String getIniciales()      { return iniciales; }
    public boolean isActivo()         { return activo; }
    public String getRol()            { return rol; }
    public String getTiempoRegistro() { return tiempoRegistro; }

    // Setters
    public void setActivo(boolean activo)             { this.activo = activo; }
    public void setTiempoRegistro(String tiempo)      { this.tiempoRegistro = tiempo; }
}