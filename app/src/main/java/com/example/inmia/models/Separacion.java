package com.example.inmia.models;

public class Separacion {
    private String id;
    private String estado;
    private String nombre;
    private String ubicacion;
    private String empresa;
    private int imagenResId;
    private String imagenUrl;

    public Separacion(String estado, String nombre, String ubicacion, String empresa, String imagenUrl) {
        this.estado = estado;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.empresa = empresa;
        this.imagenUrl = imagenUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEstado() { return estado; }
    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }
    public String getEmpresa() { return empresa; }
    public String getImagenUrl() { return imagenUrl; }
}