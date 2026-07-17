package com.example.inmia.models;

import java.util.List;

/**
 * Modelo que representa un Proyecto Inmobiliario
 *
 * Un proyecto es la entidad principal que contiene:
 * - Información básica (nombre, ubicación, descripción)
 * - Datos de marketing (fotos, vendedores)
 * - Estado del proyecto
 * - Una o más tipologías de departamentos
 */


public class Proyecto {

    private String id;                    // ID único del proyecto
    private String nombre;                // Nombre/título del proyecto
    private String distrito;              // Distrito donde se ubica el proyecto
    private String ubicacion;             // Dirección/ubicación del proyecto
    private double latitud;               // Coordenada latitud del proyecto
    private double longitud;              // Coordenada longitud del proyecto
    private String descripcion;           // Descripción larga del proyecto
    private List<String> vendedores;      // Asesores/vendedores asignados
    private String estadoProyecto;        // En planos, En preventa, En venta
    private int[] imagenes;               // Galería de fotos del proyecto
    private int imagenHeroPrincipal;      // Imagen destacada del proyecto
    private List<Tipologia> tipologias;   // Lista de tipologías disponibles
    private String inmobiliaria;          // Nombre de la empresa inmobiliaria
    private boolean conAscensor;          // Si tiene ascensor o no
    private String antiguedad;            // Antigüedad del proyecto (Nuevo, Años, etc)
    private String fechaLanzamiento;      // Fecha de lanzamiento del proyecto
    private String referencia;            // Código de referencia del proyecto
    private boolean petFriendly;          // Acepta mascotas
    private List<String> extras;          // Características extras (coworking, piscina, etc)
    private Tipologia tipologiaPrincipal; // Tipología principal (la que se ve primero)
    private String qrCode;
    private List<String> imagenesUrls;

    // Constructor
    public Proyecto() {}
    public Proyecto(String id,
                   String nombre,
                   String ubicacion,
                   String descripcion,
                   List<String> vendedores,
                   String estadoProyecto,
                   int[] imagenes,
                   int imagenHeroPrincipal,
                   List<Tipologia> tipologias,
                   String inmobiliaria,
                   boolean conAscensor,
                   String antiguedad,
                   String fechaLanzamiento,
                   String referencia,
                   boolean petFriendly,
                   List<String> extras,
                   Tipologia tipologiaPrincipal,
                   String qrCode) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.vendedores = vendedores;
        this.estadoProyecto = estadoProyecto;
        this.imagenes = imagenes;
        this.imagenHeroPrincipal = imagenHeroPrincipal;
        this.tipologias = tipologias;
        this.inmobiliaria = inmobiliaria;
        this.conAscensor = conAscensor;
        this.antiguedad = antiguedad;
        this.fechaLanzamiento = fechaLanzamiento;
        this.referencia = referencia;
        this.petFriendly = petFriendly;
        this.extras = extras;
        this.tipologiaPrincipal = tipologiaPrincipal;
        this.qrCode = qrCode;
    }

    // Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDistrito() { return distrito; }
    public String getUbicacion() { return ubicacion; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public String getDescripcion() { return descripcion; }
    public List<String> getVendedores() { return vendedores; }
    public String getEstadoProyecto() { return estadoProyecto; }
    public int[] getImagenes() { return imagenes; }
    public int getImagenHeroPrincipal() { return imagenHeroPrincipal; }
    public List<Tipologia> getTipologias() { return tipologias; }
    public String getInmobiliaria() { return inmobiliaria; }
    public boolean isConAscensor() { return conAscensor; }
    public String getAntiguedad() { return antiguedad; }
    public String getFechaLanzamiento() { return fechaLanzamiento; }
    public String getReferencia() { return referencia; }
    public boolean isPetFriendly() { return petFriendly; }
    public List<String> getExtras() { return extras; }
    public Tipologia getTipologiaPrincipal() { return tipologiaPrincipal; }
    public String getQrCode() { return qrCode; }
    public List<String> getImagenesUrls() { return imagenesUrls; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setVendedores(List<String> vendedores) { this.vendedores = vendedores; }
    public void setEstadoProyecto(String estadoProyecto) { this.estadoProyecto = estadoProyecto; }
    public void setImagenes(int[] imagenes) { this.imagenes = imagenes; }
    public void setImagenHeroPrincipal(int imagenHeroPrincipal) { this.imagenHeroPrincipal = imagenHeroPrincipal; }
    public void setTipologias(List<Tipologia> tipologias) { this.tipologias = tipologias; }
    public void setInmobiliaria(String inmobiliaria) { this.inmobiliaria = inmobiliaria; }
    public void setConAscensor(boolean conAscensor) { this.conAscensor = conAscensor; }
    public void setAntiguedad(String antiguedad) { this.antiguedad = antiguedad; }
    public void setFechaLanzamiento(String fechaLanzamiento) { this.fechaLanzamiento = fechaLanzamiento; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public void setPetFriendly(boolean petFriendly) { this.petFriendly = petFriendly; }
    public void setExtras(List<String> extras) { this.extras = extras; }
    public void setTipologiaPrincipal(Tipologia tipologiaPrincipal) { this.tipologiaPrincipal = tipologiaPrincipal; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public void setImagenesUrls(List<String> imagenesUrls) { this.imagenesUrls = imagenesUrls; }
}

