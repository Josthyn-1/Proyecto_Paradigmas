package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.EstadoMesa;
import co.edu.poligran.paradigmas.agenda.modelo.EstadoPedido;
import co.edu.poligran.paradigmas.agenda.modelo.HistorialEstado;
import co.edu.poligran.paradigmas.agenda.modelo.Mesa;
import co.edu.poligran.paradigmas.agenda.modelo.Pedido;
import co.edu.poligran.paradigmas.agenda.modelo.RolUsuario;
import co.edu.poligran.paradigmas.agenda.modelo.Usuario;
import co.edu.poligran.paradigmas.agenda.persistencia.HistorialEstadoDAO;
import co.edu.poligran.paradigmas.agenda.persistencia.MesaDAO;
import co.edu.poligran.paradigmas.agenda.persistencia.PedidoDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Agenda {
    private final Scanner entrada = new Scanner(System.in);
    private final List<Mesa> mesas = new ArrayList<>();
    private final List<Pedido> pedidos = new ArrayList<>();
    private final List<HistorialEstado> historial = new ArrayList<>();
    private final MesaDAO mesaDAO = new MesaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final HistorialEstadoDAO historialDAO = new HistorialEstadoDAO();
    private final Usuario mesero = new Usuario(
            1, "Mesero", "Demo", "mesero", RolUsuario.MESERO, true);

    public static void main(String[] args) {
        new Agenda().iniciar();
    }

    private void iniciar() {
        inicializarMesas();
        System.out.println("=== Agenda de pedidos del restaurante ===");

        boolean ejecutando = true;
        while (ejecutando) {
            mostrarMenu();
            int opcion = leerEntero("Selecciona una opción: ");
            switch (opcion) {
                case 1:
                    listarPedidos();
                    break;
                case 2:
                    crearPedido();
                    break;
                case 3:
                    cambiarEstadoPedido();
                    break;
                case 4:
                    mostrarHistorial();
                    break;
                case 0:
                    ejecutando = false;
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
        System.out.println("Programa finalizado.");
    }

    private void inicializarMesas() {
        mesaDAO.crear(mesas, new Mesa(0, 1, 4, EstadoMesa.LIBRE));
        mesaDAO.crear(mesas, new Mesa(0, 2, 4, EstadoMesa.LIBRE));
        mesaDAO.crear(mesas, new Mesa(0, 3, 2, EstadoMesa.LIBRE));
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("1. Listar pedidos");
        System.out.println("2. Crear pedido");
        System.out.println("3. Cambiar estado de un pedido");
        System.out.println("4. Consultar historial de estados");
        System.out.println("0. Salir");
    }

    private void listarPedidos() {
        List<Pedido> lista = pedidoDAO.listar(pedidos);
        if (lista.isEmpty()) {
            System.out.println("Todavía no hay pedidos.");
            return;
        }

        System.out.println("\nPedidos:");
        for (Pedido pedido : lista) {
            System.out.printf("Pedido #%d | Mesa %d | Estado: %s | Mesero: %s %s%n",
                    pedido.getId(),
                    pedido.getMesa().getNumero(),
                    pedido.getEstado(),
                    pedido.getMesero().getNombre(),
                    pedido.getMesero().getApellido());
        }
    }

    private void crearPedido() {
        List<Mesa> mesasLibres = mesaDAO.consultarLibres(mesas);
        if (mesasLibres.isEmpty()) {
            System.out.println("No hay mesas libres.");
            return;
        }

        System.out.println("Mesas disponibles:");
        for (Mesa mesa : mesasLibres) {
            System.out.printf("Mesa %d (capacidad: %d)%n", mesa.getNumero(), mesa.getCapacidad());
        }

        int numeroMesa = leerEntero("Número de mesa: ");
        Mesa mesa = mesaDAO.buscarPorNumero(mesasLibres, numeroMesa).orElse(null);
        if (mesa == null) {
            System.out.println("El número indicado no corresponde a una mesa libre.");
            return;
        }

        Pedido pedido = pedidoDAO.crear(
                pedidos, new Pedido(0, mesa, mesero, EstadoPedido.RECIBIDO));
        mesa.setEstado(EstadoMesa.OCUPADA);
        System.out.printf("Pedido #%d creado para la mesa %d.%n",
                pedido.getId(), mesa.getNumero());
    }

    private void cambiarEstadoPedido() {
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos para actualizar.");
            return;
        }

        listarPedidos();
        long pedidoId = leerEntero("ID del pedido: ");
        System.out.println("Estados disponibles:");
        EstadoPedido[] estados = EstadoPedido.values();
        for (int i = 0; i < estados.length; i++) {
            System.out.printf("%d. %s%n", i + 1, estados[i]);
        }

        int opcionEstado = leerEntero("Selecciona el nuevo estado: ");
        if (opcionEstado < 1 || opcionEstado > estados.length) {
            System.out.println("Estado no válido.");
            return;
        }

        boolean actualizado = historialDAO.registrarCambioEstado(
                historial, pedidos, pedidoId, estados[opcionEstado - 1], mesero);
        if (actualizado) {
            System.out.println("Estado actualizado y registrado en el historial.");
        } else if (pedidoDAO.buscarPorId(pedidos, pedidoId).isEmpty()) {
            System.out.println("No existe un pedido con ese ID.");
        } else {
            System.out.println("El pedido ya tiene ese estado; no se registraron cambios.");
        }
    }

    private void mostrarHistorial() {
        List<HistorialEstado> registros = historialDAO.listar(historial);
        if (registros.isEmpty()) {
            System.out.println("Todavía no hay cambios de estado registrados.");
            return;
        }

        System.out.println("\nHistorial de estados:");
        for (HistorialEstado registro : registros) {
            Usuario responsable = registro.getResponsable();
            System.out.printf("Pedido #%d | %s -> %s | %s | Responsable: %s %s%n",
                    registro.getPedido().getId(),
                    registro.getEstadoAnterior(),
                    registro.getEstadoNuevo(),
                    registro.getFechaHora(),
                    responsable.getNombre(),
                    responsable.getApellido());
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            if (!entrada.hasNextLine()) {
                return 0;
            }

            String valor = entrada.nextLine().trim();
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException excepcion) {
                System.out.println("Ingresa un número válido.");
            }
        }
    }
}
