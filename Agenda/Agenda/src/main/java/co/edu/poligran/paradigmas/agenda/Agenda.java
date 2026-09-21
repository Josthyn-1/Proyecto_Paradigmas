package co.edu.poligran.paradigmas.agenda;

import co.edu.poligran.paradigmas.agenda.modelo.CorreoElectronico;
import co.edu.poligran.paradigmas.agenda.modelo.Persona;
import co.edu.poligran.paradigmas.agenda.modelo.Telefono;
import co.edu.poligran.paradigmas.agenda.negocio.AgendaManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Punto de entrada. Contiene la lista de Personas de la agenda y el menú de consola.
 */
public class Agenda {

    /** Lista donde quedan guardadas las personas con sus teléfonos y correos. */
    private static final List<Persona> personas = new ArrayList<>();

    private static final AgendaManager manager = new AgendaManager(personas);
    private static final Scanner sc = new Scanner(System.in);

    private static final String REGEX_TELEFONO = "^[+\\d][\\d\\s-]{6,14}$";
    private static final String REGEX_CORREO = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$";

    public static void main(String[] args) {
        System.out.println(Mensajes.BIENVENIDA);
        int opcion;
        do {
            System.out.println(Mensajes.MENU_PRINCIPAL);
            opcion = leerEntero(Mensajes.SELECCIONE_OPCION);
            switch (opcion) {
                case 1: menuPersonas(); break;
                case 2: menuTelefonos(); break;
                case 3: menuCorreos(); break;
                case 4: verAgendaCompleta(); break;
                case 0: System.out.println(Mensajes.DESPEDIDA); break;
                default: System.out.println(Mensajes.OPCION_INVALIDA);
            }
        } while (opcion != 0);
    }

    // =====================================================================
    // Submenús
    // =====================================================================

    private static void menuPersonas() {
        int opcion;
        do {
            System.out.println(Mensajes.MENU_PERSONAS);
            opcion = leerEntero(Mensajes.SELECCIONE_OPCION);
            switch (opcion) {
                case 1: registrarPersona(); break;
                case 2: listarPersonas(); break;
                case 3: buscarPersona(); break;
                case 4: actualizarPersona(); break;
                case 5: eliminarPersona(); break;
                case 0: break;
                default: System.out.println(Mensajes.OPCION_INVALIDA);
            }
        } while (opcion != 0);
    }

    private static void menuTelefonos() {
        int opcion;
        do {
            System.out.println(Mensajes.MENU_TELEFONOS);
            opcion = leerEntero(Mensajes.SELECCIONE_OPCION);
            switch (opcion) {
                case 1: agregarTelefono(); break;
                case 2: listarTelefonos(); break;
                case 3: buscarTelefono(); break;
                case 4: actualizarTelefono(); break;
                case 5: eliminarTelefono(); break;
                case 0: break;
                default: System.out.println(Mensajes.OPCION_INVALIDA);
            }
        } while (opcion != 0);
    }

    private static void menuCorreos() {
        int opcion;
        do {
            System.out.println(Mensajes.MENU_CORREOS);
            opcion = leerEntero(Mensajes.SELECCIONE_OPCION);
            switch (opcion) {
                case 1: agregarCorreo(); break;
                case 2: listarCorreos(); break;
                case 3: buscarCorreo(); break;
                case 4: actualizarCorreo(); break;
                case 5: eliminarCorreo(); break;
                case 0: break;
                default: System.out.println(Mensajes.OPCION_INVALIDA);
            }
        } while (opcion != 0);
    }

    // =====================================================================
    // Personas
    // =====================================================================

