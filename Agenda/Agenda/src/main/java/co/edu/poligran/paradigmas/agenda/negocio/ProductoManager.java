package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.Producto;
import co.edu.poligran.paradigmas.agenda.persistencia.ProductoDAO;

import java.math.BigDecimal;
import java.util.List;

public class ProductoManager extends BaseManager<Producto> {
    private final ProductoDAO productoDAO;

    public ProductoManager() {
        this(new ProductoDAO());
    }

    private ProductoManager(ProductoDAO productoDAO) {
        super(productoDAO);
        this.productoDAO = productoDAO;
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
}
