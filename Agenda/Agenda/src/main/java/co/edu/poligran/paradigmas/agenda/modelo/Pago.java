package co.edu.poligran.paradigmas.agenda.modelo;

import java.math.BigDecimal;
import java.time.Instant;

public abstract class Pago {
    private long id;
    private final Factura factura;
    private final BigDecimal monto;
    private final Instant fechaHora;

    protected Pago(long id, Factura factura, BigDecimal monto, Instant fechaHora) {
        this.id = id;
        this.factura = factura;
        this.monto = monto;
        this.fechaHora = fechaHora;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Factura getFactura() {
        return factura;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public Instant getFechaHora() {
        return fechaHora;
    }
}