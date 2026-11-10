package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.Categoria;
import co.edu.poligran.paradigmas.agenda.modelo.EstadoMesa;
import co.edu.poligran.paradigmas.agenda.modelo.EstadoPedido;
import co.edu.poligran.paradigmas.agenda.modelo.ItemPedido;
import co.edu.poligran.paradigmas.agenda.modelo.Mesa;
import co.edu.poligran.paradigmas.agenda.modelo.Pedido;
import co.edu.poligran.paradigmas.agenda.modelo.Producto;
import co.edu.poligran.paradigmas.agenda.modelo.RolUsuario;
import co.edu.poligran.paradigmas.agenda.modelo.Usuario;
import co.edu.poligran.paradigmas.agenda.persistencia.ItemPedidoDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class ItemPedidoManagerTest {

    @Test
    void debeGuardarObservacionAlAgregarItemAlPedido() {
        Mesa mesa = new Mesa(1, 2, 4, EstadoMesa.OCUPADA);
        Usuario mesero = new Usuario(1, "Ana", "Pérez", "ana", RolUsuario.MESERO, true);
        Pedido pedido = new Pedido(10, mesa, mesero, EstadoPedido.RECIBIDO);
        Producto producto = new Producto(20, "Hamburguesa", new Categoria(1, "Platos", true), new BigDecimal("12000"), true, "");

        ItemPedidoDAO dao = new ItemPedidoDAO();
        Assertions.assertTrue(dao.sqlInsertItemPedido().contains("observaciones"));
        Assertions.assertTrue(dao.sqlSelectItemsByPedido().contains("observaciones"));

        ItemPedidoManager manager = new ItemPedidoManager();
        ItemPedido item = manager.agregarItemConObservacion(pedido, producto, 2, " sin cebolla ");

        Assertions.assertNotNull(item);
        Assertions.assertEquals("sin cebolla", item.getObservaciones());
        Assertions.assertEquals("sin cebolla", item.getNotas());
        Assertions.assertEquals(2, item.getCantidad());
        Assertions.assertEquals(1, manager.buscarPorPedido(10).size());
    }

    @Test
    void noDebePermitirCantidadMenorAUnoNiEstadoPagadoComoActivo() {
        Mesa mesa = new Mesa(1, 2, 4, EstadoMesa.OCUPADA);
        Usuario mesero = new Usuario(1, "Ana", "Pérez", "ana", RolUsuario.MESERO, true);
        Pedido pedido = new Pedido(10, mesa, mesero, EstadoPedido.PAGADO);
        Producto producto = new Producto(20, "Hamburguesa", new Categoria(1, "Platos", true), new BigDecimal("12000"), true, "");

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido(1, pedido, producto, 0, ""));
        Assertions.assertTrue(ex.getMessage().contains("mayor que"));

        Assertions.assertEquals(EstadoPedido.PAGADO, pedido.getEstado());
    }
}
