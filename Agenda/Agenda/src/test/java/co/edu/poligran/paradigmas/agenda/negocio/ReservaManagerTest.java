package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.EstadoReserva;
import co.edu.poligran.paradigmas.agenda.modelo.Reserva;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class ReservaManagerTest {

    @Test
    void noDebePermitirReservasSolapadasEnLaMismaMesa() {
        ReservaManager manager = new ReservaManager();

        Instant horario = Instant.parse("2026-11-10T19:00:00Z");

        Reserva reserva1 = new Reserva(0, 1, "Ana", "3001112233", horario, EstadoReserva.PENDIENTE);
        Reserva reserva2 = new Reserva(0, 1, "Luis", "3001114455", horario, EstadoReserva.PENDIENTE);

        manager.crear(reserva1);

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> manager.crear(reserva2)
        );

        Assertions.assertTrue(exception.getMessage().contains("solapamiento") ||
                exception.getMessage().contains("horario"));
    }
}
