package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.EstadoPedido;
import co.edu.poligran.paradigmas.agenda.modelo.Pedido;
import co.edu.poligran.paradigmas.agenda.persistencia.PedidoDAO;

import java.util.List;

public class PedidoManager extends BaseManager<Pedido> {
    private final PedidoDAO pedidoDAO;

    public PedidoManager() {
        this(new PedidoDAO());
    }

    private PedidoManager(PedidoDAO pedidoDAO) {
        super(pedidoDAO);
        this.pedidoDAO = pedidoDAO;
    }

    public List<Pedido> listarActivos() {
        return pedidoDAO.listarActivos(registros());
    }

    public List<Pedido> buscarPorEstado(EstadoPedido estado) {
        return pedidoDAO.buscarPorEstado(registros(), estado);
    }

    public boolean actualizarEstado(long id, EstadoPedido estado) {
        return pedidoDAO.actualizarEstado(registros(), id, estado);
    }
}
