package co.edu.poligran.paradigmas.agenda.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una persona de la agenda con sus teléfonos y correos.
 */
public class Persona {

    private int id;
    private String nombre;
    private String apellido;
    private List<Telefono> telefonos;
    private List<CorreoElectronico> correos;

    public Persona() {
        this.telefonos = new ArrayList<>();
        this.correos = new ArrayList<>();
    }

    public Persona(String nombre, String apellido) {
        this();
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public Persona(int id, String nombre, String apellido) {
        this(nombre, apellido);
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public List<Telefono> getTelefonos() { return telefonos; }
    public void setTelefonos(List<Telefono> telefonos) { this.telefonos = telefonos; }

    public List<CorreoElectronico> getCorreos() { return correos; }
    public void setCorreos(List<CorreoElectronico> correos) { this.correos = correos; }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre + " " + apellido;
    }
}
