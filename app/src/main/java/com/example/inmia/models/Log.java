package com.example.inmia.models;

public class Log {

    // Tipos de log (definen ícono y color en la lista)
    public static final String TIPO_CUENTA        = "cuenta";         // altas de cuentas nuevas
    public static final String TIPO_ESTADO_CUENTA = "estado_cuenta";  // activar/desactivar usuarios
    public static final String TIPO_PROYECTO      = "proyecto";       // crear/editar proyectos, inmobiliaria
    public static final String TIPO_SOLICITUD     = "solicitud";      // solicitudes de asesor
    public static final String TIPO_CITA          = "cita";           // agendar/cancelar citas
    public static final String TIPO_SEPARACION    = "separacion";     // separaciones y pagos

    private String descripcion;
    private String fecha;          // texto ya formateado para mostrar
    private String tipo;
    private String rol;            // quién originó el evento (opcional)

    public Log(String descripcion, String fecha, String tipo) {
        this(descripcion, fecha, tipo, "");
    }

    public Log(String descripcion, String fecha, String tipo, String rol) {
        this.descripcion = descripcion;
        this.fecha       = fecha;
        this.tipo        = tipo;
        this.rol         = rol;
    }

    // Getters
    public String getDescripcion() { return descripcion; }
    public String getFecha()       { return fecha; }
    public String getTipo()        { return tipo; }
    public String getRol()         { return rol; }
}
