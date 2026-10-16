package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.ItemPedido;
import co.edu.poligran.paradigmas.agenda.persistencia.ItemPedidoDAO;

import java.util.List;

public class ItemPedidoManager extends BaseManager<ItemPedido> {
    private final ItemPedidoDAO itemPedidoDAO;

    public ItemPedidoManager() {
        this(new ItemPedidoDAO());
    }

    private ItemPedidoManager(ItemPedidoDAO itemPedidoDAO) {
        super(itemPedidoDAO);
        this.itemPedidoDAO = itemPedidoDAO;
    }

    public List<ItemPedido> buscarPorPedido(long pedidoId) {
        return itemPedidoDAO.buscarPorPedido(registros(), pedidoId);
    }

    public boolean actualizarCantidad(long id, int cantidad) {
        return itemPedidoDAO.actualizarCantidad(registros(), id, cantidad);
    }

    public boolean actualizarNotas(long id, String notas) {
        return itemPedidoDAO.actualizarNotas(registros(), id, notas);
    }
}
