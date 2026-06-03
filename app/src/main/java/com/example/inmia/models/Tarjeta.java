package com.example.inmia.models;

public class Tarjeta {
    private String id;
    private String nombreTitular;
    private String ultimos4Digitos;
    private String fechaExpiracion;
    private String marca;
    private double saldo;
    private boolean predeterminada;

    public Tarjeta(String id, String nombreTitular, String ultimos4Digitos, String fechaExpiracion, String marca, double saldo, boolean predeterminada) {
        this.id = id;
        this.nombreTitular = nombreTitular;
        this.ultimos4Digitos = ultimos4Digitos;
        this.fechaExpiracion = fechaExpiracion;
        this.marca = marca;
        this.saldo = saldo;
        this.predeterminada = predeterminada;
    }

    public String getId() { return id; }
    public String getNombreTitular() { return nombreTitular; }
    public String getUltimos4Digitos() { return ultimos4Digitos; }
    public String getFechaExpiracion() { return fechaExpiracion; }
    public String getMarca() { return marca; }
    public double getSaldo() { return saldo; }
    public boolean isPredeterminada() { return predeterminada; }
}