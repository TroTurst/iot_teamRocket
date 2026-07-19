package com.example.inmia.models;

import java.util.ArrayList;
import java.util.List;

public class ReporteFilter {

    public static final int PERIODO_ULTIMO_MES    = 0;
    public static final int PERIODO_ULTIMOS_3     = 1;
    public static final int PERIODO_ESTE_ANIO     = 2;
    public static final int PERIODO_TODOS         = 3;

    public static final int VISTA_POR_PROYECTO    = 0;
    public static final int VISTA_POR_ASESOR      = 1;

    private int periodo;
    private int vista;
    private String distrito;
    private String asesorId;
    private List<String> estados;

    public ReporteFilter() {
        periodo   = PERIODO_ULTIMOS_3;
        vista     = VISTA_POR_PROYECTO;
        distrito  = "";
        asesorId  = "";
        estados   = new ArrayList<>();
        estados.add("Aprobada");
        estados.add("Pagada");
    }

    public void reset() {
        periodo  = PERIODO_ULTIMOS_3;
        vista    = VISTA_POR_PROYECTO;
        distrito = "";
        asesorId = "";
        estados.clear();
        estados.add("Aprobada");
        estados.add("Pagada");
    }

    public boolean hasActiveFilters() {
        boolean periodoDistinto = periodo != PERIODO_ULTIMOS_3;
        boolean vistaDistinta   = vista != VISTA_POR_PROYECTO;
        boolean distritoDistinto = distrito != null && !distrito.isEmpty();
        boolean asesorDistinto   = asesorId != null && !asesorId.isEmpty();
        boolean estadosDistintos = estados != null && !estados.isEmpty()
                && !(estados.size() == 2
                    && estados.contains("Aprobada")
                    && estados.contains("Pagada"));
        return periodoDistinto || vistaDistinta || distritoDistinto || asesorDistinto || estadosDistintos;
    }

    public String describeActiveFilters() {
        if (!hasActiveFilters()) return "";

        StringBuilder sb = new StringBuilder();
        sb.append("Filtros: ");

        switch (periodo) {
            case PERIODO_ULTIMO_MES: sb.append("\u00daltimo mes"); break;
            case PERIODO_ULTIMOS_3:  sb.append("\u00daltimos 3 meses"); break;
            case PERIODO_ESTE_ANIO:  sb.append("este a\u00f1o"); break;
            case PERIODO_TODOS:      sb.append("todos los per\u00edodos"); break;
        }
        sb.append(" \u2022 ");

        if (vista == VISTA_POR_ASESOR) sb.append("por asesor");
        else sb.append("por proyecto");
        sb.append(" \u2022 ");

        if (distritoDistinto()) {
            sb.append("distrito: ").append(distrito);
            sb.append(" \u2022 ");
        }

        if (asesorDistinto()) {
            sb.append("asesor: ").append(asesorId);
            sb.append(" \u2022 ");
        }

        if (estados != null && !estados.isEmpty()) {
            sb.append("estados: ");
            boolean first = true;
            for (String e : estados) {
                if (!first) sb.append(", ");
                sb.append(e);
                first = false;
            }
        }

        String result = sb.toString();
        if (result.endsWith(" \u2022 ")) {
            result = result.substring(0, result.length() - 3);
        }
        return result;
    }

    private boolean distritoDistinto() {
        return distrito != null && !distrito.isEmpty();
    }

    private boolean asesorDistinto() {
        return asesorId != null && !asesorId.isEmpty();
    }

    public int getPeriodo()                  { return periodo; }
    public void setPeriodo(int periodo)      { this.periodo = periodo; }

    public int getVista()                    { return vista; }
    public void setVista(int vista)          { this.vista = vista; }

    public String getDistrito()              { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }

    public String getAsesorId()              { return asesorId; }
    public void setAsesorId(String asesorId) { this.asesorId = asesorId; }

    public List<String> getEstados()         { return estados; }
    public void setEstados(List<String> estados) { this.estados = estados; }
}
