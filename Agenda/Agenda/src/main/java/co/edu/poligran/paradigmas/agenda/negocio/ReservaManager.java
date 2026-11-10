package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.EstadoReserva;
import co.edu.poligran.paradigmas.agenda.modelo.Reserva;
import co.edu.poligran.paradigmas.agenda.persistencia.ReservaDAO;

import java.time.Instant;

public class ReservaManager extends BaseManager<Reserva> {
    private final ReservaDAO reservaDAO;

    public ReservaManager() {
        this(new ReservaDAO());
    }

    private ReservaManager(ReservaDAO reservaDAO) {
        super(reservaDAO);
        this.reservaDAO = reservaDAO;
    }

    @Override
    public Reserva crear(Reserva entidad) {
        validarDisponibilidad(entidad, -1);
        if (entidad.getEstado() == null) {
            entidad.setEstado(EstadoReserva.PENDIENTE);
        }
        return super.crear(entidad);
    }

    @Override
    public Reserva actualizar(Reserva entidad) {
        validarDisponibilidad(entidad, entidad.getIdReserva());
        if (entidad.getEstado() == null) {
            entidad.setEstado(EstadoReserva.PENDIENTE);
        }
        return super.actualizar(entidad);
    }

    public boolean existeSolapamiento(long idMesa, Instant fechaHoraReserva) {
        return reservaDAO.existeSolapamiento(registros(), idMesa, fechaHoraReserva, -1);
    }

    public boolean existeSolapamiento(long idMesa, Instant fechaHoraReserva, long idReservaIgnorada) {
        return reservaDAO.existeSolapamiento(registros(), idMesa, fechaHoraReserva, idReservaIgnorada);
    }

    private void validarDisponibilidad(Reserva reserva, long idReservaIgnorada) {
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva no puede ser null");
        }
        if (reserva.getIdMesa() <= 0) {
            throw new IllegalArgumentException("La mesa de la reserva es obligatoria");
        }
        if (reserva.getFechaHoraReserva() == null) {
            throw new IllegalArgumentException("La fecha y hora de la reserva es obligatoria");
        }
        if (reservaDAO.existeSolapamiento(registros(), reserva.getIdMesa(), reserva.getFechaHoraReserva(), idReservaIgnorada)) {
            throw new IllegalArgumentException("Ya existe un solapamiento de horario para la misma mesa");
        }
    }
}
