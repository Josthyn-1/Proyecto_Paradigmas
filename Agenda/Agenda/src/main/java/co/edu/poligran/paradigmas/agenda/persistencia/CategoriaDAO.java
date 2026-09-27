package co.edu.poligran.paradigmas.agenda.persistencia;

import co.edu.poligran.paradigmas.agenda.modelo.Categoria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class CategoriaDAO implements ICrudDAO<Categoria> {
    @Override
    public Categoria crear(List<Categoria> lista, Categoria categoria) {
        validarListaYCategoria(lista, categoria);
        validarNombre(categoria.getNombre());
        if (buscarPorNombre(lista, categoria.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }
        if (categoria.getId() <= 0) {
            categoria.setId(siguienteId(lista));
        } else if (buscarPorId(lista, categoria.getId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoría con ese ID");
        }
        categoria.setNombre(categoria.getNombre().trim());
        lista.add(categoria);
        return categoria;
    }

    @Override
    public Categoria actualizar(List<Categoria> lista, Categoria categoria) {
        validarListaYCategoria(lista, categoria);
        validarNombre(categoria.getNombre());
        int indice = indicePorId(lista, categoria.getId());
        if (indice < 0) {
            throw new IllegalArgumentException("No existe una categoría con ese ID");
        }
        Optional<Categoria> categoriaConNombre = buscarPorNombre(lista, categoria.getNombre());
        if (categoriaConNombre.isPresent() && categoriaConNombre.get().getId() != categoria.getId()) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }
        categoria.setNombre(categoria.getNombre().trim());
        lista.set(indice, categoria);
        return categoria;
    }

    @Override
    public boolean eliminar(List<Categoria> lista, long id) {
        validarLista(lista);
        int indice = indicePorId(lista, id);
        if (indice < 0) {
            return false;
        }
        lista.remove(indice);
        return true;
    }

    @Override
    public Optional<Categoria> buscarPorId(List<Categoria> lista, long id) {
        validarLista(lista);
        for (Categoria categoria : lista) {
            if (categoria != null && categoria.getId() == id) {
                return Optional.of(categoria);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Categoria> listar(List<Categoria> lista) {
        validarLista(lista);
        return Collections.unmodifiableList(new ArrayList<>(lista));
    }

    public Optional<Categoria> buscarPorNombre(List<Categoria> lista, String nombre) {
        validarLista(lista);
        String nombreNormalizado = normalizar(nombre);
        if (nombreNormalizado.isEmpty()) {
            return Optional.empty();
        }
        for (Categoria categoria : lista) {
            if (categoria != null && normalizar(categoria.getNombre()).equals(nombreNormalizado)) {
                return Optional.of(categoria);
            }
        }
        return Optional.empty();
    }

    public boolean renombrar(List<Categoria> lista, long id, String nuevoNombre) {
        validarLista(lista);
        validarNombre(nuevoNombre);
        Optional<Categoria> categoria = buscarPorId(lista, id);
        if (!categoria.isPresent()) {
            return false;
        }
        Optional<Categoria> categoriaConNombre = buscarPorNombre(lista, nuevoNombre);
        if (categoriaConNombre.isPresent() && categoriaConNombre.get().getId() != id) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }
        categoria.get().setNombre(nuevoNombre.trim());
        return true;
    }

    public boolean actualizarEstado(List<Categoria> lista, long id, boolean activa) {
        validarLista(lista);
        Optional<Categoria> categoria = buscarPorId(lista, id);
        categoria.ifPresent(valor -> valor.setActiva(activa));
        return categoria.isPresent();
    }

    public boolean habilitar(List<Categoria> lista, long id) {
        return actualizarEstado(lista, id, true);
    }

    public boolean deshabilitar(List<Categoria> lista, long id) {
        return actualizarEstado(lista, id, false);
    }

    public List<Categoria> listarActivas(List<Categoria> lista) {
        validarLista(lista);
        List<Categoria> resultado = new ArrayList<>();
        for (Categoria categoria : lista) {
            if (categoria != null && categoria.isActiva()) {
                resultado.add(categoria);
            }
        }
        return Collections.unmodifiableList(resultado);
    }

    public long siguienteId(List<Categoria> lista) {
        validarLista(lista);
        long maximo = 0;
        for (Categoria categoria : lista) {
            if (categoria != null && categoria.getId() > maximo) {
                maximo = categoria.getId();
            }
        }
        return maximo + 1;
    }

    private int indicePorId(List<Categoria> lista, long id) {
        for (int indice = 0; indice < lista.size(); indice++) {
            Categoria categoria = lista.get(indice);
            if (categoria != null && categoria.getId() == id) {
                return indice;
            }
        }
        return -1;
    }

    private void validarListaYCategoria(List<Categoria> lista, Categoria categoria) {
        validarLista(lista);
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría no puede ser null");
        }
    }

    private void validarLista(List<Categoria> lista) {
        if (lista == null) {
            throw new IllegalArgumentException("La lista no puede ser null");
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
    }

    private String normalizar(String nombre) {
        return nombre == null ? "" : nombre.trim().toLowerCase(Locale.ROOT);
    }
}