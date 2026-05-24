package com.example.inmia.superadmin.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "solicitudes")
public class SolicitudEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombre;
    public String apellidos;
    public String inmobiliaria;
    public String correo;
    public String telefono;
    public String documento;
    public String fechaNac;
    public String domicilio;
    public long timestamp;
    public boolean pendiente;
}
