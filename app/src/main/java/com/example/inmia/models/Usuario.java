package com.example.inmia.models;

public class Usuario {

    private String nombre;
    private String empresa;
    private String iniciales;
    private boolean activo;
    private String rol; // "admin", "asesor", "cliente"

    public Usuario(String nombre, String empresa,
                   String iniciales, boolean activo, String rol) {
        this.nombre    = nombre;
        this.empresa   = empresa;
        this.iniciales = iniciales;
        this.activo    = activo;
        this.rol       = rol;
    }

    // Getters
    public String getNombre()    { return nombre; }
    public String getEmpresa()   { return empresa; }
    public String getIniciales() { return iniciales; }
    public boolean isActivo()    { return activo; }
    public String getRol()       { return rol; }

    // Setter — para cambiar estado desde el switch
    public void setActivo(boolean activo) { this.activo = activo; }
}