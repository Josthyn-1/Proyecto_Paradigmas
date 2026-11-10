package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.EstadoMesa;
import co.edu.poligran.paradigmas.agenda.modelo.EstadoPedido;
import co.edu.poligran.paradigmas.agenda.modelo.Mesa;
import co.edu.poligran.paradigmas.agenda.modelo.Pedido;
import co.edu.poligran.paradigmas.agenda.modelo.RolUsuario;
import co.edu.poligran.paradigmas.agenda.modelo.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PedidoManagerTransferenciaTest {

    @Test
    void debeTransferirPedidoCuandoLaNuevaMesaEsteLibre() {
        MesaManager mesaManager = new MesaManager();
        PedidoManager manager = new PedidoManager(mesaManager);

        Mesa mesaActual = new Mesa(1, 3, 4, EstadoMesa.OCUPADA);
        Mesa nuevaMesa = new Mesa(2, 5, 4, EstadoMesa.LIBRE);
        Usuario mesero = new Usuario(1, "Ana", "Pérez", "ana", RolUsuario.MESERO, true);
        Pedido pedido = new Pedido(10, mesaActual, mesero, EstadoPedido.RECIBIDO);

        mesaManager.crear(mesaActual);
        mesaManager.crear(nuevaMesa);
        manager.crear(pedido);

        boolean transferido = manager.transferirPedido(10, 2);

        Assertions.assertTrue(transferido);
        Assertions.assertEquals(2, manager.buscarPorId(10).orElseThrow().getMesa().getId());
    }
}
