package co.edu.poligran.paradigmas.agenda.modelo;

import java.time.Instant;

public class HistorialEstado {
    private long id;
    private final Pedido pedido;
    private final EstadoPedido estadoAnterior;
    private final EstadoPedido estadoNuevo;
    private final Instant fechaHora;
    private final Usuario responsable;

    public HistorialEstado(long id, Pedido pedido, EstadoPedido estadoAnterior,
            EstadoPedido estadoNuevo, Usuario responsable) {
        this(id, pedido, estadoAnterior, estadoNuevo, Instant.now(), responsable);
    }

    public HistorialEstado(long id, Pedido pedido, EstadoPedido estadoAnterior,
            EstadoPedido estadoNuevo, Instant fechaHora, Usuario responsable) {
        this.id = id;
        this.pedido = pedido;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaHora = fechaHora;
        this.responsable = responsable;
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

    public EstadoPedido getEstadoAnterior() {
        return estadoAnterior;
    }

    public EstadoPedido getEstadoNuevo() {
        return estadoNuevo;
    }

    public Instant getFechaHora() {
        return fechaHora;
    }

    public Usuario getResponsable() {
        return responsable;
    }
}