package com.example.inmia.superadmin.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotificacionSADao {

    @Insert
    void insertar(NotificacionSAEntity notificacion);

    @Query("SELECT * FROM notificaciones_sa ORDER BY timestamp DESC")
    List<NotificacionSAEntity> obtenerTodas();

    @Query("SELECT COUNT(*) FROM notificaciones_sa WHERE leida = 0")
    int contarNoLeidas();

    @Query("UPDATE notificaciones_sa SET leida = 1")
    void marcarTodasLeidas();
}
