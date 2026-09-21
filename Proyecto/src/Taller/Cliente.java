package Taller;

public class Cliente {
    private final String nombre;
    private final int edad;
    private final String tipoConsulta;
    private int turno;

    public Cliente(String nombre, int edad, String tipoConsulta) {
        this.nombre = nombre;
        this.edad = edad;
        this.tipoConsulta = tipoConsulta;
        this.turno = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    public String getTipoConsulta() {
        return tipoConsulta;
    }

    public int getTurno() {
        return turno;
    }

    public void asignarTurno(int turno) {
        this.turno = turno;
    }
}