package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.EstadoPedido;
import co.edu.poligran.paradigmas.agenda.modelo.HistorialEstado;
import co.edu.poligran.paradigmas.agenda.modelo.Pedido;
import co.edu.poligran.paradigmas.agenda.modelo.Usuario;
import co.edu.poligran.paradigmas.agenda.persistencia.HistorialEstadoDAO;

import java.util.List;

public class HistorialEstadoManager extends BaseManager<HistorialEstado> {
    private final HistorialEstadoDAO historialDAO;

    public HistorialEstadoManager() {
        this(new HistorialEstadoDAO());
    }

    private HistorialEstadoManager(HistorialEstadoDAO historialDAO) {
        super(historialDAO);
        this.historialDAO = historialDAO;
    }

    public List<HistorialEstado> buscarPorPedido(long pedidoId) {
        return historialDAO.buscarPorPedido(registros(), pedidoId);
    }

    public boolean registrarCambioEstado(PedidoManager pedidos, long pedidoId,
            EstadoPedido nuevoEstado, Usuario responsable) {
        if (pedidos == null) {
            throw new IllegalArgumentException("El manager de pedidos no puede ser null");
        }
        List<Pedido> listaPedidos = pedidos.listar();
        return historialDAO.registrarCambioEstado(
                registros(), listaPedidos, pedidoId, nuevoEstado, responsable);
    }
}
