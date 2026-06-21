package com.example.inmia.admin.data;

import com.example.inmia.R;
import com.example.inmia.models.Asesor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class AdminAsesorRepositoryMock {

    private static List<Asesor> cache;

    private AdminAsesorRepositoryMock() {
    }

    public static List<Asesor> getAsesores() {
        if (cache == null) {
            cache = Collections.unmodifiableList(Arrays.asList(
                    new Asesor("ase_001", "Carlos Mendoza", "Miraflores",
                            "Asesor inmobiliario", "carlosmendoza@casa.com", "+51 987 654 321",
                            "12345678", "Activo", "San Juan de Lurigancho",
                            6, 26, 650000, 2, 23, 540000, R.drawable.avatar_asesor_1),
                    new Asesor("ase_002", "Martina Mendoza", "San Isidro",
                            "Asesora inmobiliaria", "martinamendoza@casa.com", "+51 986 222 111",
                            "87654321", "Activo", "San Isidro",
                            5, 22, 520000, 3, 18, 430000, R.drawable.avatar_asesor_2),
                    new Asesor("ase_003", "Lucia Quispe", "Surco",
                            "Asesora inmobiliaria", "luciaquispe@casa.com", "+51 987 777 222",
                            "45671234", "Activo", "Surco",
                            4, 18, 410000, 1, 14, 280000, R.drawable.avatar_asesor_3),
                    new Asesor("ase_004", "Diego Flores", "Barranco",
                            "Asesor inmobiliario", "diegoflores@casa.com", "+51 985 444 333",
                            "23456789", "Activo", "Barranco",
                            3, 15, 360000, 1, 9, 190000, R.drawable.avatar_asesor_1),
                    new Asesor("ase_005", "Andrea Ruiz", "La Molina",
                            "Asesora inmobiliaria", "andrearuiz@casa.com", "+51 984 555 444",
                            "67890123", "Activo", "La Molina",
                            7, 30, 720000, 4, 21, 610000, R.drawable.avatar_asesor_2),
                    new Asesor("ase_006", "Jorge Alvarado", "San Borja",
                            "Asesor inmobiliario", "jorgealvarado@casa.com", "+51 983 666 555",
                            "34567890", "Activo", "San Borja",
                            5, 20, 500000, 2, 16, 390000, R.drawable.avatar_asesor_3)
            ));
        }
        return cache;
    }

    public static Asesor getAsesorById(String id) {
        if (id == null) {
            return null;
        }
        for (Asesor asesor : getAsesores()) {
            if (id.equals(asesor.getId())) {
                return asesor;
            }
        }
        return null;
    }
}
