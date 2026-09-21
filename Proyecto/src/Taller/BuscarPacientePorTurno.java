package Taller;

public class BuscarPacientePorTurno {
    public void buscarPacientePorTurno(Cola cola, int turno) {
        if (cola.estaVacia()) {
            System.out.println("La cola está vacía");
            return;
        }

        Nodo actual = cola.frente;
        while (actual != null) {
            if (actual.cliente.getTurno() == turno) {
                Cliente cliente = actual.cliente;
                System.out.println("Paciente encontrado en el turno " + turno + ":");
                System.out.println("Nombre: " + cliente.getNombre());
                System.out.println("Edad: " + cliente.getEdad());
                System.out.println("Tipo de consulta: " + cliente.getTipoConsulta());
                return;
            }
            actual = actual.siguiente;
        }

        System.out.println("No se encontró ningún paciente en el turno " + turno);
    }
}
