package com.example.inmia.models;

public class Solicitud {

    private String firestoreId;
    private String nombre;
    private String apellidos;
    private String oficina;
    private String inmobiliariaId;
    private String inmobiliariaNombre;
    private String iniciales;
    private String correo;
    private String telefono;
    private String tiempoEspera;
    private String documento;
    private String tipoDocumento;
    private String numeroDocumento;
    private String fechaNac;
    private String domicilio;
    private String fotoUrl;

    public Solicitud(String firestoreId, String nombre, String apellidos,
                     String oficina, String inmobiliariaId, String inmobiliariaNombre,
                     String correo, String telefono, String tiempoEspera,
                     String tipoDocumento, String numeroDocumento,
                     String fechaNac, String domicilio, String fotoUrl) {
        this.firestoreId       = firestoreId;
        this.nombre            = nombre;
        this.apellidos         = apellidos;
        this.oficina           = oficina;
        this.inmobiliariaId    = inmobiliariaId;
        this.inmobiliariaNombre = inmobiliariaNombre != null ? inmobiliariaNombre : "";
        this.iniciales         = obtenerIniciales(nombre, apellidos);
        this.correo            = correo;
        this.telefono          = telefono;
        this.tiempoEspera      = tiempoEspera;
        this.tipoDocumento     = tipoDocumento;
        this.numeroDocumento   = numeroDocumento;
        this.documento         = tipoDocumento + " · " + numeroDocumento;
        this.fechaNac          = fechaNac;
        this.domicilio         = domicilio;
        this.fotoUrl           = fotoUrl != null ? fotoUrl : "";
    }

    private static String obtenerIniciales(String nombre, String apellidos) {
        String n = (nombre != null && !nombre.isEmpty()) ? String.valueOf(nombre.charAt(0)) : "";
        String a = (apellidos != null && !apellidos.isEmpty()) ? String.valueOf(apellidos.charAt(0)) : "";
        return (n + a).toUpperCase();
    }

    public String getFirestoreId()     { return firestoreId; }
    public String getNombre()          { return nombre; }
    public String getApellidos()       { return apellidos; }
    public String getOficina()         { return oficina; }
    public String getInmobiliariaId()    { return inmobiliariaId; }
    public String getInmobiliariaNombre() { return inmobiliariaNombre; }
    public String getInmobiliaria()      { return inmobiliariaNombre; }
    public String getIniciales()       { return iniciales; }
    public String getCorreo()          { return correo; }
    public String getTelefono()        { return telefono; }
    public String getTiempoEspera()    { return tiempoEspera; }
    public String getDocumento()       { return documento; }
    public String getTipoDocumento()   { return tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public String getFechaNac()        { return fechaNac; }
    public String getDomicilio()       { return domicilio; }
    public String getFotoUrl()         { return fotoUrl; }
}
