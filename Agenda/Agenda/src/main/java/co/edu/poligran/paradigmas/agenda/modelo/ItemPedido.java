package co.edu.poligran.paradigmas.agenda.modelo;

import java.math.BigDecimal;

public class ItemPedido {
    private long id;
    private final Pedido pedido;
    private final Producto producto;
    private int cantidad;
    private final BigDecimal precioUnitarioHistorico;
    private String notas;

    public ItemPedido(long id, Pedido pedido, Producto producto, int cantidad, String notas) {
        this.id = id;
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitarioHistorico = producto == null ? null : producto.getPrecio();
        this.notas = notas == null ? "" : notas;
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

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas == null ? "" : notas;
    }
}