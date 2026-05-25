package com.example.inmia.superadmin.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notificaciones_sa")
public class NotificacionSAEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String titulo;
    public String descripcion;
    public String tipo;       // "admin_creado", "asesor_habilitado", "asesor_rechazado", "usuario_activado", "usuario_desactivado"
    public long timestamp;
    public boolean leida;
}