    private static void registrarPersona() {
        System.out.println(Mensajes.TITULO_REGISTRO);
        String nombre = leerTexto(Mensajes.PEDIR_NOMBRE);
        String apellido = leerTexto(Mensajes.PEDIR_APELLIDO);
        Persona persona = new Persona(nombre, apellido);

        int cantTel = leerEnteroNoNegativo(Mensajes.PEDIR_CANTIDAD_TELEFONOS);
        for (int i = 1; i <= cantTel; i++) {
            System.out.println(String.format(Mensajes.TITULO_TELEFONO, i));
            persona.getTelefonos().add(leerDatosTelefono(null));
        }

        int cantCorreos = leerEnteroNoNegativo(Mensajes.PEDIR_CANTIDAD_CORREOS);
        for (int i = 1; i <= cantCorreos; i++) {
            System.out.println(String.format(Mensajes.TITULO_CORREO, i));
            persona.getCorreos().add(new CorreoElectronico(leerCuenta(null)));
        }

        manager.crearRegistro(persona);
        System.out.println(Mensajes.PERSONA_REGISTRADA + persona.getId());
    }

    private static void listarPersonas() {
        List<Persona> lista = manager.listarPersonas();
        if (lista.isEmpty()) {
            System.out.println(Mensajes.SIN_PERSONAS);
            return;
        }
        for (Persona p : lista) {
            System.out.println(p + "  | Tel: " + p.getTelefonos().size()
                    + " | Correos: " + p.getCorreos().size());
        }
    }

    private static void buscarPersona() {
        String nombre = leerTexto(Mensajes.PEDIR_NOMBRE_BUSCAR);
        Persona p = manager.buscarPersona(nombre);
        if (p == null) {
            System.out.println(Mensajes.PERSONA_NO_ENCONTRADA);
        } else {
            mostrarDetalle(p);
        }
    }

    private static void actualizarPersona() {
        Persona actual = seleccionarPersona();
        if (actual == null) {
            return;
        }
        String nombre = leerTextoOpcional(Mensajes.PEDIR_NOMBRE, actual.getNombre());
        String apellido = leerTextoOpcional(Mensajes.PEDIR_APELLIDO, actual.getApellido());

        Persona datos = new Persona(actual.getId(), nombre, apellido);
        if (manager.actualizarPersona(datos)) {
            System.out.println(Mensajes.PERSONA_ACTUALIZADA);
        } else {
            System.out.println(Mensajes.PERSONA_NO_ENCONTRADA);
        }
    }

    private static void eliminarPersona() {
        Persona actual = seleccionarPersona();
        if (actual == null) {
            return;
        }
        if (!confirmar(String.format(Mensajes.CONFIRMAR_ELIMINAR_PERSONA,
                actual.getNombre() + " " + actual.getApellido()))) {
            System.out.println(Mensajes.OPERACION_CANCELADA);
            return;
        }
        if (manager.eliminarPersona(actual.getId())) {
            System.out.println(Mensajes.PERSONA_ELIMINADA);
        }
    }

    // =====================================================================
    // Teléfonos
    // =====================================================================

    private static void agregarTelefono() {
        Persona persona = seleccionarPersona();
        if (persona == null) {
            return;
        }
        Telefono t = manager.agregarTelefono(persona.getId(), leerDatosTelefono(null));
        System.out.println(Mensajes.TELEFONO_AGREGADO + t.getId());
    }

    private static void listarTelefonos() {
        List<Telefono> lista = manager.listarTelefonos();
        if (lista.isEmpty()) {
            System.out.println(Mensajes.SIN_TELEFONOS);
            return;
        }
        for (Telefono t : lista) {
            System.out.println(t + "  -> " + nombreDuenio(t.getIdPersona()));
        }
    }

    private static void buscarTelefono() {
        String numero = leerTexto(Mensajes.PEDIR_NUMERO_BUSCAR);
        Telefono t = manager.buscarTelefono(numero);
        if (t == null) {
            System.out.println(Mensajes.TELEFONO_NO_ENCONTRADO);
        } else {
            System.out.println(t);
            System.out.println(Mensajes.PERTENECE_A + nombreDuenio(t.getIdPersona()));
        }
    }

