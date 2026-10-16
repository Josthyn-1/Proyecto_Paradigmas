package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.Categoria;
import co.edu.poligran.paradigmas.agenda.persistencia.CategoriaDAO;

import java.util.List;
import java.util.Optional;

public class CategoriaManager extends BaseManager<Categoria> {
    private final CategoriaDAO categoriaDAO;

    public CategoriaManager() {
        this(new CategoriaDAO());
    }

    private CategoriaManager(CategoriaDAO categoriaDAO) {
        super(categoriaDAO);
        this.categoriaDAO = categoriaDAO;
    }

    public Optional<Categoria> buscarPorNombre(String nombre) {
        return categoriaDAO.buscarPorNombre(registros(), nombre);
    }

    public boolean renombrar(long id, String nuevoNombre) {
        return categoriaDAO.renombrar(registros(), id, nuevoNombre);
    }

    public boolean actualizarEstado(long id, boolean activa) {
        return categoriaDAO.actualizarEstado(registros(), id, activa);
    }

    public boolean habilitar(long id) {
        return categoriaDAO.habilitar(registros(), id);
    }

    public boolean deshabilitar(long id) {
        return categoriaDAO.deshabilitar(registros(), id);
    }

    public List<Categoria> listarActivas() {
        return categoriaDAO.listarActivas(registros());
    }
}
