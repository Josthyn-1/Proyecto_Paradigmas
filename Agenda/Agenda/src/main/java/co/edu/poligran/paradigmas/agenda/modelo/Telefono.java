package co.edu.poligran.paradigmas.agenda.modelo;

/**
 * Teléfono asociado a una persona (idPersona actúa como llave foránea).
 */
public class Telefono {

    private int id;
    private int idPersona;
    private String numero;
    private String descripcion;

    public Telefono() {
    }

    public Telefono(String numero, String descripcion) {
        this.numero = numero;
        this.descripcion = descripcion;
    }

    public Telefono(int id, int idPersona, String numero, String descripcion) {
        this(numero, descripcion);
        this.id = id;
        this.idPersona = idPersona;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPersona() { return idPersona; }
    public void setIdPersona(int idPersona) { this.idPersona = idPersona; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return "(" + id + ") " + numero + " - " + descripcion;
    }
}
