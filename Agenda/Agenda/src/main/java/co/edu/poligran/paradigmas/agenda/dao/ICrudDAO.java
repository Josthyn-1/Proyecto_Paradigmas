package co.edu.poligran.paradigmas.agenda.dao;

import java.util.List;

/**
 * Contrato genérico de operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * para entidades almacenadas <b>estrictamente en memoria</b>.
 * <p>
 * Las implementaciones de esta interfaz no guardan estado propio: cada
 * operación recibe la lista tipada sobre la cual debe trabajar. Esto permite
 * reutilizar el mismo DAO sobre distintas listas, por ejemplo, la lista
 * materializada global de teléfonos y la lista de teléfonos de una persona.
 * </p>
 *
 * @param <T> tipo de la entidad que administra el DAO
 * @version 1.0
 * @see PersonaDAO
 * @see TelefonoDAO
 * @see CorreoElectronicoDAO
 */
public interface ICrudDAO<T> {

    /**
     * Agrega un objeto a la lista indicada.
     * <p>
     * Si el identificador del objeto es menor o igual a cero, se le asigna
     * automáticamente el siguiente id disponible en la lista (máximo + 1).
     * Si ya tiene un id válido, se conserva siempre que no esté repetido.
     * </p>
     *
     * @param lista  lista en memoria donde se almacenará el objeto; no puede ser {@code null}
     * @param objeto objeto que se desea crear; no puede ser {@code null}
     * @return el mismo objeto agregado, con su id asignado
     * @throws IllegalArgumentException si la lista o el objeto son {@code null},
     *                                  o si ya existe un elemento con el mismo id
     */
    T crear(List<T> lista, T objeto);

    /**
     * Reemplaza en la lista el elemento que tenga el mismo id que el objeto recibido.
     *
     * @param lista  lista en memoria donde se buscará el elemento; no puede ser {@code null}
     * @param objeto objeto con los datos nuevos; su id identifica el elemento a reemplazar
     * @return el objeto actualizado, o {@code null} si no existe un elemento con ese id
     * @throws IllegalArgumentException si la lista o el objeto son {@code null}
     */
    T actualizar(List<T> lista, T objeto);

    /**
     * Elimina de la lista el elemento con el id indicado.
     *
     * @param lista lista en memoria de la cual se eliminará el elemento
     * @param id    identificador del elemento a eliminar
     * @return {@code true} si se eliminó algún elemento; {@code false} si no se encontró
     */
    boolean eliminar(List<T> lista, int id);

    /**
     * Busca un elemento por su identificador.
     *
     * @param lista lista en memoria donde se realizará la búsqueda
     * @param id    identificador del elemento buscado
     * @return el elemento encontrado, o {@code null} si no existe
     */
    T buscarPorId(List<T> lista, int id);

    /**
     * Obtiene todos los elementos de la lista.
     * <p>
     * Se retorna una vista de solo lectura para evitar que se modifique la
     * lista por fuera del DAO.
     * </p>
     *
     * @param lista lista en memoria que se desea consultar
     * @return vista no modificable de la lista
     */
    List<T> listar(List<T> lista);
}
