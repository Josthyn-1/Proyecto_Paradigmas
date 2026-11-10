package co.edu.poligran.paradigmas.agenda.modelo;

import java.math.BigDecimal;

public class ItemPedido {
    private long id;
    private final Pedido pedido;
    private final Producto producto;
    private int cantidad;
    private final BigDecimal precioUnitarioHistorico;
    private String observaciones;

    public ItemPedido(long id, Pedido pedido, Producto producto, int cantidad, String observaciones) {
        this.id = id;
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitarioHistorico = producto == null ? null : producto.getPrecio();
        this.observaciones = normalizarObservaciones(observaciones);
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

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitarioHistorico() {
        return precioUnitarioHistorico;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = normalizarObservaciones(observaciones);
    }

    public String getNotas() {
        return getObservaciones();
    }

    public void setNotas(String notas) {
        setObservaciones(notas);
    }

    private String normalizarObservaciones(String valor) {
        return valor == null ? "" : valor.trim();
    }
}