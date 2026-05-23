package com.example.inmia.superadmin.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AdminDao {

    @Insert
    void insertar(AdminEntity admin);

    @Query("SELECT * FROM admins ORDER BY fechaCreacion DESC")
    List<AdminEntity> obtenerTodos();
}
