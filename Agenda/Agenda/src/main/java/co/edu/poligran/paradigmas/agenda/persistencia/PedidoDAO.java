package co.edu.poligran.paradigmas.agenda.persistencia;

import co.edu.poligran.paradigmas.agenda.modelo.EstadoPedido;
import co.edu.poligran.paradigmas.agenda.modelo.Pedido;
import co.edu.poligran.paradigmas.agenda.modelo.RolUsuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PedidoDAO implements ICrudDAO<Pedido> {
    @Override
    public Pedido crear(List<Pedido> lista, Pedido pedido) {
        validarListaYPedido(lista, pedido);
        validarDatosPedido(pedido);
        if (pedido.getId() <= 0) {
            pedido.setId(siguienteId(lista));
        } else if (buscarPorId(lista, pedido.getId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un pedido con ese ID");
        }
        lista.add(pedido);
        return pedido;
    }

    @Override
    public Pedido actualizar(List<Pedido> lista, Pedido pedido) {
        validarListaYPedido(lista, pedido);
        validarDatosPedido(pedido);
        int indice = indicePorId(lista, pedido.getId());
        if (indice < 0) {
            throw new IllegalArgumentException("No existe un pedido con ese ID");
        }
        lista.set(indice, pedido);
        return pedido;
    }

    @Override
    public boolean eliminar(List<Pedido> lista, long id) {
        validarLista(lista);
        int indice = indicePorId(lista, id);
        if (indice < 0) {
            return false;
        }
        lista.remove(indice);
        return true;
    }

    @Override
    public Optional<Pedido> buscarPorId(List<Pedido> lista, long id) {
        validarLista(lista);
        for (Pedido pedido : lista) {
            if (pedido != null && pedido.getId() == id) {
                return Optional.of(pedido);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Pedido> listar(List<Pedido> lista) {
        validarLista(lista);
        return Collections.unmodifiableList(new ArrayList<>(lista));
    }

    public List<Pedido> listarActivos(List<Pedido> lista) {
        validarLista(lista);
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido pedido : lista) {
            if (pedido != null && esActivo(pedido.getEstado())) {
                resultado.add(pedido);
            }
        }
        return Collections.unmodifiableList(resultado);
    }

    public List<Pedido> buscarPorEstado(List<Pedido> lista, EstadoPedido estado) {
        validarLista(lista);
        if (estado == null) {
            throw new IllegalArgumentException("El estado del pedido es obligatorio");
        }
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido pedido : lista) {
            if (pedido != null && pedido.getEstado() == estado) {
                resultado.add(pedido);
            }
        }
        return Collections.unmodifiableList(resultado);
    }

    public boolean actualizarEstado(List<Pedido> lista, long id, EstadoPedido estado) {
        validarLista(lista);
        if (estado == null) {
            throw new IllegalArgumentException("El estado del pedido es obligatorio");
        }
        Optional<Pedido> pedido = buscarPorId(lista, id);
        pedido.ifPresent(valor -> valor.setEstado(estado));
        return pedido.isPresent();
    }

    public long siguienteId(List<Pedido> lista) {
        validarLista(lista);
        long maximo = 0;
        for (Pedido pedido : lista) {
            if (pedido != null && pedido.getId() > maximo) {
                maximo = pedido.getId();
            }
        }
        return maximo + 1;
    }

    private boolean esActivo(EstadoPedido estado) {
        return estado != null && estado != EstadoPedido.ENTREGADO && estado != EstadoPedido.CANCELADO;
    }

    private int indicePorId(List<Pedido> lista, long id) {
        for (int indice = 0; indice < lista.size(); indice++) {
            Pedido pedido = lista.get(indice);
            if (pedido != null && pedido.getId() == id) {
                return indice;
            }
        }
        return -1;
    }

    private void validarListaYPedido(List<Pedido> lista, Pedido pedido) {
        validarLista(lista);
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser null");
        }
    }

    private void validarLista(List<Pedido> lista) {
        if (lista == null) {
            throw new IllegalArgumentException("La lista no puede ser null");
        }
    }

    private void validarDatosPedido(Pedido pedido) {
        if (pedido.getMesa() == null) {
            throw new IllegalArgumentException("La mesa del pedido es obligatoria");
        }
        if (pedido.getMesero() == null || pedido.getMesero().getRol() != RolUsuario.MESERO) {
            throw new IllegalArgumentException("El usuario asociado debe tener el rol de mesero");
        }
        if (!pedido.getMesero().isActivo()) {
            throw new IllegalArgumentException("El mesero debe estar activo");
        }
        if (pedido.getEstado() == null) {
            throw new IllegalArgumentException("El estado del pedido es obligatorio");
        }
    }
}