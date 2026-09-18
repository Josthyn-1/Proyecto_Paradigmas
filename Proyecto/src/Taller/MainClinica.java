package Taller;
import java.util.Scanner;
public class MainClinica {
    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        Cola cola = new Cola();
        Turnos turnos = new Turnos();
        BuscarPacientePorTurno buscarPacientePorTurno = new BuscarPacientePorTurno();
        MayoresEdad mayoresEdad = new MayoresEdad();
        int opcion;
        System.out.println("Bienvenido a la clinica");
        do {
            System.out.println("------Menú------");
            System.out.println("1. Agregar paciente");
            System.out.println("2. Atender al siguiente paciente");
            System.out.println("3. Consultar el proximo paciente");
            System.out.println("4. Mostrar todos los pacientes");
            System.out.println("5. Buscar paciente por numero de turno");
            System.out.println("6. Contar cuantos pacientes estan esperando");
            System.out.println("7. Consultar cuantos pacientes son mayores de edad");
            System.out.println("8. Salir");
            System.out.println("Seleccione una opción");
            opcion = entrada.nextInt();
            switch (opcion) {
                case 1:
                   System.out.println("Ingrese el nombre del paciente:");
                    String nombre = entrada.next();
                    System.out.println("Ingrese su edad:");
                    int edad = entrada.nextInt();
                    System.out.println("Ingrese el tipo de consulta");
                    String tipoConsulta = entrada.next();
                    System.out.println("Paciente registrado: " + nombre + " con edad: " + edad + " y tipo de consulta: " + tipoConsulta);
                    Cliente nuevoPaciente = new Cliente(nombre, edad, tipoConsulta);
                    turnos.asignarTurno(cola, nuevoPaciente);
                    cola.encolar(nuevoPaciente);
                    break;
                case 2:
                    if (cola.estaVacia()) {
                        System.out.println("No hay clientes para atender");
                        break;
                    }
                    Cliente cliente = cola.descolar();
                    System.out.println("Atendiendo a " + cliente.getNombre() + " con turno: " + cliente.getTurno() + " y tipo de consulta: " + cliente.getTipoConsulta());
                    break;
                case 3:
                    if (cola.estaVacia()) {
                        System.out.println("No hay clientes registrados");
                    } else {
                        Cliente proximo = cola.consultar();
                        System.out.println("Proximo cliente: " + proximo.getNombre()
                                + " con edad: " + proximo.getEdad()+ " y tipo de consulta: " + proximo.getTipoConsulta() + " con turno: " + proximo.getTurno());
                    }
                    break;
                case 4:
                    if (cola.estaVacia()) {
                        System.out.println("No hay clientes registrados");
                    } else {
                        cola.mostrarCola();
                    }
                    break;
                case 5:
                    System.out.println("Ingrese el número de turno del paciente a buscar:");
                    int turno = entrada.nextInt();
                    buscarPacientePorTurno.buscarPacientePorTurno(cola, turno);
                    break;
                case 6:
                    System.out.println("Número de pacientes esperando: " + cola.cantidad());
                    break;
                case 7:
                    mayoresEdad.mostrarMayoresEdad(cola);
                    break;
                case 8:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción válida.");   
                    break;
            }
            
        } while (opcion != 8);
        entrada.close();
    }
}
