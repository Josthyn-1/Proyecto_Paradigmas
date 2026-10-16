package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.persistencia.ICrudDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseManager<T> {
    private final ICrudDAO<T> dao;
    private final List<T> registros = new ArrayList<>();

    protected BaseManager(ICrudDAO<T> dao) {
        if (dao == null) {
            throw new IllegalArgumentException("El DAO no puede ser null");
        }
        this.dao = dao;
    }

    public T crear(T entidad) {
        return dao.crear(registros, entidad);
    }

    public T actualizar(T entidad) {
        return dao.actualizar(registros, entidad);
    }

    public boolean eliminar(long id) {
        return dao.eliminar(registros, id);
    }

    public Optional<T> buscarPorId(long id) {
        return dao.buscarPorId(registros, id);
    }

    public List<T> listar() {
        return dao.listar(registros);
    }

    protected final List<T> registros() {
        return registros;
    }
}
