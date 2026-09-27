package co.edu.poligran.paradigmas.agenda.modelo;

public class Pedido {
    private long id;
    private final Mesa mesa;
    private final Usuario mesero;
    private EstadoPedido estado;

    public Pedido(long id, Mesa mesa, Usuario mesero, EstadoPedido estado) {
        this.id = id;
        this.mesa = mesa;
        this.mesero = mesero;
        this.estado = estado;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public Usuario getMesero() {
        return mesero;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }
}