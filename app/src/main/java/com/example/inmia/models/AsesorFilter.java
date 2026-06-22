package com.example.inmia.models;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class AsesorFilter {

    private String buscarNombre;
    private String distrito;
    private String ordenarPor;
    private int minProyectos;
    private int minCitas;

    public static final String ORDEN_NOMBRE_A_Z = "nombre_a_z";
    public static final String ORDEN_NOMBRE_Z_A = "nombre_z_a";
    public static final String ORDEN_PROYECTOS_MAS_MENOS = "proyectos_mas_menos";
    public static final String ORDEN_PROYECTOS_MENOS_MAS = "proyectos_menos_mas";
    public static final String ORDEN_CITAS_MAS_MENOS = "citas_mas_menos";
    public static final String ORDEN_CITAS_MENOS_MAS = "citas_menos_mas";

    public AsesorFilter() {
        buscarNombre = "";
        distrito = "";
        ordenarPor = null;
        minProyectos = 0;
        minCitas = 0;
    }

    public boolean matches(Asesor asesor) {
        if (asesor == null) return false;

        if (buscarNombre != null && !buscarNombre.trim().isEmpty()) {
            String nombre = asesor.getNombre() != null ? asesor.getNombre().toLowerCase(Locale.getDefault()) : "";
            if (!nombre.contains(buscarNombre.toLowerCase(Locale.getDefault()))) {
                return false;
            }
        }

        if (distrito != null && !distrito.trim().isEmpty()) {
            boolean matchesDistrito = false;
            if (asesor.getDistrito() != null && asesor.getDistrito().equalsIgnoreCase(distrito)) {
                matchesDistrito = true;
            }
            if (asesor.getDistritos() != null && asesor.getDistritos().contains(distrito)) {
                matchesDistrito = true;
            }
            if (!matchesDistrito) {
                return false;
            }
        }

        if (minProyectos > 0) {
            return false;
        }

        if (minCitas > 0 && asesor.getCitasMensualActual() < minCitas) {
            return false;
        }

        return true;
    }

    public Comparator<Asesor> getComparator() {
        if (ordenarPor == null) return null;

        switch (ordenarPor) {
            case ORDEN_NOMBRE_A_Z:
                return (a1, a2) -> {
                    String n1 = a1.getNombre() != null ? a1.getNombre().toLowerCase() : "";
                    String n2 = a2.getNombre() != null ? a2.getNombre().toLowerCase() : "";
                    return n1.compareTo(n2);
                };
            case ORDEN_NOMBRE_Z_A:
                return (a1, a2) -> {
                    String n1 = a1.getNombre() != null ? a1.getNombre().toLowerCase() : "";
                    String n2 = a2.getNombre() != null ? a2.getNombre().toLowerCase() : "";
                    return n2.compareTo(n1);
                };
            case ORDEN_PROYECTOS_MAS_MENOS:
                return (a1, a2) -> Integer.compare(
                        a2.getVentasMensualActual(), a1.getVentasMensualActual());
            case ORDEN_PROYECTOS_MENOS_MAS:
                return (a1, a2) -> Integer.compare(
                        a1.getVentasMensualActual(), a2.getVentasMensualActual());
            case ORDEN_CITAS_MAS_MENOS:
                return (a1, a2) -> Integer.compare(
                        a2.getCitasMensualActual(), a1.getCitasMensualActual());
            case ORDEN_CITAS_MENOS_MAS:
                return (a1, a2) -> Integer.compare(
                        a1.getCitasMensualActual(), a2.getCitasMensualActual());
            default:
                return null;
        }
    }

    public boolean hasActiveFilters() {
        return (ordenarPor != null && !ordenarPor.isEmpty())
                || (distrito != null && !distrito.isEmpty())
                || minProyectos > 0
                || minCitas > 0;
    }

    public void reset() {
        buscarNombre = "";
        distrito = "";
        ordenarPor = null;
        minProyectos = 0;
        minCitas = 0;
    }

    public String getBuscarNombre() { return buscarNombre; }
    public void setBuscarNombre(String buscarNombre) { this.buscarNombre = buscarNombre; }

    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }

    public String getOrdenarPor() { return ordenarPor; }
    public void setOrdenarPor(String ordenarPor) { this.ordenarPor = ordenarPor; }

    public int getMinProyectos() { return minProyectos; }
    public void setMinProyectos(int minProyectos) { this.minProyectos = minProyectos; }

    public int getMinCitas() { return minCitas; }
    public void setMinCitas(int minCitas) { this.minCitas = minCitas; }
}
