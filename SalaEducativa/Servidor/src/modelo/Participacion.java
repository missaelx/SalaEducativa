package modelo;

/**
 * Missael on 03/06/17.
 */
public class Participacion{
    private String nombre;
    private String mensaje;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Participacion(String nombre, String mensaje) {
        this.nombre = nombre;
        this.mensaje = mensaje;
    }
}
