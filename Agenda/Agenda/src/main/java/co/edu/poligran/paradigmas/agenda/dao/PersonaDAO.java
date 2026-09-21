package co.edu.poligran.paradigmas.agenda.dao;

import co.edu.poligran.paradigmas.agenda.modelo.Persona;
import java.util.Collections;
import java.util.List;

/**
 * Objeto de Acceso a Datos (DAO) para la entidad {@link Persona}.
 * <p>
 * Implementa las operaciones CRUD de {@link ICrudDAO} sobre listas en memoria
 * y añade la búsqueda por nombre. Este DAO solo gestiona los datos de la
 * persona; la coordinación con sus teléfonos y correos es responsabilidad de
 * {@link agenda.negocio.AgendaManager}.
 * </p>
 *
 * @version 1.0
 * @see ICrudDAO
 * @see Persona
 */
public class PersonaDAO implements ICrudDAO<Persona> {

    /**
     * Agrega una persona a la lista.
     * <p>
     * Si su id es menor o igual a cero, se le asigna el siguiente id disponible.
     * </p>
     *
     * @param lista   lista en memoria de personas
     * @param persona persona que se desea registrar
     * @return la persona registrada, con su id asignado
     * @throws IllegalArgumentException si la lista o la persona son {@code null},
     *                                  o si ya existe una persona con el mismo id
     */
    @Override
    public Persona crear(List<Persona> lista, Persona persona) {
        validar(lista, persona);
        if (persona.getId() <= 0) {
            persona.setId(siguienteId(lista));
        } else if (buscarPorId(lista, persona.getId()) != null) {
            throw new IllegalArgumentException("Ya existe una persona con id " + persona.getId());
        }
        lista.add(persona);
        return persona;
    }

    /**
     * Reemplaza la persona que tenga el mismo id que la recibida.
     *
     * @param lista   lista en memoria de personas
     * @param persona persona con los datos actualizados
     * @return la persona actualizada, o {@code null} si no existe una con ese id
     * @throws IllegalArgumentException si la lista o la persona son {@code null}
     */
    @Override
    public Persona actualizar(List<Persona> lista, Persona persona) {
        validar(lista, persona);
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == persona.getId()) {
                lista.set(i, persona);
                return persona;
            }
        }
        return null;
    }

    /**
     * Elimina la persona con el id indicado.
     * <p>
     * <b>Nota:</b> no elimina sus teléfonos ni correos de las listas
     * materializadas; para una eliminación en cascada use
     * {@link agenda.negocio.AgendaManager#eliminarPersona(int)}.
     * </p>
     *
     * @param lista lista en memoria de personas
     * @param id    identificador de la persona a eliminar
     * @return {@code true} si se eliminó; {@code false} si no se encontró
     */
    @Override
    public boolean eliminar(List<Persona> lista, int id) {
        return lista.removeIf(p -> p.getId() == id);
    }

    /**
     * Busca una persona por su identificador.
     *
     * @param lista lista en memoria de personas
     * @param id    identificador buscado
     * @return la persona encontrada, o {@code null} si no existe
     */
    @Override
    public Persona buscarPorId(List<Persona> lista, int id) {
        for (Persona p : lista) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    /**
     * Busca una persona por su nombre.
     * <p>
     * La comparación ignora mayúsculas/minúsculas y espacios al inicio y al
     * final. Si varias personas comparten el nombre, se retorna la primera
     * encontrada.
     * </p>
     *
     * @param lista  lista en memoria de personas
     * @param nombre nombre que se desea buscar
     * @return la primera persona cuyo nombre coincide, o {@code null} si no hay
     *         coincidencias o alguno de los parámetros es {@code null}
     */
    public Persona buscarPorNombre(List<Persona> lista, String nombre) {
        if (lista == null || nombre == null) {
            return null;
        }
        for (Persona p : lista) {
            if (p.getNombre() != null && p.getNombre().trim().equalsIgnoreCase(nombre.trim())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Obtiene todas las personas de la lista.
     *
     * @param lista lista en memoria de personas
     * @return vista no modificable de la lista
     */
    @Override
    public List<Persona> listar(List<Persona> lista) {
        return Collections.unmodifiableList(lista);
    }

    /**
     * Calcula el siguiente identificador disponible (id máximo + 1).
     *
     * @param lista lista en memoria de personas
     * @return el siguiente id; 1 si la lista está vacía
     */
    private int siguienteId(List<Persona> lista) {
        int max = 0;
        for (Persona p : lista) {
            max = Math.max(max, p.getId());
        }
        return max + 1;
    }

    /**
     * Verifica que la lista y la persona no sean nulas.
     *
     * @param lista   lista a validar
     * @param persona persona a validar
     * @throws IllegalArgumentException si alguno de los parámetros es {@code null}
     */
    private void validar(List<Persona> lista, Persona persona) {
        if (lista == null) {
            throw new IllegalArgumentException("La lista de personas no puede ser nula");
        }
        if (persona == null) {
            throw new IllegalArgumentException("La persona no puede ser nula");
        }
    }
}
