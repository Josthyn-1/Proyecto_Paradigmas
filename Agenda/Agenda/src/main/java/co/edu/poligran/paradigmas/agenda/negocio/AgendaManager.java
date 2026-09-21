package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.dao.CorreoElectronicoDAO;
import co.edu.poligran.paradigmas.agenda.dao.PersonaDAO;
import co.edu.poligran.paradigmas.agenda.dao.TelefonoDAO;
import co.edu.poligran.paradigmas.agenda.modelo.CorreoElectronico;
import co.edu.poligran.paradigmas.agenda.modelo.Persona;
import co.edu.poligran.paradigmas.agenda.modelo.Telefono;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de negocio de la agenda.
 * <p>
 * Coordina los DAO de {@link Persona}, {@link Telefono} y
 * {@link CorreoElectronico}, y mantiene consistentes las listas materializadas
 * en memoria:
 * </p>
 * <ul>
 *   <li><b>personas</b>: la lista que vive en la clase {@code Agenda}; se
 *       recibe por el constructor.</li>
 *   <li><b>telefonos</b> y <b>correos</b>: listas globales que funcionan como
 *       "tablas", enlazadas con la persona mediante {@code idPersona}.</li>
 * </ul>
 * <p>
 * Además, cada {@link Persona} conserva sus propias listas de teléfonos y
 * correos. Todas las operaciones de este manager actualizan ambos niveles
 * (lista global y lista de la persona) para que nunca queden desincronizados.
 * </p>
 *
 * @version 1.0
 * @see PersonaDAO
 * @see TelefonoDAO
 * @see CorreoElectronicoDAO
 */
public class AgendaManager {

    /** Lista materializada de personas (compartida con la clase Agenda). */
    private final List<Persona> personas;

    /** Lista materializada global de teléfonos. */
    private final List<Telefono> telefonos;

    /** Lista materializada global de correos electrónicos. */
    private final List<CorreoElectronico> correos;

    /** DAO para las operaciones sobre personas. */
    private final PersonaDAO personaDAO;

    /** DAO para las operaciones sobre teléfonos. */
    private final TelefonoDAO telefonoDAO;

    /** DAO para las operaciones sobre correos electrónicos. */
    private final CorreoElectronicoDAO correoDAO;

    /**
     * Crea un manager que trabajará sobre la lista de personas recibida.
     * <p>
     * Las listas globales de teléfonos y correos se crean vacías.
     * </p>
     *
     * @param personas lista en memoria donde se guardarán las personas;
     *                 normalmente es la lista declarada en la clase {@code Agenda}
     */
    public AgendaManager(List<Persona> personas) {
        this.personas = personas;
        this.telefonos = new ArrayList<>();
        this.correos = new ArrayList<>();
        this.personaDAO = new PersonaDAO();
        this.telefonoDAO = new TelefonoDAO();
        this.correoDAO = new CorreoElectronicoDAO();
    }

    // =====================================================================
    // Registro completo
    // =====================================================================

    /**
     * Registra una persona junto con todos sus teléfonos y correos.
     * <p>
     * El proceso es:
     * </p>
     * <ol>
     *   <li>Se agrega la persona a la lista de personas con {@link PersonaDAO},
     *       lo que le asigna su id.</li>
     *   <li>A cada teléfono de la persona se le asigna el {@code idPersona} y se
     *       agrega a la lista global de teléfonos con {@link TelefonoDAO}.</li>
     *   <li>A cada correo de la persona se le asigna el {@code idPersona} y se
     *       agrega a la lista global de correos con {@link CorreoElectronicoDAO}.</li>
     * </ol>
     *
     * @param persona persona que se desea registrar, con sus listas de
     *                teléfonos y correos ya cargadas (pueden estar vacías)
     * @return la persona registrada, con los ids asignados a ella, a sus
     *         teléfonos y a sus correos
     * @throws IllegalArgumentException si la persona es {@code null} o si
     *                                  alguno de los objetos trae un id repetido
     */
    public Persona crearRegistro(Persona persona) {
        if (persona == null) {
            throw new IllegalArgumentException("La persona no puede ser nula");
        }
        personaDAO.crear(personas, persona);

        for (Telefono t : persona.getTelefonos()) {
            t.setIdPersona(persona.getId());
            telefonoDAO.crear(telefonos, t);
        }
        for (CorreoElectronico c : persona.getCorreos()) {
            c.setIdPersona(persona.getId());
            correoDAO.crear(correos, c);
        }
        return persona;
    }

    // =====================================================================
    // Personas
    // =====================================================================

