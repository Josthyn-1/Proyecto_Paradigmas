package co.edu.poligran.paradigmas.agenda.persistencia;

import co.edu.poligran.paradigmas.agenda.modelo.EstadoPedido;
import co.edu.poligran.paradigmas.agenda.modelo.HistorialEstado;
import co.edu.poligran.paradigmas.agenda.modelo.Pedido;
import co.edu.poligran.paradigmas.agenda.modelo.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class HistorialEstadoDAO implements ICrudDAO<HistorialEstado> {
    @Override
    public HistorialEstado crear(List<HistorialEstado> lista, HistorialEstado historial) {
        validarListaYRegistro(lista, historial);
        validarDatos(historial);
        if (historial.getId() <= 0) {
            historial.setId(siguienteId(lista));
        } else if (buscarPorId(lista, historial.getId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un registro de historial con ese ID");
        }
        lista.add(historial);
        return historial;
    }

    @Override
    public HistorialEstado actualizar(List<HistorialEstado> lista, HistorialEstado historial) {
        validarLista(lista);
        throw new UnsupportedOperationException("Los registros de auditoría no se pueden modificar");
    }

    @Override
    public boolean eliminar(List<HistorialEstado> lista, long id) {
        validarLista(lista);
        return false;
    }

    @Override
    public Optional<HistorialEstado> buscarPorId(List<HistorialEstado> lista, long id) {
        validarLista(lista);
        for (HistorialEstado historial : lista) {
            if (historial != null && historial.getId() == id) {
                return Optional.of(historial);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<HistorialEstado> listar(List<HistorialEstado> lista) {
        validarLista(lista);
        List<HistorialEstado> resultado = new ArrayList<>(lista);
        resultado.sort(Comparator.comparing(HistorialEstado::getFechaHora,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return Collections.unmodifiableList(resultado);
    }

    public List<HistorialEstado> buscarPorPedido(List<HistorialEstado> lista, long pedidoId) {
        validarLista(lista);
        List<HistorialEstado> resultado = new ArrayList<>();
        for (HistorialEstado historial : lista) {
            if (historial != null && historial.getPedido() != null
                    && historial.getPedido().getId() == pedidoId) {
                resultado.add(historial);
            }
        }
        resultado.sort(Comparator.comparing(HistorialEstado::getFechaHora));
        return Collections.unmodifiableList(resultado);
    }

    public boolean registrarCambioEstado(List<HistorialEstado> historial, List<Pedido> pedidos,
            long pedidoId, EstadoPedido nuevoEstado, Usuario responsable) {
        validarLista(historial);
        if (pedidos == null) {
            throw new IllegalArgumentException("La lista de pedidos no puede ser null");
        }
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado es obligatorio");
        }
        if (responsable == null) {
            throw new IllegalArgumentException("El usuario responsable es obligatorio");
        }

        Pedido pedido = buscarPedido(pedidos, pedidoId);
        if (pedido == null || pedido.getEstado() == nuevoEstado) {
            return false;
        }

        HistorialEstado registro = new HistorialEstado(0, pedido, pedido.getEstado(),
                nuevoEstado, responsable);
        crear(historial, registro);
        pedido.setEstado(nuevoEstado);
        return true;
    }

    public long siguienteId(List<HistorialEstado> lista) {
        validarLista(lista);
        long maximo = 0;
        for (HistorialEstado historial : lista) {
            if (historial != null && historial.getId() > maximo) {
                maximo = historial.getId();
            }
        }
        return maximo + 1;
    }

    private Pedido buscarPedido(List<Pedido> pedidos, long id) {
        for (Pedido pedido : pedidos) {
            if (pedido != null && pedido.getId() == id) {
                return pedido;
            }
        }
        return null;
    }

    private void validarListaYRegistro(List<HistorialEstado> lista, HistorialEstado historial) {
        validarLista(lista);
        if (historial == null) {
            throw new IllegalArgumentException("El registro de historial no puede ser null");
        }
    }

    private void validarLista(List<HistorialEstado> lista) {
        if (lista == null) {
            throw new IllegalArgumentException("La lista de historial no puede ser null");
        }
    }

    private void validarDatos(HistorialEstado historial) {
        if (historial.getPedido() == null) {
            throw new IllegalArgumentException("El pedido asociado es obligatorio");
        }
        if (historial.getEstadoAnterior() == null || historial.getEstadoNuevo() == null) {
            throw new IllegalArgumentException("Los estados anterior y nuevo son obligatorios");
        }
        if (historial.getFechaHora() == null) {
            throw new IllegalArgumentException("La fecha y hora del registro son obligatorias");
        }
        if (historial.getResponsable() == null) {
            throw new IllegalArgumentException("El usuario responsable es obligatorio");
        }
    }
}