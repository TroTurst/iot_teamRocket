package com.example.inmia.cliente;

public class Proyecto {
    private String nombre;
    private String ubicacion;
    private String precio;
    private String etiqueta;
    private int imagenResId;

    public Proyecto(String nombre, String ubicacion, String precio, String etiqueta, int imagenResId) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.precio = precio;
        this.etiqueta = etiqueta;
        this.imagenResId = imagenResId;
    }

    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }
    public String getPrecio() { return precio; }
    public String getEtiqueta() { return etiqueta; }
    public int getImagenResId() { return imagenResId; }
    
}
