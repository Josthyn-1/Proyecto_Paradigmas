package co.edu.poligran.paradigmas.agenda.modelo;

import java.math.BigDecimal;
import java.time.Instant;

public class Factura {
    private long id;
    private final Pedido pedido;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal propinas;
    private double descuento;
    private double totalConDescuento;
    private final Instant fechaHora;

    public Factura(long id, Pedido pedido, BigDecimal subtotal, BigDecimal impuestos,
            BigDecimal propinas) {
        this(id, pedido, subtotal, impuestos, propinas, 0.0, 0.0, Instant.now());
    }

    public Factura(long id, Pedido pedido, BigDecimal subtotal, BigDecimal impuestos,
            BigDecimal propinas, double descuento, double totalConDescuento) {
        this(id, pedido, subtotal, impuestos, propinas, descuento, totalConDescuento, Instant.now());
    }

    public Factura(long id, Pedido pedido, BigDecimal subtotal, BigDecimal impuestos,
            BigDecimal propinas, Instant fechaHora) {
        this(id, pedido, subtotal, impuestos, propinas, 0.0, 0.0, fechaHora);
    }

    public Factura(long id, Pedido pedido, BigDecimal subtotal, BigDecimal impuestos,
            BigDecimal propinas, double descuento, double totalConDescuento, Instant fechaHora) {
        this.id = id;
        this.pedido = pedido;
        this.subtotal = subtotal;
        this.impuestos = impuestos;
        this.propinas = propinas;
        this.descuento = validarDescuento(descuento);
        this.totalConDescuento = totalConDescuento;
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

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = validarDescuento(descuento);
    }

    public double getTotalConDescuento() {
        return totalConDescuento;
    }

    public void setTotalConDescuento(double totalConDescuento) {
        this.totalConDescuento = totalConDescuento;
    }

    public Instant getFechaHora() {
        return fechaHora;
    }

    public BigDecimal getTotal() {
        return subtotal.add(impuestos).add(propinas);
    }

    public BigDecimal calcularTotalConDescuento() {
        BigDecimal total = getTotal();
        if (descuento <= 0) {
            return total;
        }
        BigDecimal porcentaje = BigDecimal.valueOf(descuento).divide(BigDecimal.valueOf(100), 10, java.math.RoundingMode.HALF_UP);
        BigDecimal descuentoMonto = total.multiply(porcentaje);
        return total.subtract(descuentoMonto);
    }

    private double validarDescuento(double valor) {
        if (valor < 0 || valor > 100) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 100");
        }
        return valor;
    }
}