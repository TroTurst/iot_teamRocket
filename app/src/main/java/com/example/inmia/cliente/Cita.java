package com.example.inmia.cliente;

public class Cita {
    private String estado;
    private String nombre;
    private String ubicacion;
    private String empresa;

    public Cita(String estado, String nombre, String ubicacion, String empresa) {
        this.estado = estado;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.empresa = empresa;
    }

    public String getEstado() { return estado; }
    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }
    public String getEmpresa() { return empresa; }
}