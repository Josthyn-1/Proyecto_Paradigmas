package co.edu.poligran.paradigmas.agenda.dao;

import co.edu.poligran.paradigmas.agenda.modelo.Telefono;
import java.util.Collections;
import java.util.List;

/**
 * Objeto de Acceso a Datos (DAO) para la entidad {@link Telefono}.
 * <p>
 * Implementa las operaciones CRUD de {@link ICrudDAO} sobre listas en memoria
 * y añade la búsqueda por número. Puede operar tanto sobre la lista
 * materializada global de teléfonos como sobre la lista de teléfonos de una
 * {@link co.edu.poligran.paradigmas.agenda.modelo.Persona}.
 * </p>
 *
 * @version 1.0
 * @see ICrudDAO
 * @see Telefono
 */
public class TelefonoDAO implements ICrudDAO<Telefono> {

    /**
     * Agrega un teléfono a la lista.
     * <p>
     * Si su id es menor o igual a cero, se le asigna el siguiente id disponible.
     * El {@code idPersona} debe asignarse antes de invocar este método.
     * </p>
     *
     * @param lista    lista en memoria de teléfonos
     * @param telefono teléfono que se desea registrar
     * @return el teléfono registrado, con su id asignado
     * @throws IllegalArgumentException si la lista o el teléfono son {@code null},
     *                                  o si ya existe un teléfono con el mismo id
     */
    @Override
    public Telefono crear(List<Telefono> lista, Telefono telefono) {
        validar(lista, telefono);
        if (telefono.getId() <= 0) {
            telefono.setId(siguienteId(lista));
        } else if (buscarPorId(lista, telefono.getId()) != null) {
            throw new IllegalArgumentException("Ya existe un teléfono con id " + telefono.getId());
        }
        lista.add(telefono);
        return telefono;
    }

    /**
     * Reemplaza el teléfono que tenga el mismo id que el recibido.
     *
     * @param lista    lista en memoria de teléfonos
     * @param telefono teléfono con los datos actualizados
     * @return el teléfono actualizado, o {@code null} si no existe uno con ese id
     * @throws IllegalArgumentException si la lista o el teléfono son {@code null}
     */
    @Override
    public Telefono actualizar(List<Telefono> lista, Telefono telefono) {
        validar(lista, telefono);
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == telefono.getId()) {
                lista.set(i, telefono);
                return telefono;
            }
        }
        return null;
    }

    /**
     * Elimina el teléfono con el id indicado.
     *
     * @param lista lista en memoria de teléfonos
     * @param id    identificador del teléfono a eliminar
     * @return {@code true} si se eliminó; {@code false} si no se encontró
     */
    @Override
    public boolean eliminar(List<Telefono> lista, int id) {
        return lista.removeIf(t -> t.getId() == id);
    }

    /**
     * Busca un teléfono por su identificador.
     *
     * @param lista lista en memoria de teléfonos
     * @param id    identificador buscado
     * @return el teléfono encontrado, o {@code null} si no existe
     */
    @Override
    public Telefono buscarPorId(List<Telefono> lista, int id) {
        for (Telefono t : lista) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    /**
     * Busca un teléfono por su número.
     * <p>
     * La comparación es exacta, ignorando únicamente los espacios al inicio y
     * al final.
     * </p>
     *
     * @param lista  lista en memoria de teléfonos
     * @param numero número que se desea buscar
     * @return el primer teléfono con ese número, o {@code null} si no hay
     *         coincidencias o alguno de los parámetros es {@code null}
     */
    public Telefono buscarPorNumero(List<Telefono> lista, String numero) {
        if (lista == null || numero == null) {
            return null;
        }
        for (Telefono t : lista) {
            if (t.getNumero() != null && t.getNumero().trim().equals(numero.trim())) {
                return t;
            }
        }
        return null;
    }

    /**
     * Obtiene todos los teléfonos de la lista.
     *
     * @param lista lista en memoria de teléfonos
     * @return vista no modificable de la lista
     */
    @Override
    public List<Telefono> listar(List<Telefono> lista) {
        return Collections.unmodifiableList(lista);
    }

    /**
     * Calcula el siguiente identificador disponible (id máximo + 1).
     *
     * @param lista lista en memoria de teléfonos
     * @return el siguiente id; 1 si la lista está vacía
     */
    private int siguienteId(List<Telefono> lista) {
        int max = 0;
        for (Telefono t : lista) {
            max = Math.max(max, t.getId());
        }
        return max + 1;
    }

    /**
     * Verifica que la lista y el teléfono no sean nulos.
     *
     * @param lista    lista a validar
     * @param telefono teléfono a validar
     * @throws IllegalArgumentException si alguno de los parámetros es {@code null}
     */
    private void validar(List<Telefono> lista, Telefono telefono) {
        if (lista == null) {
            throw new IllegalArgumentException("La lista de teléfonos no puede ser nula");
        }
        if (telefono == null) {
            throw new IllegalArgumentException("El teléfono no puede ser nulo");
        }
    }
}
