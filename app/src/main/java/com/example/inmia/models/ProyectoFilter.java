package com.example.inmia.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ProyectoFilter {

    private List<String> estados;
    private double precioMin;
    private double precioMax;
    private int areaMin;
    private int areaMax;
    private int dormitoriosMin;
    private int dormitoriosMax;
    private int tipologiasMin;
    private String ordenarPor;
    private String buscarNombre;

    private boolean petFriendly;
    private boolean conAscensor;
    private boolean conTerraza;
    private boolean conBalcon;
    private boolean conAireAcondicionado;
    private boolean conCocinaIntegrada;
    private boolean amueblado;
    private boolean conPersianas;

    public static final String ORDEN_PRECIO_MAYOR_MENOR = "precio_mayor_menor";
    public static final String ORDEN_PRECIO_MENOR_MAYOR = "precio_menor_mayor";
    public static final String ORDEN_NOMBRE_A_Z = "nombre_a_z";
    public static final String ORDEN_NOMBRE_Z_A = "nombre_z_a";

    public ProyectoFilter() {
        estados = new ArrayList<>();
        precioMin = 0;
        precioMax = Double.MAX_VALUE;
        areaMin = 0;
        areaMax = Integer.MAX_VALUE;
        dormitoriosMin = 0;
        dormitoriosMax = Integer.MAX_VALUE;
        tipologiasMin = 0;
        ordenarPor = null;
        buscarNombre = "";
        petFriendly = false;
        conAscensor = false;
        conTerraza = false;
        conBalcon = false;
        conAireAcondicionado = false;
        conCocinaIntegrada = false;
        amueblado = false;
        conPersianas = false;
    }

    public boolean matches(Proyecto proyecto) {
        if (proyecto == null) return false;

        if (!estados.isEmpty() && !estados.contains(proyecto.getEstadoProyecto())) {
            return false;
        }

        if (buscarNombre != null && !buscarNombre.trim().isEmpty()) {
            String nombre = proyecto.getNombre() != null ? proyecto.getNombre().toLowerCase(Locale.getDefault()) : "";
            if (!nombre.contains(buscarNombre.toLowerCase(Locale.getDefault()))) {
                return false;
            }
        }

        Tipologia tip = proyecto.getTipologiaPrincipal();
        if (tip != null) {
            double precio = parsePrecio(tip.getPrecio());
            if (precio < precioMin || precio > precioMax) {
                return false;
            }

            int area = parseArea(tip.getArea());
            if (area < areaMin || area > areaMax) {
                return false;
            }

            int dormitorios = parseDormitorios(tip.getDormitorios());
            if (dormitorios < dormitoriosMin || dormitorios > dormitoriosMax) {
                return false;
            }

            if (petFriendly && !proyecto.isPetFriendly()) return false;
            if (conAscensor && !proyecto.isConAscensor()) return false;
            if (conTerraza && !tip.isTerraza()) return false;
            if (conBalcon && !tip.isBalcon()) return false;
            if (conAireAcondicionado && !tip.isAireAcondicionado()) return false;
            if (conCocinaIntegrada && !tip.isCocinaIntegrada()) return false;
            if (amueblado && !tip.isAmueblado()) return false;
            if (conPersianas && !tip.isPersianasAutomaticas()) return false;
        }

        if (tipologiasMin > 0) {
            int cantTipologias = proyecto.getTipologias() != null ? proyecto.getTipologias().size() : 0;
            if (cantTipologias < tipologiasMin) {
                return false;
            }
        }

        return true;
    }

    public Comparator<Proyecto> getComparator() {
        if (ordenarPor == null) return null;

        switch (ordenarPor) {
            case ORDEN_PRECIO_MAYOR_MENOR:
                return (p1, p2) -> {
                    double precio1 = p1.getTipologiaPrincipal() != null ? parsePrecio(p1.getTipologiaPrincipal().getPrecio()) : 0;
                    double precio2 = p2.getTipologiaPrincipal() != null ? parsePrecio(p2.getTipologiaPrincipal().getPrecio()) : 0;
                    return Double.compare(precio2, precio1);
                };
            case ORDEN_PRECIO_MENOR_MAYOR:
                return (p1, p2) -> {
                    double precio1 = p1.getTipologiaPrincipal() != null ? parsePrecio(p1.getTipologiaPrincipal().getPrecio()) : 0;
                    double precio2 = p2.getTipologiaPrincipal() != null ? parsePrecio(p2.getTipologiaPrincipal().getPrecio()) : 0;
                    return Double.compare(precio1, precio2);
                };
            case ORDEN_NOMBRE_A_Z:
                return (p1, p2) -> {
                    String nombre1 = p1.getNombre() != null ? p1.getNombre().toLowerCase() : "";
                    String nombre2 = p2.getNombre() != null ? p2.getNombre().toLowerCase() : "";
                    return nombre1.compareTo(nombre2);
                };
            case ORDEN_NOMBRE_Z_A:
                return (p1, p2) -> {
                    String nombre1 = p1.getNombre() != null ? p1.getNombre().toLowerCase() : "";
                    String nombre2 = p2.getNombre() != null ? p2.getNombre().toLowerCase() : "";
                    return nombre2.compareTo(nombre1);
                };
            default:
                return null;
        }
    }

    private double parsePrecio(String precio) {
        if (precio == null || precio.isEmpty()) return 0;
        String limpio = precio.replaceAll("[^0-9.]", "");
        try {
            return Double.parseDouble(limpio);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int parseArea(String area) {
        if (area == null || area.isEmpty()) return 0;
        String limpio = area.replaceAll("[^0-9]", "");
        try {
            return Integer.parseInt(limpio);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int parseDormitorios(String dormitorios) {
        if (dormitorios == null || dormitorios.isEmpty()) return 0;
        String limpio = dormitorios.replaceAll("[^0-9]", "");
        try {
            return Integer.parseInt(limpio);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean hasActiveFilters() {
        return !estados.isEmpty() ||
               precioMin > 0 || precioMax < Double.MAX_VALUE ||
               areaMin > 0 || areaMax < Integer.MAX_VALUE ||
               dormitoriosMin > 0 || dormitoriosMax < Integer.MAX_VALUE ||
               tipologiasMin > 0 ||
               petFriendly || conAscensor || conTerraza || conBalcon ||
               conAireAcondicionado || conCocinaIntegrada || amueblado || conPersianas ||
               (ordenarPor != null && !ordenarPor.isEmpty());
    }

    public void reset() {
        estados.clear();
        precioMin = 0;
        precioMax = Double.MAX_VALUE;
        areaMin = 0;
        areaMax = Integer.MAX_VALUE;
        dormitoriosMin = 0;
        dormitoriosMax = Integer.MAX_VALUE;
        tipologiasMin = 0;
        ordenarPor = null;
        buscarNombre = "";
        petFriendly = false;
        conAscensor = false;
        conTerraza = false;
        conBalcon = false;
        conAireAcondicionado = false;
        conCocinaIntegrada = false;
        amueblado = false;
        conPersianas = false;
    }

    public List<String> getEstados() { return estados; }
    public void setEstados(List<String> estados) { this.estados = estados; }

    public double getPrecioMin() { return precioMin; }
    public void setPrecioMin(double precioMin) { this.precioMin = precioMin; }

    public double getPrecioMax() { return precioMax; }
    public void setPrecioMax(double precioMax) { this.precioMax = precioMax; }

    public int getAreaMin() { return areaMin; }
    public void setAreaMin(int areaMin) { this.areaMin = areaMin; }

    public int getAreaMax() { return areaMax; }
    public void setAreaMax(int areaMax) { this.areaMax = areaMax; }

    public int getDormitoriosMin() { return dormitoriosMin; }
    public void setDormitoriosMin(int dormitoriosMin) { this.dormitoriosMin = dormitoriosMin; }

    public int getDormitoriosMax() { return dormitoriosMax; }
    public void setDormitoriosMax(int dormitoriosMax) { this.dormitoriosMax = dormitoriosMax; }

    public int getTipologiasMin() { return tipologiasMin; }
    public void setTipologiasMin(int tipologiasMin) { this.tipologiasMin = tipologiasMin; }

    public String getOrdenarPor() { return ordenarPor; }
    public void setOrdenarPor(String ordenarPor) { this.ordenarPor = ordenarPor; }

    public String getBuscarNombre() { return buscarNombre; }
    public void setBuscarNombre(String buscarNombre) { this.buscarNombre = buscarNombre; }

    public boolean isPetFriendly() { return petFriendly; }
    public void setPetFriendly(boolean petFriendly) { this.petFriendly = petFriendly; }

    public boolean isConAscensor() { return conAscensor; }
    public void setConAscensor(boolean conAscensor) { this.conAscensor = conAscensor; }

    public boolean isConTerraza() { return conTerraza; }
    public void setConTerraza(boolean conTerraza) { this.conTerraza = conTerraza; }

    public boolean isConBalcon() { return conBalcon; }
    public void setConBalcon(boolean conBalcon) { this.conBalcon = conBalcon; }

    public boolean isConAireAcondicionado() { return conAireAcondicionado; }
    public void setConAireAcondicionado(boolean conAireAcondicionado) { this.conAireAcondicionado = conAireAcondicionado; }

    public boolean isConCocinaIntegrada() { return conCocinaIntegrada; }
    public void setConCocinaIntegrada(boolean conCocinaIntegrada) { this.conCocinaIntegrada = conCocinaIntegrada; }

    public boolean isAmueblado() { return amueblado; }
    public void setAmueblado(boolean amueblado) { this.amueblado = amueblado; }

    public boolean isConPersianas() { return conPersianas; }
    public void setConPersianas(boolean conPersianas) { this.conPersianas = conPersianas; }
}