    private static void actualizarTelefono() {
        int id = leerEntero(Mensajes.PEDIR_ID_TELEFONO);
        Telefono actual = manager.buscarTelefonoPorId(id);
        if (actual == null) {
            System.out.println(Mensajes.TELEFONO_NO_ENCONTRADO);
            return;
        }
        System.out.println(actual);
        Telefono datos = leerDatosTelefono(actual);
        datos.setId(actual.getId());
        if (manager.actualizarTelefono(datos)) {
            System.out.println(Mensajes.TELEFONO_ACTUALIZADO);
        }
    }

    private static void eliminarTelefono() {
        int id = leerEntero(Mensajes.PEDIR_ID_TELEFONO);
        if (manager.eliminarTelefono(id)) {
            System.out.println(Mensajes.TELEFONO_ELIMINADO);
        } else {
            System.out.println(Mensajes.TELEFONO_NO_ENCONTRADO);
        }
    }

    // =====================================================================
    // Correos electrónicos
    // =====================================================================

    private static void agregarCorreo() {
        Persona persona = seleccionarPersona();
        if (persona == null) {
            return;
        }
        CorreoElectronico c = manager.agregarCorreo(persona.getId(),
                new CorreoElectronico(leerCuenta(null)));
        System.out.println(Mensajes.CORREO_AGREGADO + c.getId());
    }

    private static void listarCorreos() {
        List<CorreoElectronico> lista = manager.listarCorreos();
        if (lista.isEmpty()) {
            System.out.println(Mensajes.SIN_CORREOS);
            return;
        }
        for (CorreoElectronico c : lista) {
            System.out.println(c + "  -> " + nombreDuenio(c.getIdPersona()));
        }
    }

    private static void buscarCorreo() {
        String cuenta = leerTexto(Mensajes.PEDIR_CUENTA_BUSCAR);
        CorreoElectronico c = manager.buscarCorreo(cuenta);
        if (c == null) {
            System.out.println(Mensajes.CORREO_NO_ENCONTRADO);
        } else {
            System.out.println(c);
            System.out.println(Mensajes.PERTENECE_A + nombreDuenio(c.getIdPersona()));
        }
    }

    private static void actualizarCorreo() {
        int id = leerEntero(Mensajes.PEDIR_ID_CORREO);
        CorreoElectronico actual = manager.buscarCorreoPorId(id);
        if (actual == null) {
            System.out.println(Mensajes.CORREO_NO_ENCONTRADO);
            return;
        }
        System.out.println(actual);
        CorreoElectronico datos = new CorreoElectronico(leerCuenta(actual));
        datos.setId(actual.getId());
        if (manager.actualizarCorreo(datos)) {
            System.out.println(Mensajes.CORREO_ACTUALIZADO);
        }
    }

    private static void eliminarCorreo() {
        int id = leerEntero(Mensajes.PEDIR_ID_CORREO);
        if (manager.eliminarCorreo(id)) {
            System.out.println(Mensajes.CORREO_ELIMINADO);
        } else {
            System.out.println(Mensajes.CORREO_NO_ENCONTRADO);
        }
    }

    // =====================================================================
    // Visualización
    // =====================================================================

    private static void verAgendaCompleta() {
        System.out.println(Mensajes.TITULO_AGENDA);
        if (personas.isEmpty()) {
            System.out.println(Mensajes.AGENDA_VACIA);
            return;
        }
        for (Persona p : personas) {
            mostrarDetalle(p);
        }
    }

    private static void mostrarDetalle(Persona p) {
        System.out.println(Mensajes.SEPARADOR);
        System.out.println(p);
        System.out.println(Mensajes.ETIQUETA_TELEFONOS);
        if (p.getTelefonos().isEmpty()) {
            System.out.println(Mensajes.ETIQUETA_SIN_TELEFONOS);
        }
        for (Telefono t : p.getTelefonos()) {
            System.out.println("    - " + t);
        }
        System.out.println(Mensajes.ETIQUETA_CORREOS);
        if (p.getCorreos().isEmpty()) {
            System.out.println(Mensajes.ETIQUETA_SIN_CORREOS);
        }
        for (CorreoElectronico c : p.getCorreos()) {
            System.out.println("    - " + c);
        }
    }

