package com.example.inmia.models;

/**
 * Modelo que representa una Tipología de Departamento
 *
 * Cada Proyecto puede tener una o más tipologías.
 * Una tipología define las características específicas de un tipo de departamento:
 * área, dormitorios, baños, estacionamientos, precio, etc.
 */
public class Tipologia {

    private String id;                    // ID único de la tipología
    private String nombre;                // Nombre corto (ej: "45 m² · 1d")
    private String descripcion;           // Descripción detallada de la tipología
    private String area;                  // Área en m² (ej: "45 m²")
    private String dormitorios;           // Número de dormitorios (ej: "1")
    private String banos;                 // Número de baños (ej: "1")
    private String estacionamiento;       // Info de estacionamiento (ej: "Sin estacionamiento")
    private String precio;                // Precio total (ej: "S/ 420,000")
    private String estado;                // Estado de disponibilidad (ej: "Disponible")
    private int imagenHero;               // Imagen principal de esta tipología
    private int[] imagenes;               // Array de imágenes de esta tipología
    private boolean patio;                // Si tiene patio
    private String certificadoEnergetico; // Tipo de certificado (A, B, C, D, etc)
    private boolean terraza;              // Si tiene terraza
    private boolean balcon;               // Si tiene balcón
    private boolean aireAcondicionado;    // Si tiene aire acondicionado
    private boolean cocinaIntegrada;      // Si tiene cocina integrada
    private int closets;                  // Número de closets/armarios empotrados
    private String tipoPiso;              // Tipo de piso (cerámica, madera, etc)
    private boolean amueblado;            // Si viene amueblado
    private String ventilacion;           // Tipo de ventilación (natural, forzada, etc)
    private boolean persianasAutomaticas; // Si tiene persianas automáticas
    private String tipoAcabados;          // Tipo de acabados (lujo, estándar, básico)

    // Constructor
    public Tipologia() {}
    public Tipologia(String id,
                    String nombre,
                    String descripcion,
                    String area,
                    String dormitorios,
                    String banos,
                    String estacionamiento,
                    String precio,
                    String estado,
                    int imagenHero,
                    int[] imagenes,
                    boolean patio,
                    String certificadoEnergetico,
                    boolean terraza,
                    boolean balcon,
                    boolean aireAcondicionado,
                    boolean cocinaIntegrada,
                    int closets,
                    String tipoPiso,
                    boolean amueblado,
                    String ventilacion,
                    boolean persianasAutomaticas,
                    String tipoAcabados) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.area = area;
        this.dormitorios = dormitorios;
        this.banos = banos;
        this.estacionamiento = estacionamiento;
        this.precio = precio;
        this.estado = estado;
        this.imagenHero = imagenHero;
        this.imagenes = imagenes;
        this.patio = patio;
        this.certificadoEnergetico = certificadoEnergetico;
        this.terraza = terraza;
        this.balcon = balcon;
        this.aireAcondicionado = aireAcondicionado;
        this.cocinaIntegrada = cocinaIntegrada;
        this.closets = closets;
        this.tipoPiso = tipoPiso;
        this.amueblado = amueblado;
        this.ventilacion = ventilacion;
        this.persianasAutomaticas = persianasAutomaticas;
        this.tipoAcabados = tipoAcabados;
    }

    // Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getArea() { return area; }
    public String getDormitorios() { return dormitorios; }
    public String getBanos() { return banos; }
    public String getEstacionamiento() { return estacionamiento; }
    public String getPrecio() { return precio; }
    public String getEstado() { return estado; }
    public int getImagenHero() { return imagenHero; }
    public int[] getImagenes() { return imagenes; }
    public boolean isPatio() { return patio; }
    public String getCertificadoEnergetico() { return certificadoEnergetico; }
    public boolean isTerraza() { return terraza; }
    public boolean isBalcon() { return balcon; }
    public boolean isAireAcondicionado() { return aireAcondicionado; }
    public boolean isCocinaIntegrada() { return cocinaIntegrada; }
    public int getClosets() { return closets; }
    public String getTipoPiso() { return tipoPiso; }
    public boolean isAmueblado() { return amueblado; }
    public String getVentilacion() { return ventilacion; }
    public boolean isPersianasAutomaticas() { return persianasAutomaticas; }
    public String getTipoAcabados() { return tipoAcabados; }

    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setArea(String area) { this.area = area; }
    public void setDormitorios(String dormitorios) { this.dormitorios = dormitorios; }
    public void setBanos(String banos) { this.banos = banos; }
    public void setEstacionamiento(String estacionamiento) { this.estacionamiento = estacionamiento; }
    public void setPrecio(String precio) { this.precio = precio; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setImagenHero(int imagenHero) { this.imagenHero = imagenHero; }
    public void setImagenes(int[] imagenes) { this.imagenes = imagenes; }
    public void setPatio(boolean patio) { this.patio = patio; }
    public void setCertificadoEnergetico(String certificadoEnergetico) { this.certificadoEnergetico = certificadoEnergetico; }
    public void setTerraza(boolean terraza) { this.terraza = terraza; }
    public void setBalcon(boolean balcon) { this.balcon = balcon; }
    public void setAireAcondicionado(boolean aireAcondicionado) { this.aireAcondicionado = aireAcondicionado; }
    public void setCocinaIntegrada(boolean cocinaIntegrada) { this.cocinaIntegrada = cocinaIntegrada; }
    public void setClosets(int closets) { this.closets = closets; }
    public void setTipoPiso(String tipoPiso) { this.tipoPiso = tipoPiso; }
    public void setAmueblado(boolean amueblado) { this.amueblado = amueblado; }
    public void setVentilacion(String ventilacion) { this.ventilacion = ventilacion; }
    public void setPersianasAutomaticas(boolean persianasAutomaticas) { this.persianasAutomaticas = persianasAutomaticas; }
    public void setTipoAcabados(String tipoAcabados) { this.tipoAcabados = tipoAcabados; }
}





