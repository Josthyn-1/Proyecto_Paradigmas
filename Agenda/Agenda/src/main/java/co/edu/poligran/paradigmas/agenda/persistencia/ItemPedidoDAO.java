package co.edu.poligran.paradigmas.agenda.persistencia;

import co.edu.poligran.paradigmas.agenda.modelo.ItemPedido;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ItemPedidoDAO implements ICrudDAO<ItemPedido> {
    @Override
    public ItemPedido crear(List<ItemPedido> lista, ItemPedido item) {
        validarListaYItem(lista, item);
        validarDatosItem(item);
        if (item.getId() <= 0) {
            item.setId(siguienteId(lista));
        } else if (buscarPorId(lista, item.getId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un item de pedido con ese ID");
        }
        item.setNotas(item.getNotas().trim());
        lista.add(item);
        return item;
    }

    @Override
    public ItemPedido actualizar(List<ItemPedido> lista, ItemPedido item) {
        validarListaYItem(lista, item);
        validarDatosItem(item);
        Optional<ItemPedido> existente = buscarPorId(lista, item.getId());
        if (!existente.isPresent()) {
            throw new IllegalArgumentException("No existe un item de pedido con ese ID");
        }
        ItemPedido actual = existente.get();
        if (actual.getPedido().getId() != item.getPedido().getId()
                || actual.getProducto().getId() != item.getProducto().getId()) {
            throw new IllegalArgumentException("No se puede cambiar el pedido o producto de un item existente");
        }
        actual.setCantidad(item.getCantidad());
        actual.setNotas(item.getNotas().trim());
        return actual;
    }

    @Override
    public boolean eliminar(List<ItemPedido> lista, long id) {
        validarLista(lista);
        int indice = indicePorId(lista, id);
        if (indice < 0) {
            return false;
        }
        lista.remove(indice);
        return true;
    }

    @Override
    public Optional<ItemPedido> buscarPorId(List<ItemPedido> lista, long id) {
        validarLista(lista);
        for (ItemPedido item : lista) {
            if (item != null && item.getId() == id) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ItemPedido> listar(List<ItemPedido> lista) {
        validarLista(lista);
        return Collections.unmodifiableList(new ArrayList<>(lista));
    }

    public List<ItemPedido> buscarPorPedido(List<ItemPedido> lista, long pedidoId) {
        validarLista(lista);
        List<ItemPedido> resultado = new ArrayList<>();
        for (ItemPedido item : lista) {
            if (item != null && item.getPedido() != null
                    && item.getPedido().getId() == pedidoId) {
                resultado.add(item);
            }
        }
        return Collections.unmodifiableList(resultado);
    }

    public boolean actualizarCantidad(List<ItemPedido> lista, long id, int cantidad) {
        validarLista(lista);
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        Optional<ItemPedido> item = buscarPorId(lista, id);
        item.ifPresent(valor -> valor.setCantidad(cantidad));
        return item.isPresent();
    }

    public boolean actualizarNotas(List<ItemPedido> lista, long id, String notas) {
        validarLista(lista);
        Optional<ItemPedido> item = buscarPorId(lista, id);
        item.ifPresent(valor -> valor.setNotas(notas == null ? "" : notas.trim()));
        return item.isPresent();
    }

    public long siguienteId(List<ItemPedido> lista) {
        validarLista(lista);
        long maximo = 0;
        for (ItemPedido item : lista) {
            if (item != null && item.getId() > maximo) {
                maximo = item.getId();
            }
        }
        return maximo + 1;
    }

    private int indicePorId(List<ItemPedido> lista, long id) {
        for (int indice = 0; indice < lista.size(); indice++) {
            ItemPedido item = lista.get(indice);
            if (item != null && item.getId() == id) {
                return indice;
            }
        }
        return -1;
    }

    private void validarListaYItem(List<ItemPedido> lista, ItemPedido item) {
        validarLista(lista);
        if (item == null) {
            throw new IllegalArgumentException("El item de pedido no puede ser null");
        }
    }

    private void validarLista(List<ItemPedido> lista) {
        if (lista == null) {
            throw new IllegalArgumentException("La lista no puede ser null");
        }
    }

    private void validarDatosItem(ItemPedido item) {
        if (item.getPedido() == null) {
            throw new IllegalArgumentException("El pedido asociado es obligatorio");
        }
        if (item.getProducto() == null) {
            throw new IllegalArgumentException("El producto es obligatorio");
        }
        if (item.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        BigDecimal precio = item.getPrecioUnitarioHistorico();
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio unitario histórico debe ser mayor o igual a cero");
        }
        if (item.getNotas() == null) {
            throw new IllegalArgumentException("Las notas no pueden ser null");
        }
    }
}