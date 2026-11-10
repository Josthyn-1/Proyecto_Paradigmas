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
import java.util.ArrayList;
import java.util.List;

class ProductoManagerReporteTest {

    @Test
    void debeDevolverProductoMasVendidoPorCantidad() {
        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO();
        String sql = itemPedidoDAO.sqlProductoMasVendido();
        Assertions.assertTrue(sql.toUpperCase().contains("GROUP BY"));
        Assertions.assertTrue(sql.toUpperCase().contains("SUM(CANTIDAD)"));
        Assertions.assertTrue(sql.toUpperCase().contains("ORDER BY"));
        Assertions.assertTrue(sql.toUpperCase().contains("LIMIT 1"));

        Usuario mesero = new Usuario(1, "Ana", "Pérez", "ana", RolUsuario.MESERO, true);
        Mesa mesa = new Mesa(1, 2, 4, EstadoMesa.OCUPADA);
        Pedido pedido = new Pedido(10, mesa, mesero, EstadoPedido.RECIBIDO);

        Categoria categoria = new Categoria(1, "Platos", true);
        Producto producto1 = new Producto(1, "Hamburguesa", categoria, new BigDecimal("12000"), true, "");
        Producto producto2 = new Producto(2, "Pizza", categoria, new BigDecimal("18000"), true, "");

        List<ItemPedido> items = new ArrayList<>();
        items.add(new ItemPedido(1, pedido, producto1, 3, "sin cebolla"));
        items.add(new ItemPedido(2, pedido, producto2, 1, "bien cocida"));
        items.add(new ItemPedido(3, pedido, producto1, 2, "sin queso"));

        ProductoManager manager = new ProductoManager();
        Producto masVendido = manager.obtenerProductoMasVendido(items).orElseThrow();

        Assertions.assertEquals(1, masVendido.getId());
    }
}
