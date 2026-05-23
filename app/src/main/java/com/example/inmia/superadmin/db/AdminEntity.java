package com.example.inmia.superadmin.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "admins")
public class AdminEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombres;
    public String apellidos;
    public String tipoDocumento;
    public String numDocumento;
    public String fechaNacimiento;
    public String correo;
    public String telefono;
    public String domicilio;
    public long fechaCreacion;
}
