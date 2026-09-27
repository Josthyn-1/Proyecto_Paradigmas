package co.edu.poligran.paradigmas.agenda.modelo;

import java.math.BigDecimal;
import java.time.Instant;

public class PagoEfectivo extends Pago {
    private final BigDecimal montoRecibido;
    private final BigDecimal cambio;

    public PagoEfectivo(long id, Factura factura, BigDecimal montoRecibido, BigDecimal cambio) {
        this(id, factura, montoRecibido, cambio, Instant.now());
    }

    public PagoEfectivo(long id, Factura factura, BigDecimal montoRecibido,
            BigDecimal cambio, Instant fechaHora) {
        super(id, factura, calcularMontoPagado(montoRecibido, cambio), fechaHora);
        this.montoRecibido = montoRecibido;
        this.cambio = cambio;
    }

    public BigDecimal getMontoRecibido() {
        return montoRecibido;
    }

    public BigDecimal getCambio() {
        return cambio;
    }

    private static BigDecimal calcularMontoPagado(BigDecimal montoRecibido, BigDecimal cambio) {
        if (montoRecibido == null || cambio == null) {
            throw new IllegalArgumentException("El monto recibido y el cambio son obligatorios");
        }
        if (montoRecibido.compareTo(BigDecimal.ZERO) <= 0
                || cambio.compareTo(BigDecimal.ZERO) < 0
                || cambio.compareTo(montoRecibido) >= 0) {
            throw new IllegalArgumentException("El efectivo recibido y el cambio no son válidos");
        }
        return montoRecibido.subtract(cambio);
    }
}