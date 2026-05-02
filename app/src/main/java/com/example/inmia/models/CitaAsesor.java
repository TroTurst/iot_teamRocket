package com.example.inmia.models;

public class CitaAsesor {
    private final String cliente;
    private final String fecha;
    private final String hora;
    private final String proyecto;
    private final String estado;

    public CitaAsesor(String cliente, String fecha, String hora, String proyecto, String estado) {
        this.cliente = cliente;
        this.fecha = fecha;
        this.hora = hora;
        this.proyecto = proyecto;
        this.estado = estado;
    }

    public String getCliente() {
        return cliente;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getProyecto() {
        return proyecto;
    }

    public String getEstado() {
        return estado;
    }
}

