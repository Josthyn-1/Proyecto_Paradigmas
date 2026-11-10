package co.edu.poligran.paradigmas.agenda.persistencia;

import co.edu.poligran.paradigmas.agenda.modelo.EstadoReserva;
import co.edu.poligran.paradigmas.agenda.modelo.Reserva;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ReservaDAO implements ICrudDAO<Reserva> {
    @Override
    public Reserva crear(List<Reserva> lista, Reserva reserva) {
        validarListaYReserva(lista, reserva);
        validarReserva(reserva);

        if (reserva.getIdReserva() <= 0) {
            reserva.setIdReserva(siguienteId(lista));
        } else if (buscarPorId(lista, reserva.getIdReserva()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una reserva con ese ID");
        }

        lista.add(reserva);
        return reserva;
    }

    @Override
    public Reserva actualizar(List<Reserva> lista, Reserva reserva) {
        validarListaYReserva(lista, reserva);
        validarReserva(reserva);

        int indice = indicePorId(lista, reserva.getIdReserva());
        if (indice < 0) {
            throw new IllegalArgumentException("No existe una reserva con ese ID");
        }

        lista.set(indice, reserva);
        return reserva;
    }

    @Override
    public boolean eliminar(List<Reserva> lista, long id) {
        validarLista(lista);
        int indice = indicePorId(lista, id);
        if (indice < 0) {
            return false;
        }
        lista.remove(indice);
        return true;
    }

    @Override
    public Optional<Reserva> buscarPorId(List<Reserva> lista, long id) {
        validarLista(lista);
        for (Reserva reserva : lista) {
            if (reserva != null && reserva.getIdReserva() == id) {
                return Optional.of(reserva);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Reserva> listar(List<Reserva> lista) {
        validarLista(lista);
        return Collections.unmodifiableList(new ArrayList<>(lista));
    }

    public List<Reserva> listarPorMesa(List<Reserva> lista, long idMesa) {
        validarLista(lista);
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva reserva : lista) {
            if (reserva != null && reserva.getIdMesa() == idMesa) {
                resultado.add(reserva);
            }
        }
        return Collections.unmodifiableList(resultado);
    }

    public Optional<Reserva> buscarPorMesaYHorario(List<Reserva> lista, long idMesa, Instant fechaHoraReserva) {
        validarLista(lista);
        for (Reserva reserva : lista) {
            if (reserva != null
                    && reserva.getIdMesa() == idMesa
                    && reserva.getFechaHoraReserva() != null
                    && reserva.getFechaHoraReserva().equals(fechaHoraReserva)) {
                return Optional.of(reserva);
            }
        }
        return Optional.empty();
    }

    public boolean existeSolapamiento(List<Reserva> lista, long idMesa, Instant fechaHoraReserva, long idIgnorado) {
        validarLista(lista);
        if (idMesa <= 0 || fechaHoraReserva == null) {
            return false;
        }

        for (Reserva reserva : lista) {
            if (reserva == null
                    || reserva.getIdReserva() == idIgnorado
                    || reserva.getIdMesa() != idMesa
                    || reserva.getFechaHoraReserva() == null
                    || !reserva.getFechaHoraReserva().equals(fechaHoraReserva)
                    || reserva.getEstado() == EstadoReserva.CANCELADA) {
                continue;
            }
            return true;
        }
        return false;
    }

    public long siguienteId(List<Reserva> lista) {
        validarLista(lista);
        long maximo = 0;
        for (Reserva reserva : lista) {
            if (reserva != null && reserva.getIdReserva() > maximo) {
                maximo = reserva.getIdReserva();
            }
        }
        return maximo + 1;
    }

    private int indicePorId(List<Reserva> lista, long id) {
        for (int indice = 0; indice < lista.size(); indice++) {
            Reserva reserva = lista.get(indice);
            if (reserva != null && reserva.getIdReserva() == id) {
                return indice;
            }
        }
        return -1;
    }

    private void validarListaYReserva(List<Reserva> lista, Reserva reserva) {
        validarLista(lista);
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva no puede ser null");
        }
    }

    private void validarLista(List<Reserva> lista) {
        if (lista == null) {
            throw new IllegalArgumentException("La lista no puede ser null");
        }
    }

    private void validarReserva(Reserva reserva) {
        if (reserva.getIdMesa() <= 0) {
            throw new IllegalArgumentException("La mesa de la reserva es obligatoria");
        }
        if (reserva.getNombreCliente() == null || reserva.getNombreCliente().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
        if (reserva.getTelefonoCliente() == null || reserva.getTelefonoCliente().trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono del cliente es obligatorio");
        }
        if (reserva.getFechaHoraReserva() == null) {
            throw new IllegalArgumentException("La fecha y hora de la reserva es obligatoria");
        }
        if (reserva.getEstado() == null) {
            reserva.setEstado(EstadoReserva.PENDIENTE);
        }
    }
}
