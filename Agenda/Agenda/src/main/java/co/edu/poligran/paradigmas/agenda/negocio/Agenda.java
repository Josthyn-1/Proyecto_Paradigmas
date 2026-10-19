package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Agenda {
    private final Scanner entrada = new Scanner(System.in);

    private final MesaManager mesaManager = new MesaManager();
    private final CategoriaManager categoriaManager = new CategoriaManager();
    private final ProductoManager productoManager = new ProductoManager();
    private final PedidoManager pedidoManager = new PedidoManager();
    private final ItemPedidoManager itemPedidoManager = new ItemPedidoManager();
    private final UsuarioManager usuarioManager = new UsuarioManager();
    private final HistorialEstadoManager historialManager = new HistorialEstadoManager();
    private final FacturaManager facturaManager = new FacturaManager();

    // Usuario por defecto (mesero) usado en operaciones que requieran responsable
    private final Usuario mesero = new Usuario(1, "Mesero", "Demo", "mesero", RolUsuario.MESERO, true);

    public static void main(String[] args) {
        new Agenda().iniciar();
    }

    private void iniciar() {
        inicializarDemo();
        boolean ejecutando = true;
        while (ejecutando) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("Selecciona una opción: ");
            switch (opcion) {
                case 1: menuMesas(); break;
                case 2: menuProductos(); break;
                case 3: menuPedidos(); break;
                case 4: menuUsuarios(); break;
                case 5: menuFacturas(); break;
                case 6: mostrarHistorial(); break;
                case 0: ejecutando = false; break;
                default: System.out.println("Opción no válida.");
            }
        }
        System.out.println("Programa finalizado.");
    }

    private void inicializarDemo() {
        Categoria categoriaGeneral = obtenerCategoriaGeneral();
        // Inicializa algunas mesas por defecto
        if (mesaManager.listar().isEmpty()) {
            mesaManager.crear(new Mesa(0, 1, 4, EstadoMesa.LIBRE));
            mesaManager.crear(new Mesa(0, 2, 4, EstadoMesa.LIBRE));
            mesaManager.crear(new Mesa(0, 3, 2, EstadoMesa.LIBRE));
        }
        // Puedes añadir productos de demo si lo deseas (ajusta constructor si es necesario)
        if (productoManager.listar().isEmpty()) {
            try {
                productoManager.crear(new Producto(0, "Agua", categoriaGeneral, new BigDecimal("1.50"), true, "Agua embotellada"));
                productoManager.crear(new Producto(0, "Cafe", categoriaGeneral, new BigDecimal("1.20"), true, "Cafe negro"));
            } catch (Exception e) {
                // Si el constructor real difiere, ajusta en tu proyecto
            }
        }
        // Registrar usuario mesero (si no existe)
        usuarioManager.buscarPorNombreAcceso("mesero").or(() -> {
            try {
                Usuario u = usuarioManager.crear(new Usuario(0, "Mesero", "Demo", "mesero", RolUsuario.MESERO, true));
                return Optional.of(u);
            } catch (Exception ex) {
                return Optional.empty();
            }
        });
    }

    private Categoria obtenerCategoriaGeneral() {
        return categoriaManager.buscarPorNombre("General")
                .orElseGet(() -> categoriaManager.crear(new Categoria(0, "General", true)));
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n=== Agenda - Menú Principal ===");
        System.out.println("1. Mesas");
        System.out.println("2. Productos");
        System.out.println("3. Pedidos");
        System.out.println("4. Usuarios");
        System.out.println("5. Facturas");
        System.out.println("6. Historial de estados");
        System.out.println("0. Salir");
    }

    /* ---------- MENÚ MESAS ---------- */
    private void menuMesas() {
        boolean regresar = false;
        while (!regresar) {
            System.out.println("\n--- Mesas ---");
            System.out.println("1. Listar mesas");
            System.out.println("2. Crear mesa");
            System.out.println("3. Cambiar estado de mesa");
            System.out.println("4. Eliminar mesa");
            System.out.println("0. Volver");
            int op = leerEntero("Opción: ");
            switch (op) {
                case 1: listarMesas(); break;
                case 2: crearMesa(); break;
                case 3: cambiarEstadoMesa(); break;
                case 4: eliminarMesa(); break;
                case 0: regresar = true; break;
                default: System.out.println("Opción no válida.");
            }
        }
    }

    private void listarMesas() {
        List<Mesa> mesas = mesaManager.listar();
        if (mesas.isEmpty()) {
            System.out.println("No hay mesas registradas.");
            return;
        }
        System.out.println("\nMesas:");
        for (Mesa m : mesas) {
            System.out.printf("Id:%d | Nº:%d | Cap:%d | Estado:%s%n", m.getId(), m.getNumero(), m.getCapacidad(), m.getEstado());
        }
    }

    private void crearMesa() {
        try {
            int numero = leerEntero("Número de mesa: ");
            int capacidad = leerEntero("Capacidad: ");
            Mesa nueva = new Mesa(0, numero, capacidad, EstadoMesa.LIBRE);
            mesaManager.crear(nueva);
            System.out.println("Mesa creada correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error al crear mesa: " + e.getMessage());
        }
    }

    private void cambiarEstadoMesa() {
        listarMesas();
        long id = leerEntero("Id de la mesa a modificar: ");
        Optional<Mesa> opt = mesaManager.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println("Mesa no encontrada.");
            return;
        }
        System.out.println("Estados disponibles:");
        for (EstadoMesa em : EstadoMesa.values()) {
            System.out.println(em);
        }
        String estadoStr = leerLinea("Nuevo estado (nombre): ");
        try {
            EstadoMesa nuevo = EstadoMesa.valueOf(estadoStr.trim().toUpperCase());
            boolean ok = mesaManager.actualizarEstado(id, nuevo);
            System.out.println(ok ? "Estado actualizado." : "No se pudo actualizar el estado.");
        } catch (Exception e) {
            System.out.println("Estado inválido.");
        }
    }

    private void eliminarMesa() {
        listarMesas();
        long id = leerEntero("Id de la mesa a eliminar: ");
        boolean ok = mesaManager.eliminar(id);
        System.out.println(ok ? "Mesa eliminada." : "No se encontró la mesa.");
    }

    /* ---------- MENÚ PRODUCTOS ---------- */
    private void menuProductos() {
        boolean regresar = false;
        while (!regresar) {
            System.out.println("\n--- Productos ---");
            System.out.println("1. Listar productos");
            System.out.println("2. Crear producto");
            System.out.println("3. Actualizar precio");
            System.out.println("4. Cambiar disponibilidad");
            System.out.println("5. Eliminar producto");
            System.out.println("0. Volver");
            int op = leerEntero("Opción: ");
            switch (op) {
                case 1: listarProductos(); break;
                case 2: crearProducto(); break;
                case 3: actualizarPrecioProducto(); break;
                case 4: cambiarDisponibilidadProducto(); break;
                case 5: eliminarProducto(); break;
                case 0: regresar = true; break;
                default: System.out.println("Opción no válida.");
            }
        }
    }

    private void listarProductos() {
        List<Producto> lista = productoManager.listar();
        if (lista.isEmpty()) {
            System.out.println("No hay productos.");
            return;
        }
        System.out.println("\nProductos:");
        for (Producto p : lista) {
            System.out.printf("Id:%d | %s | %s | Precio:%s | Disponible:%s%n",
                    p.getId(), p.getNombre(), p.getDescripcion(),
                    p.getPrecio(), p.isDisponible());
        }
    }

    private void crearProducto() {
        try {
            String nombre = leerLinea("Nombre: ");
            String descripcion = leerLinea("Descripción: ");
            BigDecimal precio = new BigDecimal(leerLinea("Precio: "));
            boolean disponible = leerEntero("Disponible? 1=si 0=no: ") == 1;
            // Ajusta el constructor de Producto según tu modelo si es distinto
            Producto p = new Producto(0, nombre, obtenerCategoriaGeneral(), precio, disponible, descripcion);
            productoManager.crear(p);
            System.out.println("Producto creado.");
        } catch (Exception e) {
            System.out.println("Error creando producto: " + e.getMessage());
        }
    }

    private void actualizarPrecioProducto() {
        listarProductos();
        long id = leerEntero("Id producto: ");
        BigDecimal precio = new BigDecimal(leerLinea("Nuevo precio: "));
        boolean ok = productoManager.actualizarPrecio(id, precio);
        System.out.println(ok ? "Precio actualizado." : "Producto no encontrado.");
    }

    private void cambiarDisponibilidadProducto() {
        listarProductos();
        long id = leerEntero("Id producto: ");
        boolean disponible = leerEntero("1=disponible, 0=no: ") == 1;
        boolean ok = productoManager.actualizarDisponibilidad(id, disponible);
        System.out.println(ok ? "Disponibilidad actualizada." : "Producto no encontrado.");
    }

    private void eliminarProducto() {
        listarProductos();
        long id = leerEntero("Id producto a eliminar: ");
        boolean ok = productoManager.eliminar(id);
        System.out.println(ok ? "Producto eliminado." : "No se encontró el producto.");
    }

    /* ---------- MENÚ PEDIDOS ---------- */
    private void menuPedidos() {
        boolean regresar = false;
        while (!regresar) {
            System.out.println("\n--- Pedidos ---");
            System.out.println("1. Listar pedidos");
            System.out.println("2. Crear pedido");
            System.out.println("3. Agregar item a pedido");
            System.out.println("4. Modificar cantidad/nota de item");
            System.out.println("5. Cambiar estado pedido");
            System.out.println("6. Eliminar pedido");
            System.out.println("0. Volver");
            int op = leerEntero("Opción: ");
            switch (op) {
                case 1: listarPedidos(); break;
                case 2: crearPedido(); break;
                case 3: agregarItemAPedido(); break;
                case 4: modificarItemPedido(); break;
                case 5: cambiarEstadoPedido(); break;
                case 6: eliminarPedido(); break;
                case 0: regresar = true; break;
                default: System.out.println("Opción no válida.");
            }
        }
    }

    private void listarPedidos() {
        List<Pedido> lista = pedidoManager.listar();
        if (lista.isEmpty()) {
            System.out.println("Todavía no hay pedidos.");
            return;
        }
        System.out.println("\nPedidos:");
        for (Pedido pedido : lista) {
            System.out.printf("Pedido #%d | Mesa %d | Estado: %s | Mesero: %s %s%n",
                    pedido.getId(),
                    pedido.getMesa().getNumero(),
                    pedido.getEstado(),
                    pedido.getMesero().getNombre(),
                    pedido.getMesero().getApellido());
            // listar items del pedido
            List<ItemPedido> items = itemPedidoManager.buscarPorPedido(pedido.getId());
            for (ItemPedido it : items) {
                System.out.printf("  Item Id:%d | Producto:%s | Cant:%d | Notas:%s%n",
                        it.getId(), it.getProducto().getNombre(), it.getCantidad(), it.getNotas());
            }
        }
    }

    private void crearPedido() {
        List<Mesa> libres = mesaManager.consultarLibres();
        if (libres.isEmpty()) {
            System.out.println("No hay mesas libres.");
            return;
        }
        System.out.println("Mesas libres:");
        for (Mesa m : libres) {
            System.out.printf("Nº:%d (Id:%d) Cap:%d%n", m.getNumero(), m.getId(), m.getCapacidad());
        }
        int numeroMesa = leerEntero("Número de mesa: ");
        Optional<Mesa> mesaOpt = mesaManager.buscarPorNumero(numeroMesa);
        if (mesaOpt.isEmpty() || !libres.contains(mesaOpt.get())) {
            System.out.println("Mesa inválida o no está libre.");
            return;
        }
        Mesa mesa = mesaOpt.get();
        try {
            Pedido nuevo = new Pedido(0, mesa, mesero, EstadoPedido.RECIBIDO);
            Pedido creado = pedidoManager.crear(nuevo);
            mesaManager.actualizarEstado(mesa.getId(), EstadoMesa.OCUPADA);
            System.out.printf("Pedido #%d creado para la mesa %d.%n", creado.getId(), mesa.getNumero());
        } catch (Exception e) {
            System.out.println("Error creando pedido: " + e.getMessage());
        }
    }

    private void agregarItemAPedido() {
        listarPedidos();
        long pedidoId = leerEntero("ID del pedido: ");
        Optional<Pedido> pedidoOpt = pedidoManager.buscarPorId(pedidoId);
        if (pedidoOpt.isEmpty()) {
            System.out.println("Pedido no encontrado.");
            return;
        }
        listarProductos();
        long productoId = leerEntero("ID del producto: ");
        Optional<Producto> productoOpt = productoManager.buscarPorId(productoId);
        if (productoOpt.isEmpty()) {
            System.out.println("Producto no encontrado.");
            return;
        }
        int cantidad = leerEntero("Cantidad: ");
        String notas = leerLinea("Notas (vacío para ninguna): ");
        try {
            ItemPedido item = new ItemPedido(0, pedidoOpt.get(), productoOpt.get(), cantidad, notas);
            itemPedidoManager.crear(item);
            System.out.println("Item agregado al pedido.");
        } catch (Exception e) {
            System.out.println("Error agregando item: " + e.getMessage());
        }
    }

    private void modificarItemPedido() {
        listarPedidos();
        long pedidoId = leerEntero("ID del pedido: ");
        List<ItemPedido> items = itemPedidoManager.buscarPorPedido(pedidoId);
        if (items.isEmpty()) {
            System.out.println("No hay items para ese pedido.");
            return;
        }
        System.out.println("Items:");
        for (ItemPedido it : items) {
            System.out.printf("Id:%d | Producto:%s | Cant:%d | Notas:%s%n",
                    it.getId(), it.getProducto().getNombre(), it.getCantidad(), it.getNotas());
        }
        long itemId = leerEntero("Id del item a modificar: ");
        int nuevaCantidad = leerEntero("Nueva cantidad (0 para no cambiar): ");
        String nuevasNotas = leerLinea("Nuevas notas (vacío para no cambiar): ");
        try {
            if (nuevaCantidad > 0) {
                boolean ok = itemPedidoManager.actualizarCantidad(itemId, nuevaCantidad);
                System.out.println(ok ? "Cantidad actualizada." : "Item no encontrado.");
            }
            if (!nuevasNotas.trim().isEmpty()) {
                boolean ok2 = itemPedidoManager.actualizarNotas(itemId, nuevasNotas);
                System.out.println(ok2 ? "Notas actualizadas." : "Item no encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Error modificando item: " + e.getMessage());
        }
    }

    private void cambiarEstadoPedido() {
        listarPedidos();
        long pedidoId = leerEntero("ID del pedido: ");
        EstadoPedido[] estados = EstadoPedido.values();
        System.out.println("Estados disponibles:");
        for (int i = 0; i < estados.length; i++) {
            System.out.printf("%d. %s%n", i + 1, estados[i]);
        }
        int opcion = leerEntero("Selecciona el nuevo estado: ");
        if (opcion < 1 || opcion > estados.length) {
            System.out.println("Estado no válido.");
            return;
        }
        boolean actualizado = historialManager.registrarCambioEstado(
                pedidoManager, pedidoId, estados[opcion - 1], mesero);
        if (actualizado) {
            System.out.println("Estado actualizado y registrado.");
        } else if (pedidoManager.buscarPorId(pedidoId).isEmpty()) {
            System.out.println("No existe un pedido con ese ID.");
        } else {
            System.out.println("El pedido ya tiene ese estado.");
        }
    }

    private void eliminarPedido() {
        listarPedidos();
        long id = leerEntero("ID del pedido a eliminar: ");
        boolean ok = pedidoManager.eliminar(id);
        System.out.println(ok ? "Pedido eliminado." : "No se encontró el pedido.");
    }

    /* ---------- MENÚ USUARIOS ---------- */
    private void menuUsuarios() {
        boolean regresar = false;
        while (!regresar) {
            System.out.println("\n--- Usuarios ---");
            System.out.println("1. Listar usuarios");
            System.out.println("2. Crear usuario");
            System.out.println("3. Cambiar rol");
            System.out.println("4. Activar/Desactivar usuario");
            System.out.println("5. Eliminar usuario");
            System.out.println("0. Volver");
            int op = leerEntero("Opción: ");
            switch (op) {
                case 1: listarUsuarios(); break;
                case 2: crearUsuario(); break;
                case 3: cambiarRolUsuario(); break;
                case 4: cambiarEstadoUsuario(); break;
                case 5: eliminarUsuario(); break;
                case 0: regresar = true; break;
                default: System.out.println("Opción no válida.");
            }
        }
    }

    private void listarUsuarios() {
        List<Usuario> lista = usuarioManager.listar();
        if (lista.isEmpty()) {
            System.out.println("No hay usuarios.");
            return;
        }
        for (Usuario u : lista) {
            System.out.printf("Id:%d | %s %s | Acceso:%s | Rol:%s | Activo:%s%n",
                    u.getId(), u.getNombre(), u.getApellido(), u.getNombreAcceso(), u.getRol(), u.isActivo());
        }
    }

    private void crearUsuario() {
        try {
            String nombre = leerLinea("Nombre: ");
            String apellido = leerLinea("Apellido: ");
            String acceso = leerLinea("Nombre de acceso: ");
            System.out.println("Roles disponibles:");
            for (RolUsuario r : RolUsuario.values()) System.out.println(r);
            String rolStr = leerLinea("Rol (nombre): ");
            RolUsuario rol = RolUsuario.valueOf(rolStr.trim().toUpperCase());
            Usuario u = new Usuario(0, nombre, apellido, acceso, rol, true);
            usuarioManager.crear(u);
            System.out.println("Usuario creado.");
        } catch (Exception e) {
            System.out.println("Error creando usuario: " + e.getMessage());
        }
    }

    private void cambiarRolUsuario() {
        listarUsuarios();
        long id = leerEntero("Id usuario: ");
        System.out.println("Roles:");
        for (RolUsuario r : RolUsuario.values()) System.out.println(r);
        String rolStr = leerLinea("Nuevo rol: ");
        try {
            RolUsuario rol = RolUsuario.valueOf(rolStr.trim().toUpperCase());
            boolean ok = usuarioManager.actualizarRol(id, rol);
            System.out.println(ok ? "Rol actualizado." : "Usuario no encontrado.");
        } catch (Exception e) {
            System.out.println("Rol inválido.");
        }
    }

    private void cambiarEstadoUsuario() {
        listarUsuarios();
        long id = leerEntero("Id usuario: ");
        boolean activo = leerEntero("1=activar, 0=desactivar: ") == 1;
        boolean ok = usuarioManager.actualizarEstado(id, activo);
        System.out.println(ok ? "Estado actualizado." : "Usuario no encontrado.");
    }

    private void eliminarUsuario() {
        listarUsuarios();
        long id = leerEntero("Id usuario a eliminar: ");
        boolean ok = usuarioManager.eliminar(id);
        System.out.println(ok ? "Usuario eliminado." : "Usuario no encontrado.");
    }

    /* ---------- MENÚ FACTURAS ---------- */
    private void menuFacturas() {
        boolean regresar = false;
        while (!regresar) {
            System.out.println("\n--- Facturas ---");
            System.out.println("1. Crear factura para pedido");
            System.out.println("2. Consultar factura por pedido");
            System.out.println("0. Volver");
            int op = leerEntero("Opción: ");
            switch (op) {
                case 1: crearFacturaParaPedido(); break;
                case 2: consultarFacturaPedido(); break;
                case 0: regresar = true; break;
                default: System.out.println("Opción no válida.");
            }
        }
    }

    private void crearFacturaParaPedido() {
        listarPedidos();
        long pedidoId = leerEntero("ID del pedido para facturar: ");
        Optional<Pedido> pedidoOpt = pedidoManager.buscarPorId(pedidoId);
        if (pedidoOpt.isEmpty()) {
            System.out.println("Pedido no encontrado.");
            return;
        }
        try {
                Factura factura = new Factura(0, pedidoOpt.get(), BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO);
            facturaManager.crear(factura);
            System.out.println("Factura creada para el pedido.");
            mesaManager.actualizarEstado(pedidoOpt.get().getMesa().getId(), EstadoMesa.LIBRE);
        } catch (Exception e) {
            System.out.println("Error creando factura: " + e.getMessage());
        }
    }

    private void consultarFacturaPedido() {
        listarPedidos();
        long pedidoId = leerEntero("ID del pedido: ");
        Optional<Factura> f = facturaManager.buscarPorPedido(pedidoId);
        if (f.isPresent()) {
            Factura factura = f.get();
            System.out.printf("Factura Id:%d | Pedido:%d | Total:%s%n",
                    factura.getId(), factura.getPedido().getId(), factura.getTotal());
        } else {
            System.out.println("No existe factura para ese pedido.");
        }
    }

    /* ---------- HISTORIAL ---------- */
    private void mostrarHistorial() {
        List<HistorialEstado> registros = historialManager.listar();
        if (registros.isEmpty()) {
            System.out.println("Todavía no hay cambios de estado registrados.");
            return;
        }
        System.out.println("\nHistorial de estados:");
        for (HistorialEstado registro : registros) {
            Usuario responsable = registro.getResponsable();
            System.out.printf("Registro Id:%d | Pedido:%d | %s -> %s | %s | Responsable: %s %s%n",
                    registro.getId(),
                    registro.getPedido().getId(),
                    registro.getEstadoAnterior(),
                    registro.getEstadoNuevo(),
                    registro.getFechaHora(),
                    responsable.getNombre(),
                    responsable.getApellido());
        }
    }

    /* ---------- UTILIDADES DE ENTRADA ---------- */
    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = entrada.nextLine();
            if (linea == null) return 0;
            linea = linea.trim();
            if (linea.isEmpty()) return 0;
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un número válido.");
            }
        }
    }

    private String leerLinea(String mensaje) {
        System.out.print(mensaje);
        String linea = entrada.nextLine();
        return linea == null ? "" : linea.trim();
    }
}
