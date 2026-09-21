package co.edu.poligran.paradigmas.agenda;

/**
 * Centraliza todos los textos que se muestran en la consola.
 */
public final class Mensajes {

    private Mensajes() {
    }

    private static final String NL = System.lineSeparator();

    // ---------------------------------------------------------------- Menús
    public static final String BIENVENIDA
            = "==========================================" + NL
            + "        BIENVENIDO A LA AGENDA" + NL
            + "==========================================";

    public static final String MENU_PRINCIPAL
            = NL + "------------- MENÚ PRINCIPAL -------------" + NL
            + " 1. Gestionar personas" + NL
            + " 2. Gestionar teléfonos" + NL
            + " 3. Gestionar correos electrónicos" + NL
            + " 4. Ver agenda completa" + NL
            + " 0. Salir" + NL
            + "------------------------------------------";

    public static final String MENU_PERSONAS
            = NL + "---------------- PERSONAS ----------------" + NL
            + " 1. Registrar persona (con teléfonos y correos)" + NL
            + " 2. Listar personas" + NL
            + " 3. Buscar persona por nombre" + NL
            + " 4. Actualizar persona" + NL
            + " 5. Eliminar persona" + NL
            + " 0. Volver" + NL
            + "------------------------------------------";

    public static final String MENU_TELEFONOS
            = NL + "---------------- TELÉFONOS ---------------" + NL
            + " 1. Agregar teléfono a una persona" + NL
            + " 2. Listar teléfonos" + NL
            + " 3. Buscar teléfono por número" + NL
            + " 4. Actualizar teléfono" + NL
            + " 5. Eliminar teléfono" + NL
            + " 0. Volver" + NL
            + "------------------------------------------";

    public static final String MENU_CORREOS
            = NL + "---------- CORREOS ELECTRÓNICOS ----------" + NL
            + " 1. Agregar correo a una persona" + NL
            + " 2. Listar correos" + NL
            + " 3. Buscar correo por cuenta" + NL
            + " 4. Actualizar correo" + NL
            + " 5. Eliminar correo" + NL
            + " 0. Volver" + NL
            + "------------------------------------------";

    // ------------------------------------------------------------- Generales
    public static final String SELECCIONE_OPCION = "Seleccione una opción: ";
    public static final String OPCION_INVALIDA = "Opción no válida. Intente de nuevo.";
    public static final String NUMERO_INVALIDO = "Debe ingresar un número entero válido.";
    public static final String NUMERO_NEGATIVO = "El valor no puede ser negativo.";
    public static final String CAMPO_OBLIGATORIO = "Este campo es obligatorio.";
    public static final String OPERACION_CANCELADA = "Operación cancelada.";
    public static final String CONFIRMAR = " (s/n): ";
    public static final String MANTENER_VALOR = " [Enter para mantener: %s]: ";
    public static final String DESPEDIDA = "¡Hasta pronto!";
    public static final String AGENDA_VACIA = "La agenda está vacía.";
    public static final String TITULO_AGENDA = NL + "============= AGENDA COMPLETA ============";
    public static final String SEPARADOR = "------------------------------------------";

    // -------------------------------------------------------------- Personas
    public static final String TITULO_REGISTRO = NL + ">> Registro de nueva persona";
    public static final String PEDIR_NOMBRE = "Nombre: ";
    public static final String PEDIR_APELLIDO = "Apellido: ";
    public static final String PEDIR_NOMBRE_BUSCAR = "Nombre a buscar: ";
    public static final String PEDIR_ID_PERSONA = "Id de la persona: ";
    public static final String PEDIR_CANTIDAD_TELEFONOS = "¿Cuántos teléfonos desea registrar? ";
    public static final String PEDIR_CANTIDAD_CORREOS = "¿Cuántos correos desea registrar? ";
    public static final String PERSONA_REGISTRADA = "Persona registrada con id: ";
    public static final String PERSONA_ACTUALIZADA = "Persona actualizada correctamente.";
    public static final String PERSONA_ELIMINADA = "Persona eliminada junto con sus teléfonos y correos.";
    public static final String PERSONA_NO_ENCONTRADA = "No se encontró ninguna persona.";
    public static final String CONFIRMAR_ELIMINAR_PERSONA = "¿Seguro que desea eliminar a %s?";
    public static final String SIN_PERSONAS = "No hay personas registradas.";
    public static final String PERSONA_SELECCIONADA = "Persona: ";

    // ------------------------------------------------------------- Teléfonos
    public static final String TITULO_TELEFONO = ">> Teléfono #%d";
    public static final String PEDIR_NUMERO = "Número: ";
    public static final String PEDIR_NUMERO_BUSCAR = "Número a buscar: ";
    public static final String PEDIR_DESCRIPCION = "Descripción (ej. Celular, Casa, Oficina): ";
    public static final String PEDIR_ID_TELEFONO = "Id del teléfono: ";
    public static final String NUMERO_FORMATO_INVALIDO
            = "Número inválido: use solo dígitos, espacios, '+' o '-' (7 a 15 caracteres).";
    public static final String NUMERO_DUPLICADO = "Ese número ya está registrado en la agenda.";
    public static final String TELEFONO_AGREGADO = "Teléfono agregado con id: ";
    public static final String TELEFONO_ACTUALIZADO = "Teléfono actualizado correctamente.";
    public static final String TELEFONO_ELIMINADO = "Teléfono eliminado correctamente.";
    public static final String TELEFONO_NO_ENCONTRADO = "No se encontró el teléfono.";
    public static final String SIN_TELEFONOS = "No hay teléfonos registrados.";
    public static final String ETIQUETA_TELEFONOS = "  Teléfonos:";
    public static final String ETIQUETA_SIN_TELEFONOS = "    (sin teléfonos)";

    // --------------------------------------------------------------- Correos
    public static final String TITULO_CORREO = ">> Correo #%d";
    public static final String PEDIR_CUENTA = "Cuenta de correo: ";
    public static final String PEDIR_CUENTA_BUSCAR = "Cuenta a buscar: ";
    public static final String PEDIR_ID_CORREO = "Id del correo: ";
    public static final String CUENTA_FORMATO_INVALIDO = "Formato de correo inválido (ej. usuario@dominio.com).";
    public static final String CUENTA_DUPLICADA = "Esa cuenta ya está registrada en la agenda.";
    public static final String CORREO_AGREGADO = "Correo agregado con id: ";
    public static final String CORREO_ACTUALIZADO = "Correo actualizado correctamente.";
    public static final String CORREO_ELIMINADO = "Correo eliminado correctamente.";
    public static final String CORREO_NO_ENCONTRADO = "No se encontró el correo.";
    public static final String SIN_CORREOS = "No hay correos registrados.";
    public static final String ETIQUETA_CORREOS = "  Correos:";
    public static final String ETIQUETA_SIN_CORREOS = "    (sin correos)";
    public static final String PERTENECE_A = "  Pertenece a: ";
}
