package co.edu.poligran.paradigmas.agenda.modelo;

/**
 * Correo electrónico asociado a una persona (idPersona actúa como llave foránea).
 */
public class CorreoElectronico {

    private int id;
    private int idPersona;
    private String cuenta;

    public CorreoElectronico() {
    }

    public CorreoElectronico(String cuenta) {
        this.cuenta = cuenta;
    }

    public CorreoElectronico(int id, int idPersona, String cuenta) {
        this(cuenta);
        this.id = id;
        this.idPersona = idPersona;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPersona() { return idPersona; }
    public void setIdPersona(int idPersona) { this.idPersona = idPersona; }

    public String getCuenta() { return cuenta; }
    public void setCuenta(String cuenta) { this.cuenta = cuenta; }

    @Override
    public String toString() {
        return "(" + id + ") " + cuenta;
    }
}
