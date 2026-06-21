package com.example.inmia.models;

public class Asesor {
    private final String id;
    private final String nombre;
    private final String distrito;
    private final String rol;
    private final String email;
    private final String telefono;
    private final String dni;
    private final String estado;
    private final String zonaTrabajo;
    private int metaVentasMensual;
    private int metaCitasMensual;
    private int metaGananciasMensual;
    private final int ventasMensualActual;
    private final int citasMensualActual;
    private final int gananciasMensualActual;
    private final int fotoResId;
    private String fotoUrl = "";

    public Asesor(String id,
                  String nombre,
                  String distrito,
                  String rol,
                  String email,
                  String telefono,
                  String dni,
                  String estado,
                  String zonaTrabajo,
                  int metaVentasMensual,
                  int metaCitasMensual,
                  int metaGananciasMensual,
                  int ventasMensualActual,
                  int citasMensualActual,
                  int gananciasMensualActual,
                  int fotoResId) {
        this.id = id;
        this.nombre = nombre;
        this.distrito = distrito;
        this.rol = rol;
        this.email = email;
        this.telefono = telefono;
        this.dni = dni;
        this.estado = estado;
        this.zonaTrabajo = zonaTrabajo;
        this.metaVentasMensual = metaVentasMensual;
        this.metaCitasMensual = metaCitasMensual;
        this.metaGananciasMensual = metaGananciasMensual;
        this.ventasMensualActual = ventasMensualActual;
        this.citasMensualActual = citasMensualActual;
        this.gananciasMensualActual = gananciasMensualActual;
        this.fotoResId = fotoResId;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDistrito() {
        return distrito;
    }

    public String getRol() {
        return rol;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDni() {
        return dni;
    }

    public String getEstado() {
        return estado;
    }

    public String getZonaTrabajo() {
        return zonaTrabajo;
    }

    public int getMetaVentasMensual() {
        return metaVentasMensual;
    }

    public int getMetaCitasMensual() {
        return metaCitasMensual;
    }

    public int getMetaGananciasMensual() {
        return metaGananciasMensual;
    }

    public int getVentasMensualActual() {
        return ventasMensualActual;
    }

    public int getCitasMensualActual() {
        return citasMensualActual;
    }

    public int getGananciasMensualActual() {
        return gananciasMensualActual;
    }

    public void setMetaVentasMensual(int metaVentasMensual) {
        this.metaVentasMensual = metaVentasMensual;
    }

    public void setMetaCitasMensual(int metaCitasMensual) {
        this.metaCitasMensual = metaCitasMensual;
    }

    public void setMetaGananciasMensual(int metaGananciasMensual) {
        this.metaGananciasMensual = metaGananciasMensual;
    }

    public int getFotoResId() {
        return fotoResId;
    }

    public String getFotoUrl() {
        return fotoUrl != null ? fotoUrl : "";
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
}