    /**
     * Obtiene todas las personas registradas.
     *
     * @return vista no modificable de la lista de personas
     */
    public List<Persona> listarPersonas() {
        return personaDAO.listar(personas);
    }

    /**
     * Busca una persona por su nombre.
     *
     * @param nombre nombre que se desea buscar (no distingue mayúsculas)
     * @return la primera persona que coincide, o {@code null} si no existe
     * @see PersonaDAO#buscarPorNombre(List, String)
     */
    public Persona buscarPersona(String nombre) {
        return personaDAO.buscarPorNombre(personas, nombre);
    }

    /**
     * Busca una persona por su identificador.
     *
     * @param id identificador de la persona
     * @return la persona encontrada, o {@code null} si no existe
     */
    public Persona buscarPersonaPorId(int id) {
        return personaDAO.buscarPorId(personas, id);
    }

    /**
     * Actualiza el nombre y el apellido de una persona.
     * <p>
     * Los teléfonos y correos de la persona original se transfieren al objeto
     * recibido, de modo que no se pierden al reemplazarla en la lista.
     * </p>
     *
     * @param datos persona con el id a actualizar y los nuevos nombre y apellido
     * @return {@code true} si se actualizó; {@code false} si no existe una
     *         persona con ese id
     */
    public boolean actualizarPersona(Persona datos) {
        Persona actual = personaDAO.buscarPorId(personas, datos.getId());
        if (actual == null) {
            return false;
        }
        datos.setTelefonos(actual.getTelefonos());
        datos.setCorreos(actual.getCorreos());
        return personaDAO.actualizar(personas, datos) != null;
    }

    /**
     * Elimina una persona y, en cascada, todos sus teléfonos y correos de las
     * listas materializadas globales.
     *
     * @param idPersona identificador de la persona a eliminar
     * @return {@code true} si se eliminó; {@code false} si no existe
     */
    public boolean eliminarPersona(int idPersona) {
        Persona persona = personaDAO.buscarPorId(personas, idPersona);
        if (persona == null) {
            return false;
        }
        for (Telefono t : persona.getTelefonos()) {
            telefonoDAO.eliminar(telefonos, t.getId());
        }
        for (CorreoElectronico c : persona.getCorreos()) {
            correoDAO.eliminar(correos, c.getId());
        }
        return personaDAO.eliminar(personas, idPersona);
    }

    // =====================================================================
    // Teléfonos
    // =====================================================================

    /**
     * Obtiene todos los teléfonos registrados en la agenda.
     *
     * @return vista no modificable de la lista global de teléfonos
     */
    public List<Telefono> listarTelefonos() {
        return telefonoDAO.listar(telefonos);
    }

    /**
     * Busca un teléfono por su número.
     *
     * @param numero número que se desea buscar
     * @return el teléfono encontrado, o {@code null} si no existe
     * @see TelefonoDAO#buscarPorNumero(List, String)
     */
    public Telefono buscarTelefono(String numero) {
        return telefonoDAO.buscarPorNumero(telefonos, numero);
    }

    /**
     * Busca un teléfono por su identificador.
     *
     * @param id identificador del teléfono
     * @return el teléfono encontrado, o {@code null} si no existe
     */
    public Telefono buscarTelefonoPorId(int id) {
        return telefonoDAO.buscarPorId(telefonos, id);
    }

    /**
     * Agrega un teléfono a una persona existente.
     * <p>
     * El teléfono se registra tanto en la lista global de teléfonos como en la
     * lista de teléfonos de la persona.
     * </p>
     *
     * @param idPersona identificador de la persona dueña del teléfono
     * @param telefono  teléfono que se desea agregar
     * @return el teléfono agregado con su id asignado, o {@code null} si la
     *         persona no existe
     */
    public Telefono agregarTelefono(int idPersona, Telefono telefono) {
        Persona duenio = personaDAO.buscarPorId(personas, idPersona);
        if (duenio == null) {
            return null;
        }
        telefono.setIdPersona(idPersona);
        telefonoDAO.crear(telefonos, telefono);              // lista materializada
        telefonoDAO.crear(duenio.getTelefonos(), telefono);  // lista de la persona
        return telefono;
    }