    private static String nombreDuenio(int idPersona) {
        Persona p = manager.buscarPersonaPorId(idPersona);
        return p == null ? "?" : p.getNombre() + " " + p.getApellido();
    }

    // =====================================================================
    // Lectura de datos
    // =====================================================================

    /** Pide el id de una persona, la muestra y la retorna (o null si no existe). */
    private static Persona seleccionarPersona() {
        if (personas.isEmpty()) {
            System.out.println(Mensajes.SIN_PERSONAS);
            return null;
        }
        listarPersonas();
        Persona p = manager.buscarPersonaPorId(leerEntero(Mensajes.PEDIR_ID_PERSONA));
        if (p == null) {
            System.out.println(Mensajes.PERSONA_NO_ENCONTRADA);
        } else {
            System.out.println(Mensajes.PERSONA_SELECCIONADA + p);
        }
        return p;
    }

    /** Lee número y descripción. Si 'actual' no es null, Enter conserva cada valor. */
    private static Telefono leerDatosTelefono(Telefono actual) {
        while (true) {
            String numero = actual == null
                    ? leerTexto(Mensajes.PEDIR_NUMERO)
                    : leerTextoOpcional(Mensajes.PEDIR_NUMERO, actual.getNumero());
            if (!numero.matches(REGEX_TELEFONO)) {
                System.out.println(Mensajes.NUMERO_FORMATO_INVALIDO);
                continue;
            }
            Telefono existente = manager.buscarTelefono(numero);
            if (existente != null && (actual == null || existente.getId() != actual.getId())) {
                System.out.println(Mensajes.NUMERO_DUPLICADO);
                continue;
            }
            String descripcion = actual == null
                    ? leerTexto(Mensajes.PEDIR_DESCRIPCION)
                    : leerTextoOpcional(Mensajes.PEDIR_DESCRIPCION, actual.getDescripcion());
            return new Telefono(numero, descripcion);
        }
    }

    /** Lee una cuenta de correo válida. Si 'actual' no es null, Enter la conserva. */
    private static String leerCuenta(CorreoElectronico actual) {
        while (true) {
            String cuenta = actual == null
                    ? leerTexto(Mensajes.PEDIR_CUENTA)
                    : leerTextoOpcional(Mensajes.PEDIR_CUENTA, actual.getCuenta());
            if (!cuenta.matches(REGEX_CORREO)) {
                System.out.println(Mensajes.CUENTA_FORMATO_INVALIDO);
                continue;
            }
            CorreoElectronico existente = manager.buscarCorreo(cuenta);
            if (existente != null && (actual == null || existente.getId() != actual.getId())) {
                System.out.println(Mensajes.CUENTA_DUPLICADA);
                continue;
            }
            return cuenta;
        }
    }

    private static String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = sc.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println(Mensajes.CAMPO_OBLIGATORIO);
        }
    }

    private static String leerTextoOpcional(String mensaje, String valorActual) {
        String etiqueta = mensaje.replace(": ", "")
                + String.format(Mensajes.MANTENER_VALOR, valorActual);
        System.out.print(etiqueta);
        String texto = sc.nextLine().trim();
        return texto.isEmpty() ? valorActual : texto;
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = sc.nextLine().trim();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println(Mensajes.NUMERO_INVALIDO);
            }
        }
    }

    private static int leerEnteroNoNegativo(String mensaje) {
        while (true) {
            int n = leerEntero(mensaje);
            if (n >= 0) {
                return n;
            }
            System.out.println(Mensajes.NUMERO_NEGATIVO);
        }
    }

    private static boolean confirmar(String mensaje) {
        System.out.print(mensaje + Mensajes.CONFIRMAR);
        return sc.nextLine().trim().equalsIgnoreCase("s");
    }
}
