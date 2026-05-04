package com.example.inmia.admin.data;

import com.example.inmia.R;
import com.example.inmia.models.Proyecto;
import com.example.inmia.models.Tipologia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Repositorio mock (solo Admin) para compartir los mismos datos entre
 * lista de proyectos y detalle, sin tener que pasar objetos grandes por Intent.
 */
public final class AdminProyectoRepositoryMock {

    private static List<Proyecto> cache;

    private AdminProyectoRepositoryMock() {
    }

    public static List<Proyecto> getProyectos() {
        if (cache == null) {
            cache = new ArrayList<>(crearProyectosMock());
        }
        return cache;
    }

    public static void addProyecto(Proyecto proyecto) {
        if (proyecto == null) return;
        List<Proyecto> proyectos = getProyectos();
        proyectos.add(0, proyecto);
    }

    public static Proyecto getProyectoById(String id) {
        if (id == null) return null;
        for (Proyecto p : getProyectos()) {
            if (id.equals(p.getId())) return p;
        }
        return null;
    }

    private static List<Proyecto> crearProyectosMock() {
        List<Proyecto> proyectos = new ArrayList<>();

        // TIPOLOGÍA 1: 45 m² · 1d
        Tipologia tipologia45 = new Tipologia(
                "tip_45",
                "45 m² · 1d",
                "Departamento compacto y moderno ideal para una persona o una pareja. Cocina integrada tipo kitchenette, zona de home office y vista hacia áreas verdes. Ideal para inversión por alta demanda de alquiler.",
                "45 m²", "1", "1", "Sin estacionamiento", "S/ 420,000", "Disponible",
                R.drawable.onboarding1,
                new int[]{R.drawable.onboarding1, R.drawable.onboarding2, R.drawable.onboarding3, R.drawable.images_2},
                false, "A", false, true, true, true, 2, "porcelanato", false, "natural", true, "estándar"
        );

        // TIPOLOGÍA 2: 65 m² · 2d
        Tipologia tipologia65 = new Tipologia(
                "tip_65",
                "65 m² · 2d",
                "Departamento de dos dormitorios pensado para familias pequeñas. Sala comedor con iluminación natural, closets empotrados, lavandería ventilada y balcón con vista a la ciudad.",
                "65 m²", "2", "2", "1 incluido", "S/ 648,000", "Disponible",
                R.drawable.onboarding2,
                new int[]{R.drawable.onboarding2, R.drawable.onboarding3, R.drawable.onboarding1},
                true, "B", true, true, true, true, 3, "madera laminada", false, "natural", true, "lujo"
        );

        // TIPOLOGÍA 3: 90 m² · 3d
        Tipologia tipologia90 = new Tipologia(
                "tip_90",
                "90 m² · 3d",
                "La tipología más amplia del proyecto con tres dormitorios. Dormitorio principal con baño incorporado, balcón panorámico, cocina semiabierta y opción de family room. Acabados premium y preparación para aire acondicionado.",
                "90 m²", "3", "3", "2 incluidos", "S/ 915,000", "Disponible",
                R.drawable.onboarding3,
                new int[]{R.drawable.onboarding3, R.drawable.images_2, R.drawable.onboarding2, R.drawable.onboarding1},
                true, "A+", true, true, true, true, 4, "porcelanato", true, "forzada", true, "premium"
        );

        // Tipologías extra (para que no todos los proyectos compartan exactamente las mismas)
        Tipologia tipologiaStudio = new Tipologia(
                "tip_studio",
                "Studio 38 m²",
                "Studio funcional con ambiente integrado, clóset de pared a pared y excelente iluminación. Ideal para primeras compras o inversión.",
                "38 m²", "0", "1", "Opcional", "S/ 365,000", "Disponible",
                R.drawable.onboarding1,
                new int[]{R.drawable.onboarding1, R.drawable.images_2},
                false, "B", false, false, false, true, 1, "cerámica", false, "natural", false, "básico"
        );

        Tipologia tipologiaPenthouse = new Tipologia(
                "tip_penthouse",
                "Penthouse 120 m² · 3d",
                "Penthouse con terraza amplia, parrilla, vista al mar y acabados premium. Incluye 2 estacionamientos y depósito.",
                "120 m²", "3", "4", "2 incluidos", "S/ 1,450,000", "Últimas unidades",
                R.drawable.images_2,
                new int[]{R.drawable.images_2, R.drawable.onboarding3, R.drawable.onboarding2},
                true, "A+", true, true, true, true, 6, "madera estructurada", true, "forzada", true, "premium"
        );

        // PROYECTO 1
        proyectos.add(new Proyecto(
                "proy_001",
                "Proyecto Las Nuevas Casas 22a",
                "Santa Catalina, Lima",
                "Proyecto en planos ubicado en zona estratégica con departamentos desde 38 m². Conectividad rápida a vías principales, comercios y centros empresariales. Incluye áreas comunes: coworking, zona de parrillas, gimnasio y piscina.",
                Arrays.asList("Carlos Mendoza", "Ana García"),
                "En planos",
                new int[]{R.drawable.onboarding1, R.drawable.onboarding2, R.drawable.onboarding3},
                R.drawable.onboarding1,
                Arrays.asList(tipologiaStudio, tipologia45, tipologia65, tipologia90),
                "Grupo Inmobiliario INMIA",
                true, "Nuevo proyecto", "Marzo 2026", "REF-001", true,
                Arrays.asList("Coworking", "Piscina", "Gimnasio", "Parrillas", "Área verde", "Lobby doble altura"),
                tipologia45,
                "https://qr.inmia.com/proy001"
        ));

        // PROYECTO 2
        proyectos.add(new Proyecto(
                "proy_002",
                "Condominio Residencial Premium",
                "Jesús María, Lima",
                "Departamentos con servicios premium en ubicación privilegiada (Jesús María). Seguridad 24/7, áreas comunes equipadas, estacionamientos de visita y ascensores de alta capacidad. Entrega estimada Q4 2026.",
                Arrays.asList("Juan López", "María Rodríguez"),
                "En preventa",
                new int[]{R.drawable.onboarding2, R.drawable.onboarding3},
                R.drawable.onboarding2,
                Arrays.asList(tipologia45, tipologia65, tipologia90),
                "Inmobiliaria del Centro",
                true, "6 meses", "Febrero 2026", "REF-002", true,
                Arrays.asList("Piscina", "Gimnasio", "Parrillas", "Sala de niños", "Bike parking"),
                tipologia65,
                "https://qr.inmia.com/proy002"
        ));

        // PROYECTO 3
        proyectos.add(new Proyecto(
                "proy_003",
                "Edificio de Inversión Santa Catalina",
                "Santa Catalina, Lima",
                "Proyecto orientado a inversión con alto potencial de rentabilidad. Unidades compactas, mantenimiento eficiente y administración centralizada. Ideal para renta tradicional o temporal.",
                Arrays.asList("Pedro Flores"),
                "En venta",
                new int[]{R.drawable.onboarding3, R.drawable.images_2},
                R.drawable.onboarding3,
                Arrays.asList(tipologiaStudio, tipologia45, tipologia65),
                "Desarrolladora Catalina",
                false, "Completado", "Enero 2024", "REF-003", false,
                Arrays.asList("Azotea", "Salón de eventos", "Lavandería común"),
                tipologia45,
                "https://qr.inmia.com/proy003"
        ));

        // PROYECTO 4
        proyectos.add(new Proyecto(
                "proy_004",
                "Torres del Mar",
                "Miraflores, Lima",
                "Departamentos con vista al mar en ubicación exclusiva frente al océano. Zona residencial de alta demanda, acabados premium, balcones amplios y acceso rápido a malecón. Edificio ecoeficiente con certificación energética.",
                Arrays.asList("Laura Díaz", "Carlos Mendoza"),
                "En planos",
                new int[]{R.drawable.images_2, R.drawable.onboarding1},
                R.drawable.images_2,
                Arrays.asList(tipologia65, tipologia90, tipologiaPenthouse),
                "Grupo Marina Inmobiliaria",
                true, "Nuevo", "Abril 2026", "REF-004", true,
                Arrays.asList("Vista al mar", "Balcones amplios", "Acceso playa", "Recepción 24/7", "Pet Spa"),
                tipologiaPenthouse,
                "https://qr.inmia.com/proy004"
        ));

        return proyectos;
    }
}
