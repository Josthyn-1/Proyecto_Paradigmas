package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.ItemPedido;
import co.edu.poligran.paradigmas.agenda.modelo.Pedido;
import co.edu.poligran.paradigmas.agenda.modelo.Producto;
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

    public boolean actualizarObservaciones(long id, String observaciones) {
        return itemPedidoDAO.actualizarObservaciones(registros(), id, observaciones);
    }

    public ItemPedido agregarItemConObservacion(Pedido pedido, Producto producto, int cantidad, String observaciones) {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido asociado es obligatorio");
        }
        if (producto == null) {
            throw new IllegalArgumentException("El producto es obligatorio");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        String observacionNormalizada = observaciones == null ? "" : observaciones.trim();
        ItemPedido item = new ItemPedido(0, pedido, producto, cantidad, observacionNormalizada);
        return crear(item);
    }

    public ItemPedido procesarItemPedido(Pedido pedido, Producto producto, int cantidad, String observaciones) {
        return agregarItemConObservacion(pedido, producto, cantidad, observaciones);
    }
}
