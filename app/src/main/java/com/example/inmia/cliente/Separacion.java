package com.example.inmia.cliente;

public class Separacion {
    private String estado;
    private String nombre;
    private String ubicacion;
    private String empresa;
    private int imagenResId;

    public Separacion(String estado, String nombre, String ubicacion, String empresa, int imagenResId) {
        this.estado = estado;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.empresa = empresa;
        this.imagenResId = imagenResId;
    }

    public String getEstado() { return estado; }
    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }
    public String getEmpresa() { return empresa; }
    public int getImagenResId() { return imagenResId; }
}