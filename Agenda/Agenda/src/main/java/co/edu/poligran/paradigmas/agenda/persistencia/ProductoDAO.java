package co.edu.poligran.paradigmas.agenda.persistencia;

import co.edu.poligran.paradigmas.agenda.modelo.Producto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ProductoDAO implements ICrudDAO<Producto> {
    @Override
    public Producto crear(List<Producto> lista, Producto producto) {
        validarListaYProducto(lista, producto);
        validarDatosProducto(producto);
        if (producto.getId() <= 0) {
            producto.setId(siguienteId(lista));
        } else if (buscarPorId(lista, producto.getId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un producto con ese ID");
        }
        producto.setNombre(producto.getNombre().trim());
        producto.setDescripcion(producto.getDescripcion().trim());
        lista.add(producto);
        return producto;
    }

    @Override
    public Producto actualizar(List<Producto> lista, Producto producto) {
        validarListaYProducto(lista, producto);
        validarDatosProducto(producto);
        int indice = indicePorId(lista, producto.getId());
        if (indice < 0) {
            throw new IllegalArgumentException("No existe un producto con ese ID");
        }
        producto.setNombre(producto.getNombre().trim());
        producto.setDescripcion(producto.getDescripcion().trim());
        lista.set(indice, producto);
        return producto;
    }

    @Override
    public boolean eliminar(List<Producto> lista, long id) {
        validarLista(lista);
        int indice = indicePorId(lista, id);
        if (indice < 0) {
            return false;
        }
        lista.remove(indice);
        return true;
    }

    @Override
    public Optional<Producto> buscarPorId(List<Producto> lista, long id) {
        validarLista(lista);
        for (Producto producto : lista) {
            if (producto != null && producto.getId() == id) {
                return Optional.of(producto);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Producto> listar(List<Producto> lista) {
        validarLista(lista);
        return Collections.unmodifiableList(new ArrayList<>(lista));
    }

    public List<Producto> buscarPorCategoria(List<Producto> lista, long categoriaId) {
        validarLista(lista);
        List<Producto> resultado = new ArrayList<>();
        for (Producto producto : lista) {
            if (producto != null && producto.getCategoria() != null
                    && producto.getCategoria().getId() == categoriaId) {
                resultado.add(producto);
            }
        }
        return Collections.unmodifiableList(resultado);
    }

    public List<Producto> listarDisponibles(List<Producto> lista) {
        validarLista(lista);
        List<Producto> resultado = new ArrayList<>();
        for (Producto producto : lista) {
            if (producto != null && producto.isDisponible()) {
                resultado.add(producto);
            }
        }
        return Collections.unmodifiableList(resultado);
    }

    public boolean actualizarPrecio(List<Producto> lista, long id, BigDecimal precio) {
        validarLista(lista);
        validarPrecio(precio);
        Optional<Producto> producto = buscarPorId(lista, id);
        producto.ifPresent(valor -> valor.setPrecio(precio));
        return producto.isPresent();
    }

    public boolean actualizarDisponibilidad(List<Producto> lista, long id, boolean disponible) {
        validarLista(lista);
        Optional<Producto> producto = buscarPorId(lista, id);
        producto.ifPresent(valor -> valor.setDisponible(disponible));
        return producto.isPresent();
    }

    public boolean actualizarDescripcion(List<Producto> lista, long id, String descripcion) {
        validarLista(lista);
        if (descripcion == null) {
            throw new IllegalArgumentException("La descripción no puede ser null");
        }
        Optional<Producto> producto = buscarPorId(lista, id);
        producto.ifPresent(valor -> valor.setDescripcion(descripcion.trim()));
        return producto.isPresent();
    }

    public long siguienteId(List<Producto> lista) {
        validarLista(lista);
        long maximo = 0;
        for (Producto producto : lista) {
            if (producto != null && producto.getId() > maximo) {
                maximo = producto.getId();
            }
        }
        return maximo + 1;
    }

    private int indicePorId(List<Producto> lista, long id) {
        for (int indice = 0; indice < lista.size(); indice++) {
            Producto producto = lista.get(indice);
            if (producto != null && producto.getId() == id) {
                return indice;
            }
        }
        return -1;
    }

    private void validarListaYProducto(List<Producto> lista, Producto producto) {
        validarLista(lista);
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null");
        }
    }

    private void validarLista(List<Producto> lista) {
        if (lista == null) {
            throw new IllegalArgumentException("La lista no puede ser null");
        }
    }

    private void validarDatosProducto(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        if (producto.getCategoria() == null) {
            throw new IllegalArgumentException("La categoría del producto es obligatoria");
        }
        validarPrecio(producto.getPrecio());
        if (producto.getDescripcion() == null) {
            throw new IllegalArgumentException("La descripción no puede ser null");
        }
    }

    private void validarPrecio(BigDecimal precio) {
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio debe ser mayor o igual a cero");
        }
    }
}