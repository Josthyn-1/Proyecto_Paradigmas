package Taller;

public class Turnos {

    public void asignarTurno(Cola cola, Cliente cliente) {
        int turno = cola.obtenerSiguienteTurno();
        cliente.asignarTurno(turno);
        if (cola.estaVacia()) {
            System.out.println("Turno asignado a " + cliente.getNombre() + ": " + turno);
            return;
        }
        System.out.println("Turno asignado a " + cliente.getNombre() + " con edad: " + cliente.getEdad() + " y tipo de consulta: " + cliente.getTipoConsulta());
        System.out.println("Turno asignado: " + turno);
    }
}