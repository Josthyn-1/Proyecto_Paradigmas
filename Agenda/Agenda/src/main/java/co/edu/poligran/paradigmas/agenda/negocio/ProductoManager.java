package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.ItemPedido;
import co.edu.poligran.paradigmas.agenda.modelo.Producto;
import co.edu.poligran.paradigmas.agenda.persistencia.ItemPedidoDAO;
import co.edu.poligran.paradigmas.agenda.persistencia.ProductoDAO;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductoManager extends BaseManager<Producto> {
    private final ProductoDAO productoDAO;
    private final ItemPedidoDAO itemPedidoDAO;

    public ProductoManager() {
        this(new ProductoDAO(), new ItemPedidoDAO());
    }

    private ProductoManager(ProductoDAO productoDAO, ItemPedidoDAO itemPedidoDAO) {
        super(productoDAO);
        this.productoDAO = productoDAO;
        this.itemPedidoDAO = itemPedidoDAO;
    }

    public List<Producto> buscarPorCategoria(long categoriaId) {
        return productoDAO.buscarPorCategoria(registros(), categoriaId);
    }

    public List<Producto> listarDisponibles() {
        return productoDAO.listarDisponibles(registros());
    }

    public boolean actualizarPrecio(long id, BigDecimal precio) {
        return productoDAO.actualizarPrecio(registros(), id, precio);
    }

    public boolean actualizarDisponibilidad(long id, boolean disponible) {
        return productoDAO.actualizarDisponibilidad(registros(), id, disponible);
    }

    public boolean actualizarDescripcion(long id, String descripcion) {
        return productoDAO.actualizarDescripcion(registros(), id, descripcion);
    }

    public Optional<Producto> obtenerProductoMasVendido(List<ItemPedido> items) {
        if (items == null) {
            throw new IllegalArgumentException("La lista de items no puede ser null");
        }

        Map<Long, Integer> cantidadesPorProducto = new HashMap<>();
        for (ItemPedido item : items) {
            if (item == null || item.getProducto() == null) {
                continue;
            }
            long idProducto = item.getProducto().getId();
            int cantidad = item.getCantidad();
            cantidadesPorProducto.put(idProducto, cantidadesPorProducto.getOrDefault(idProducto, 0) + cantidad);
        }

        if (cantidadesPorProducto.isEmpty()) {
            return Optional.empty();
        }

        long productoIdMasVendido = -1;
        int mayorCantidad = Integer.MIN_VALUE;
        for (Map.Entry<Long, Integer> entrada : cantidadesPorProducto.entrySet()) {
            if (entrada.getValue() > mayorCantidad) {
                mayorCantidad = entrada.getValue();
                productoIdMasVendido = entrada.getKey();
            }
        }

        Optional<Producto> producto = buscarPorId(productoIdMasVendido);
        if (producto.isPresent()) {
            return producto;
        }

        for (ItemPedido item : items) {
            if (item != null && item.getProducto() != null && item.getProducto().getId() == productoIdMasVendido) {
                return Optional.of(item.getProducto());
            }
        }

        return Optional.empty();
    }

    public String sqlProductoMasVendido() {
        return itemPedidoDAO.sqlProductoMasVendido();
    }
}
