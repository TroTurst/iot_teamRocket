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
                    new Asesor("ase_001", "Carlos Mendoza", "Senior · Ventas", R.drawable.avatar_asesor_1),
                    new Asesor("ase_002", "Martina Mendoza", "Junior · Ventas", R.drawable.avatar_asesor_2),
                    new Asesor("ase_003", "Lucia Quispe", "Senior · Alquileres", R.drawable.avatar_asesor_3),
                    new Asesor("ase_004", "Diego Flores", "Junior · Alquileres", R.drawable.avatar_asesor_1),
                    new Asesor("ase_005", "Andrea Ruiz", "Senior · Preventa", R.drawable.avatar_asesor_2),
                    new Asesor("ase_006", "Jorge Alvarado", "Senior · Preventa", R.drawable.avatar_asesor_3)
            ));
        }
        return cache;
    }
}

