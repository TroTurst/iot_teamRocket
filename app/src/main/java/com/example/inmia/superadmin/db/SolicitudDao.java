package com.example.inmia.superadmin.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SolicitudDao {

    @Insert
    void insertar(SolicitudEntity solicitud);

    @Query("SELECT * FROM solicitudes WHERE pendiente = 1 ORDER BY timestamp DESC")
    List<SolicitudEntity> obtenerPendientes();

    @Query("UPDATE solicitudes SET pendiente = 0 WHERE id = :id")
    void marcarProcesada(int id);
}
