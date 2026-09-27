package co.edu.poligran.paradigmas.agenda.persistencia;

import java.util.List;
import java.util.Optional;

public interface ICrudDAO<T> {
    T crear(List<T> lista, T entidad);

    T actualizar(List<T> lista, T entidad);

    boolean eliminar(List<T> lista, long id);

    Optional<T> buscarPorId(List<T> lista, long id);

    List<T> listar(List<T> lista);
}