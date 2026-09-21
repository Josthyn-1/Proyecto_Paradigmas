package co.edu.poligran.paradigmas.agenda.dao;

import co.edu.poligran.paradigmas.agenda.modelo.CorreoElectronico;
import java.util.Collections;
import java.util.List;

/**
 * Objeto de Acceso a Datos (DAO) para la entidad {@link CorreoElectronico}.
 * <p>
 * Implementa las operaciones CRUD de {@link ICrudDAO} sobre listas en memoria
 * y añade la búsqueda por cuenta. Puede operar tanto sobre la lista
 * materializada global de correos como sobre la lista de correos de una
 * {@link co.edu.poligran.paradigmas.agenda.modelo.Persona}.
 * </p>
 *
 * @version 1.0
 * @see ICrudDAO
 * @see CorreoElectronico
 */
public class CorreoElectronicoDAO implements ICrudDAO<CorreoElectronico> {

    /**
     * Agrega un correo electrónico a la lista.
     * <p>
     * Si su id es menor o igual a cero, se le asigna el siguiente id disponible.
     * El {@code idPersona} debe asignarse antes de invocar este método.
     * </p>
     *
     * @param lista  lista en memoria de correos
     * @param correo correo que se desea registrar
     * @return el correo registrado, con su id asignado
     * @throws IllegalArgumentException si la lista o el correo son {@code null},
     *                                  o si ya existe un correo con el mismo id
     */
    @Override
    public CorreoElectronico crear(List<CorreoElectronico> lista, CorreoElectronico correo) {
        validar(lista, correo);
        if (correo.getId() <= 0) {
            correo.setId(siguienteId(lista));
        } else if (buscarPorId(lista, correo.getId()) != null) {
            throw new IllegalArgumentException("Ya existe un correo con id " + correo.getId());
        }
        lista.add(correo);
        return correo;
    }

    /**
     * Reemplaza el correo que tenga el mismo id que el recibido.
     *
     * @param lista  lista en memoria de correos
     * @param correo correo con los datos actualizados
     * @return el correo actualizado, o {@code null} si no existe uno con ese id
     * @throws IllegalArgumentException si la lista o el correo son {@code null}
     */
    @Override
    public CorreoElectronico actualizar(List<CorreoElectronico> lista, CorreoElectronico correo) {
        validar(lista, correo);
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == correo.getId()) {
                lista.set(i, correo);
                return correo;
            }
        }
        return null;
    }

    /**
     * Elimina el correo con el id indicado.
     *
     * @param lista lista en memoria de correos
     * @param id    identificador del correo a eliminar
     * @return {@code true} si se eliminó; {@code false} si no se encontró
     */
    @Override
    public boolean eliminar(List<CorreoElectronico> lista, int id) {
        return lista.removeIf(c -> c.getId() == id);
    }

    /**
     * Busca un correo por su identificador.
     *
     * @param lista lista en memoria de correos
     * @param id    identificador buscado
     * @return el correo encontrado, o {@code null} si no existe
     */
    @Override
    public CorreoElectronico buscarPorId(List<CorreoElectronico> lista, int id) {
        for (CorreoElectronico c : lista) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    /**
     * Busca un correo por su cuenta.
     * <p>
     * La comparación ignora mayúsculas/minúsculas y espacios al inicio y al final.
     * </p>
     *
     * @param lista  lista en memoria de correos
     * @param cuenta cuenta de correo que se desea buscar (ej. {@code usuario@dominio.com})
     * @return el primer correo con esa cuenta, o {@code null} si no hay
     *         coincidencias o alguno de los parámetros es {@code null}
     */
    public CorreoElectronico buscarPorCuenta(List<CorreoElectronico> lista, String cuenta) {
        if (lista == null || cuenta == null) {
            return null;
        }
        for (CorreoElectronico c : lista) {
            if (c.getCuenta() != null && c.getCuenta().trim().equalsIgnoreCase(cuenta.trim())) {
                return c;
            }
        }
        return null;
    }

    /**
     * Obtiene todos los correos de la lista.
     *
     * @param lista lista en memoria de correos
     * @return vista no modificable de la lista
     */
    @Override
    public List<CorreoElectronico> listar(List<CorreoElectronico> lista) {
        return Collections.unmodifiableList(lista);
    }

    /**
     * Calcula el siguiente identificador disponible (id máximo + 1).
     *
     * @param lista lista en memoria de correos
     * @return el siguiente id; 1 si la lista está vacía
     */
    private int siguienteId(List<CorreoElectronico> lista) {
        int max = 0;
        for (CorreoElectronico c : lista) {
            max = Math.max(max, c.getId());
        }
        return max + 1;
    }

    /**
     * Verifica que la lista y el correo no sean nulos.
     *
     * @param lista  lista a validar
     * @param correo correo a validar
     * @throws IllegalArgumentException si alguno de los parámetros es {@code null}
     */
    private void validar(List<CorreoElectronico> lista, CorreoElectronico correo) {
        if (lista == null) {
            throw new IllegalArgumentException("La lista de correos no puede ser nula");
        }
        if (correo == null) {
            throw new IllegalArgumentException("El correo no puede ser nulo");
        }
    }
}