    /**
     * Actualiza el número y la descripción de un teléfono.
     * <p>
     * Se conserva el {@code idPersona} original y el cambio se aplica tanto en
     * la lista global como en la lista de la persona dueña.
     * </p>
     *
     * @param datos teléfono con el id a actualizar y los nuevos datos
     * @return {@code true} si se actualizó; {@code false} si no existe un
     *         teléfono con ese id
     */
    public boolean actualizarTelefono(Telefono datos) {
        Telefono actual = telefonoDAO.buscarPorId(telefonos, datos.getId());
        if (actual == null) {
            return false;
        }
        datos.setIdPersona(actual.getIdPersona());
        telefonoDAO.actualizar(telefonos, datos);
        Persona duenio = personaDAO.buscarPorId(personas, actual.getIdPersona());
        if (duenio != null) {
            telefonoDAO.actualizar(duenio.getTelefonos(), datos);
        }
        return true;
    }

    /**
     * Elimina un teléfono de la lista global y de la lista de su dueño.
     *
     * @param id identificador del teléfono a eliminar
     * @return {@code true} si se eliminó; {@code false} si no existe
     */
    public boolean eliminarTelefono(int id) {
        Telefono actual = telefonoDAO.buscarPorId(telefonos, id);
        if (actual == null) {
            return false;
        }
        Persona duenio = personaDAO.buscarPorId(personas, actual.getIdPersona());
        if (duenio != null) {
            telefonoDAO.eliminar(duenio.getTelefonos(), id);
        }
        return telefonoDAO.eliminar(telefonos, id);
    }

    // =====================================================================
    // Correos electrónicos
    // =====================================================================

    /**
     * Obtiene todos los correos registrados en la agenda.
     *
     * @return vista no modificable de la lista global de correos
     */
    public List<CorreoElectronico> listarCorreos() {
        return correoDAO.listar(correos);
    }

    /**
     * Busca un correo por su cuenta.
     *
     * @param cuenta cuenta que se desea buscar (no distingue mayúsculas)
     * @return el correo encontrado, o {@code null} si no existe
     * @see CorreoElectronicoDAO#buscarPorCuenta(List, String)
     */
    public CorreoElectronico buscarCorreo(String cuenta) {
        return correoDAO.buscarPorCuenta(correos, cuenta);
    }

    /**
     * Busca un correo por su identificador.
     *
     * @param id identificador del correo
     * @return el correo encontrado, o {@code null} si no existe
     */
    public CorreoElectronico buscarCorreoPorId(int id) {
        return correoDAO.buscarPorId(correos, id);
    }

    /**
     * Agrega un correo a una persona existente.
     * <p>
     * El correo se registra tanto en la lista global de correos como en la
     * lista de correos de la persona.
     * </p>
     *
     * @param idPersona identificador de la persona dueña del correo
     * @param correo    correo que se desea agregar
     * @return el correo agregado con su id asignado, o {@code null} si la
     *         persona no existe
     */
    public CorreoElectronico agregarCorreo(int idPersona, CorreoElectronico correo) {
        Persona duenio = personaDAO.buscarPorId(personas, idPersona);
        if (duenio == null) {
            return null;
        }
        correo.setIdPersona(idPersona);
        correoDAO.crear(correos, correo);
        correoDAO.crear(duenio.getCorreos(), correo);
        return correo;
    }

    /**
     * Actualiza la cuenta de un correo.
     * <p>
     * Se conserva el {@code idPersona} original y el cambio se aplica tanto en
     * la lista global como en la lista de la persona dueña.
     * </p>
     *
     * @param datos correo con el id a actualizar y la nueva cuenta
     * @return {@code true} si se actualizó; {@code false} si no existe un
     *         correo con ese id
     */
    public boolean actualizarCorreo(CorreoElectronico datos) {
        CorreoElectronico actual = correoDAO.buscarPorId(correos, datos.getId());
        if (actual == null) {
            return false;
        }
        datos.setIdPersona(actual.getIdPersona());
        correoDAO.actualizar(correos, datos);
        Persona duenio = personaDAO.buscarPorId(personas, actual.getIdPersona());
        if (duenio != null) {
            correoDAO.actualizar(duenio.getCorreos(), datos);
        }
        return true;
    }

    /**
     * Elimina un correo de la lista global y de la lista de su dueño.
     *
     * @param id identificador del correo a eliminar
     * @return {@code true} si se eliminó; {@code false} si no existe
     */
    public boolean eliminarCorreo(int id) {
        CorreoElectronico actual = correoDAO.buscarPorId(correos, id);
        if (actual == null) {
            return false;
        }
        Persona duenio = personaDAO.buscarPorId(personas, actual.getIdPersona());
        if (duenio != null) {
            correoDAO.eliminar(duenio.getCorreos(), id);
        }
        return correoDAO.eliminar(correos, id);
    }
}
