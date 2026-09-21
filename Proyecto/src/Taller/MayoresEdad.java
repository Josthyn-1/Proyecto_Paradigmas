package Taller;

public class MayoresEdad {
    public void mostrarMayoresEdad(Cola cola) {
        if (cola.estaVacia()) {
            System.out.println("La cola está vacía");
            return;
        }

        Nodo actual = cola.frente;
        boolean hayMayoresEdad = false;

        while (actual != null) {
            Cliente cliente = actual.cliente;
            if (cliente.getEdad() >= 18) {
                System.out.println("Paciente: " + cliente.getNombre() + ", Edad: " + cliente.getEdad() + ", Tipo de consulta: " + cliente.getTipoConsulta());
                hayMayoresEdad = true;
            }
            actual = actual.siguiente;
        }

        if (!hayMayoresEdad) {
            System.out.println("No hay pacientes mayores de edad en la cola");
        }
    }
}
