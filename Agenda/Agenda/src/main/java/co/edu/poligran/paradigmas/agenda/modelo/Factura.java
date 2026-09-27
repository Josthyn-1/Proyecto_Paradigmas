package co.edu.poligran.paradigmas.agenda.modelo;

import java.math.BigDecimal;
import java.time.Instant;

public class Factura {
    private long id;
    private final Pedido pedido;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal propinas;
    private final Instant fechaHora;

    public Factura(long id, Pedido pedido, BigDecimal subtotal, BigDecimal impuestos,
            BigDecimal propinas) {
        this(id, pedido, subtotal, impuestos, propinas, Instant.now());
    }

    public Factura(long id, Pedido pedido, BigDecimal subtotal, BigDecimal impuestos,
            BigDecimal propinas, Instant fechaHora) {
        this.id = id;
        this.pedido = pedido;
        this.subtotal = subtotal;
        this.impuestos = impuestos;
        this.propinas = propinas;
        this.fechaHora = fechaHora;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(BigDecimal impuestos) {
        this.impuestos = impuestos;
    }

    public BigDecimal getPropinas() {
        return propinas;
    }

    public void setPropinas(BigDecimal propinas) {
        this.propinas = propinas;
    }

    public Instant getFechaHora() {
        return fechaHora;
    }

    public BigDecimal getTotal() {
        return subtotal.add(impuestos).add(propinas);
    }
}