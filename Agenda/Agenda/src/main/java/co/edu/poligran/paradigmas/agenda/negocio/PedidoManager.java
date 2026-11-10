package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.EstadoMesa;
import co.edu.poligran.paradigmas.agenda.modelo.EstadoPedido;
import co.edu.poligran.paradigmas.agenda.modelo.Mesa;
import co.edu.poligran.paradigmas.agenda.modelo.Pedido;
import co.edu.poligran.paradigmas.agenda.persistencia.PedidoDAO;

import java.util.List;
import java.util.Optional;

public class PedidoManager extends BaseManager<Pedido> {
    private final PedidoDAO pedidoDAO;
    private final MesaManager mesaManager;

    public PedidoManager() {
        this(new PedidoDAO(), new MesaManager());
    }

    public PedidoManager(MesaManager mesaManager) {
        this(new PedidoDAO(), mesaManager);
    }

    private PedidoManager(PedidoDAO pedidoDAO, MesaManager mesaManager) {
        super(pedidoDAO);
        this.pedidoDAO = pedidoDAO;
        this.mesaManager = mesaManager == null ? new MesaManager() : mesaManager;
    }

    public List<Pedido> listarActivos() {
        return pedidoDAO.listarActivos(registros());
    }

    public List<Pedido> buscarPorEstado(EstadoPedido estado) {
        return pedidoDAO.buscarPorEstado(registros(), estado);
    }

    public List<Pedido> listarPedidosCocina() {
        return pedidoDAO.listarPendientesYEnPreparacion(registros());
    }

    public boolean actualizarEstado(long id, EstadoPedido estado) {
        return pedidoDAO.actualizarEstado(registros(), id, estado);
    }

    public boolean transferirPedido(int idPedido, int nuevaMesaId) {
        long pedidoId = idPedido;
        long mesaDestinoId = nuevaMesaId;

        if (pedidoId <= 0 || mesaDestinoId <= 0) {
            throw new IllegalArgumentException("El pedido y la nueva mesa deben ser válidos");
        }

        Optional<Pedido> pedidoOpt = buscarPorId(pedidoId);
        if (!pedidoOpt.isPresent()) {
            return false;
        }

        Pedido pedido = pedidoOpt.get();
        if (pedido.getEstado() == null || pedido.getEstado() == EstadoPedido.CANCELADO
                || pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new IllegalArgumentException("Solo se pueden transferir pedidos activos");
        }

        Mesa nuevaMesa = mesaManager.buscarPorId(mesaDestinoId)
                .orElseThrow(() -> new IllegalArgumentException("La mesa destino no existe"));

        if (nuevaMesa.getEstado() != EstadoMesa.LIBRE) {
            throw new IllegalArgumentException("La nueva mesa no está libre");
        }

        return pedidoDAO.actualizarMesaPedido(registros(), pedido.getId(), nuevaMesa.getId());
    }
}
