package com.example.inmia.admin;

public class ReporteItem {
    private final int tipo;

    private int mediaVentas;
    private int mediaCitas;
    private String mediaGanancias;

    private String mejorNombre;
    private int mejorVentas;
    private int mejorCitas;
    private String mejorGanancias;

    private int casasVendidas;
    private int asesoresActivos;
    private int pendientes;

    private ReporteItem(int tipo) {
        this.tipo = tipo;
    }

    public static ReporteItem media(int ventas, int citas, String ganancias) {
        ReporteItem item = new ReporteItem(AdminReporteAdapter.TYPE_MEDIA);
        item.mediaVentas = ventas;
        item.mediaCitas = citas;
        item.mediaGanancias = ganancias;
        return item;
    }

    public static ReporteItem mejor(String nombre, int ventas, int citas, String ganancias) {
        ReporteItem item = new ReporteItem(AdminReporteAdapter.TYPE_MEJOR);
        item.mejorNombre = nombre;
        item.mejorVentas = ventas;
        item.mejorCitas = citas;
        item.mejorGanancias = ganancias;
        return item;
    }

    public static ReporteItem estado(int casasVendidas, int asesoresActivos, int pendientes) {
        ReporteItem item = new ReporteItem(AdminReporteAdapter.TYPE_ESTADO);
        item.casasVendidas = casasVendidas;
        item.asesoresActivos = asesoresActivos;
        item.pendientes = pendientes;
        return item;
    }

    public int getTipo() {
        return tipo;
    }

    public int getMediaVentas() {
        return mediaVentas;
    }

    public int getMediaCitas() {
        return mediaCitas;
    }

    public String getMediaGanancias() {
        return mediaGanancias;
    }

    public String getMejorNombre() {
        return mejorNombre;
    }

    public int getMejorVentas() {
        return mejorVentas;
    }

    public int getMejorCitas() {
        return mejorCitas;
    }

    public String getMejorGanancias() {
        return mejorGanancias;
    }

    public int getCasasVendidas() {
        return casasVendidas;
    }

    public int getAsesoresActivos() {
        return asesoresActivos;
    }

    public int getPendientes() {
        return pendientes;
    }
}

