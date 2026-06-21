package com.example.inmia.models;

public class Usuario {

    private String uid;
    private String nombre;
    private String empresa;
    private String iniciales;
    private boolean activo;
    private String rol;
    private String tiempoRegistro;

    // ← NUEVOS campos de perfil
    private String documento;
    private String fechaNacimiento;
    private String correo;
    private String telefono;
    private String domicilio;
    private String fotoUrl = "";

    // Constructor básico — gestión de usuarios sin perfil completo
    public Usuario(String nombre, String empresa,
                   String iniciales, boolean activo, String rol) {
        this.nombre          = nombre;
        this.empresa         = empresa;
        this.iniciales       = iniciales;
        this.activo          = activo;
        this.rol             = rol;
        this.tiempoRegistro  = "";
        this.documento       = "";
        this.fechaNacimiento = "";
        this.correo          = "";
        this.telefono        = "";
        this.domicilio       = "";
    }

    // Constructor con tiempoRegistro — nuevos usuarios home
    public Usuario(String nombre, String empresa,
                   String iniciales, boolean activo,
                   String rol, String tiempoRegistro) {
        this(nombre, empresa, iniciales, activo, rol);
        this.tiempoRegistro = tiempoRegistro;
    }

    // Constructor completo — perfil completo
    public Usuario(String nombre, String empresa,
                   String iniciales, boolean activo, String rol,
                   String documento, String fechaNacimiento,
                   String correo, String telefono, String domicilio) {
        this.nombre          = nombre;
        this.empresa         = empresa;
        this.iniciales       = iniciales;
        this.activo          = activo;
        this.rol             = rol;
        this.tiempoRegistro  = "";
        this.documento       = documento;
        this.fechaNacimiento = fechaNacimiento;
        this.correo          = correo;
        this.telefono        = telefono;
        this.domicilio       = domicilio;
    }

    // Getters
    public String getUid()             { return uid; }
    public String getNombre()          { return nombre; }
    public String getEmpresa()         { return empresa; }
    public String getIniciales()       { return iniciales; }
    public boolean isActivo()          { return activo; }
    public String getRol()             { return rol; }
    public String getTiempoRegistro()  { return tiempoRegistro; }
    public String getDocumento()       { return documento; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getCorreo()          { return correo; }
    public String getTelefono()        { return telefono; }
    public String getDomicilio()       { return domicilio; }
    public String getFotoUrl()         { return fotoUrl != null ? fotoUrl : ""; }

    // Setters
    public void setUid(String uid)                     { this.uid = uid; }
    public void setActivo(boolean activo)              { this.activo = activo; }
    public void setTiempoRegistro(String tiempo)       { this.tiempoRegistro = tiempo; }
    public void setDocumento(String documento)         { this.documento = documento; }
    public void setFechaNacimiento(String fecha)       { this.fechaNacimiento = fecha; }
    public void setCorreo(String correo)               { this.correo = correo; }
    public void setTelefono(String telefono)           { this.telefono = telefono; }
    public void setDomicilio(String domicilio)         { this.domicilio = domicilio; }
    public void setFotoUrl(String fotoUrl)             { this.fotoUrl = fotoUrl; }
}