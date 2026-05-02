package com.example.inmia.models;

public class Asesor {
    private final String id;
    private final String nombre;
    private final String especialidad;
    private final int fotoResId;

    public Asesor(String id, String nombre, String especialidad, int fotoResId) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.fotoResId = fotoResId;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public int getFotoResId() {
        return fotoResId;
    }
}

