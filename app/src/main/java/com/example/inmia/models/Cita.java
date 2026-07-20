package com.example.inmia.models;

public class Cita {
    private String id;
    private String estado;
    private String nombre;
    private String ubicacion;
    private String empresa;



    public Cita() {}
    public Cita(String estado, String nombre, String ubicacion, String empresa) {
        this.estado = estado;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.empresa = empresa;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEstado() { return estado; }
    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }
    public String getEmpresa() { return empresa; }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }
}