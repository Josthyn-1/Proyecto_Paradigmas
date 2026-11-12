package co.edu.poligran.paradigmas.agenda.web;

import co.edu.poligran.paradigmas.agenda.modelo.*;
import co.edu.poligran.paradigmas.agenda.negocio.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.*;

/**
 * Servidor web ligero: sirve la interfaz HTML (src/main/resources/web) y expone
 * una API JSON que usa los managers del proyecto. No requiere librerías externas.
 */
public class ServidorWeb {
    private final MesaManager mesas = new MesaManager();
    private final CategoriaManager categorias = new CategoriaManager();
    private final ProductoManager productos = new ProductoManager();
    private final PedidoManager pedidos = new PedidoManager();
    private final ItemPedidoManager items = new ItemPedidoManager();
    private final HistorialEstadoManager historial = new HistorialEstadoManager();
    private final FacturaManager facturas = new FacturaManager();
    private final UsuarioManager usuarios = new UsuarioManager();
    private final SecureRandom random = new SecureRandom();
    private final Map<String, Long> sesiones = new ConcurrentHashMap<>();

    private static final String COOKIE_SESION = "AGENDA_SESSION";
    private static final int ITERACIONES_CLAVE = 120_000;

    public static void main(String[] args) throws IOException {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        int intento = puerto;
        while (true) {
            try {
                ServidorWeb app = new ServidorWeb();
                app.datosDemo();
                HttpServer server = app.crearServidor(intento);
                server.start();
                System.out.println("Abre en el navegador: http://localhost:" + intento);
                return;
            } catch (java.net.BindException e) {
                if (intento == 8080 && args.length == 0) {
                    intento = 8081;
                    continue;
                }
                throw e;
            }
        }
    }

    HttpServer crearServidor(int puerto) throws IOException {
        HttpServer server = HttpServer.create(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), puerto), 0);
        server.createContext("/", this::manejar);
        return server;
    }

    void datosDemo() {
        usuarios.buscarPorNombreAcceso("admin").orElseGet(() -> {
            byte[][] credencial = crearCredencial("admin");
            return usuarios.crear(new Usuario(0, "Administrador", "Sistema", "admin",
                    RolUsuario.ADMINISTRADOR, true, credencial[0], credencial[1]));
        });

        for (int i = 1; i <= 6; i++) {
            mesas.crear(new Mesa(0, i, i % 2 == 0 ? 4 : 2, EstadoMesa.LIBRE));
        }

        Categoria bebidas = categorias.crear(new Categoria(0, "Bebidas", true));
        Categoria platos = categorias.crear(new Categoria(0, "Platos", true));
        Categoria postres = categorias.crear(new Categoria(0, "Postres", true));

        productos.crear(new Producto(0, "Limonada", bebidas, new BigDecimal("4.50"), true, "Limonada natural"));
        productos.crear(new Producto(0, "Café", bebidas, new BigDecimal("2.80"), true, "Café negro"));
        productos.crear(new Producto(0, "Pasta Alfredo", platos, new BigDecimal("12.90"), true, "Pasta cremosa con pollo"));
        productos.crear(new Producto(0, "Hamburguesa", platos, new BigDecimal("11.50"), true, "Carne, queso y papas"));
        productos.crear(new Producto(0, "Cheesecake", postres, new BigDecimal("5.90"), true, "Con salsa de frutos rojos"));
    }

    private void manejar(HttpExchange ex) throws IOException {
        String ruta = ex.getRequestURI().getPath();
        try {
            if (ruta.startsWith("/api/")) {
                String cuerpo;
                synchronized (this) {
                    cuerpo = api(ex, ruta, leerForm(ex));
                }
                enviar(ex, 200, "application/json", cuerpo.getBytes(StandardCharsets.UTF_8));
            } else {
                estatico(ex, ruta);
            }
        } catch (ErrorApi e) {
            enviar(ex, e.codigo, "application/json",
                    json(m("error", e.getMessage())).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            String msg = e.getMessage() == null ? e.toString() : e.getMessage();
            enviar(ex, 400, "application/json", json(m("error", msg)).getBytes(StandardCharsets.UTF_8));
        }
    }

    private String api(HttpExchange ex, String ruta, Map<String, String> p) {
        if ("/api/login".equals(ruta)) {
            String nombreAcceso = p.getOrDefault("usuario", "");
            String clave = p.getOrDefault("clave", "");
            Usuario usuario = usuarios.buscarPorNombreAcceso(nombreAcceso)
                    .filter(u -> u.isActivo() && validarClave(clave, u))
                    .orElseThrow(() -> new ErrorApi(401, "Usuario o clave incorrectos"));
            byte[] token = new byte[32];
            random.nextBytes(token);
            String sesion = Base64.getUrlEncoder().withoutPadding().encodeToString(token);
            sesiones.put(sesion, usuario.getId());
            agregarCookie(ex, sesion);
            return json(datos(usuario));
        }
        if ("/api/logout".equals(ruta)) {
            String sesion = tokenSesion(ex);
            if (sesion != null) {
                sesiones.remove(sesion);
            }
            agregarCookie(ex, "");
            return json(m("cerrarSesion", true));
        }

        Usuario actor = usuarioSesion(ex);
        if ("/api/session".equals(ruta) || "/api/datos".equals(ruta)) {
            return json(datos(actor));
        }

        switch (ruta) {
            case "/api/mesa/crear":
                mesas.crear(new Mesa(0, (int) num(p, "numero"), (int) num(p, "capacidad"), EstadoMesa.LIBRE));
                break;
            case "/api/mesa/estado":
                mesas.actualizarEstado(num(p, "id"), EstadoMesa.valueOf(p.get("estado"))); 
                break;
            case "/api/mesa/eliminar":
                mesas.eliminar(num(p, "id"));
                break;
            case "/api/producto/crear": {
                Categoria cat = categorias.buscarPorNombre(p.getOrDefault("categoria", "General"))
                        .orElseGet(() -> categorias.crear(new Categoria(0, p.getOrDefault("categoria", "General"), true)));
                productos.crear(new Producto(0, p.get("nombre"), cat, new BigDecimal(p.get("precio")), true,
                        p.getOrDefault("descripcion", "")));
                break;
            }
            case "/api/producto/disponible":
                productos.actualizarDisponibilidad(num(p, "id"), Boolean.parseBoolean(p.get("disponible")));
                break;
            case "/api/producto/eliminar":
                productos.eliminar(num(p, "id"));
                break;
            case "/api/pedido/crear": {
                Mesa mesa = mesas.buscarPorNumero((int) num(p, "mesa"))
                        .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada"));
                if (mesa.getEstado() != EstadoMesa.LIBRE) {
                    throw new IllegalArgumentException("La mesa no está libre");
                }
                pedidos.crear(new Pedido(0, mesa, actor, EstadoPedido.RECIBIDO));
                mesas.actualizarEstado(mesa.getId(), EstadoMesa.OCUPADA);
                break;
            }
            case "/api/pedido/item": {
                Pedido ped = pedidos.buscarPorId(num(p, "pedido"))
                        .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
                Producto prod = productos.buscarPorId(num(p, "producto"))
                        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
                items.crear(new ItemPedido(0, ped, prod, (int) num(p, "cantidad"), p.getOrDefault("notas", "")));
                break;
            }
            case "/api/pedido/estado":
                historial.registrarCambioEstado(pedidos, num(p, "id"), EstadoPedido.valueOf(p.get("estado")), actor);
                break;
            case "/api/pedido/eliminar":
                pedidos.eliminar(num(p, "id"));
                break;
            case "/api/factura/crear": {
                Pedido ped = pedidos.buscarPorId(num(p, "pedido"))
                        .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
                if (ped.getEstado() == EstadoPedido.CANCELADO || ped.getEstado() == EstadoPedido.PAGADO) {
                    throw new IllegalArgumentException("El pedido no puede facturarse si ya está cancelado o pagado");
                }
                facturas.crear(new Factura(0, ped, totalPedido(ped), BigDecimal.ZERO, BigDecimal.ZERO));
                pedidos.actualizarEstado(ped.getId(), EstadoPedido.PAGADO);
                mesas.actualizarEstado(ped.getMesa().getId(), EstadoMesa.LIBRE);
                break;
            }
            case "/api/usuarios/crear":
                exigirAdministrador(actor);
                String clave = p.getOrDefault("clave", "");
                if (clave.length() < 4) {
                    throw new IllegalArgumentException("La clave debe tener al menos 4 caracteres");
                }
                RolUsuario rolNuevo = rol(p.get("rol"));
                byte[][] credencial = crearCredencial(clave);
                usuarios.crear(new Usuario(0, requerido(p, "nombre"), requerido(p, "apellido"),
                        requerido(p, "nombreAcceso"), rolNuevo, true, credencial[0], credencial[1]));
                break;
            case "/api/usuarios/rol": {
                exigirAdministrador(actor);
                Usuario objetivo = usuarioPorId(num(p, "id"));
                protegerAdministrador(objetivo);
                RolUsuario nuevoRol = rol(p.get("rol"));
                if (objetivo.getId() == actor.getId() && nuevoRol != RolUsuario.ADMINISTRADOR) {
                    throw new IllegalArgumentException("No puedes quitarte el rol de administrador");
                }
                usuarios.actualizarRol(objetivo.getId(), nuevoRol);
                break;
            }
            case "/api/usuarios/estado": {
                exigirAdministrador(actor);
                Usuario objetivo = usuarioPorId(num(p, "id"));
                protegerAdministrador(objetivo);
                if (objetivo.getId() == actor.getId() && Boolean.parseBoolean(p.get("activo")) == false) {
                    throw new IllegalArgumentException("No puedes desactivar tu propia cuenta");
                }
                usuarios.actualizarEstado(objetivo.getId(), Boolean.parseBoolean(p.get("activo")));
                break;
            }
            case "/api/usuarios/eliminar": {
                exigirAdministrador(actor);
                Usuario objetivo = usuarioPorId(num(p, "id"));
                protegerAdministrador(objetivo);
                if (objetivo.getId() == actor.getId()) {
                    throw new IllegalArgumentException("No puedes eliminar tu propia cuenta");
                }
                usuarios.eliminar(objetivo.getId());
                sesiones.entrySet().removeIf(s -> s.getValue().equals(objetivo.getId()));
                break;
            }
            default:
                throw new IllegalArgumentException("Ruta desconocida: " + ruta);
        }

        return json(datos(actor));
    }

    private Map<String, Object> datos(Usuario usuario) {
        List<Object> lm = new ArrayList<>();
        List<Object> lp = new ArrayList<>();
        List<Object> lpe = new ArrayList<>();
        List<Object> lf = new ArrayList<>();

        for (Mesa x : mesas.listar()) {
            lm.add(m("id", x.getId(), "numero", x.getNumero(), "capacidad", x.getCapacidad(), "estado", x.getEstado()));
        }

        for (Producto x : productos.listar()) {
            lp.add(m("id", x.getId(), "nombre", x.getNombre(), "descripcion", x.getDescripcion(),
                    "precio", x.getPrecio(), "disponible", x.isDisponible(),
                    "categoria", x.getCategoria() == null ? "General" : x.getCategoria().getNombre()));
        }

        for (Pedido x : pedidos.listar()) {
            List<Object> li = new ArrayList<>();
            for (ItemPedido i : items.buscarPorPedido(x.getId())) {
                li.add(m("id", i.getId(), "producto", i.getProducto().getNombre(), "cantidad", i.getCantidad(),
                        "notas", i.getObservaciones(), "precio", precio(i)));
            }
            lpe.add(m("id", x.getId(), "mesa", x.getMesa().getNumero(), "estado", x.getEstado(),
                    "items", li, "total", totalPedido(x)));
        }

        for (Factura x : facturas.listar()) {
            lf.add(m("id", x.getId(), "pedido", x.getPedido().getId(), "mesa", x.getPedido().getMesa().getNumero(),
                    "total", x.getTotal(), "fecha", String.valueOf(x.getFechaHora())));
        }

        Map<String, Object> respuesta = m("usuario", usuarioPublico(usuario),
                "mesas", lm, "productos", lp, "pedidos", lpe, "facturas", lf);
        if (usuario.getRol() == RolUsuario.ADMINISTRADOR) {
            List<Object> listaUsuarios = new ArrayList<>();
            for (Usuario u : usuarios.listar()) {
                listaUsuarios.add(usuarioPublico(u));
            }
            respuesta.put("usuarios", listaUsuarios);
        }
        return respuesta;
    }

    private Map<String, Object> usuarioPublico(Usuario usuario) {
        return m("id", usuario.getId(), "nombre", usuario.getNombre(), "apellido", usuario.getApellido(),
                "nombreAcceso", usuario.getNombreAcceso(), "rol", usuario.getRol(), "activo", usuario.isActivo());
    }

    private Usuario usuarioSesion(HttpExchange ex) {
        String sesion = tokenSesion(ex);
        Long id = sesion == null ? null : sesiones.get(sesion);
        Usuario usuario = id == null ? null : usuarios.buscarPorId(id).orElse(null);
        if (usuario == null || !usuario.isActivo()) {
            if (sesion != null) {
                sesiones.remove(sesion);
            }
            throw new ErrorApi(401, "Inicia sesión para continuar");
        }
        return usuario;
    }

    private String tokenSesion(HttpExchange ex) {
        List<String> cookies = ex.getRequestHeaders().get("Cookie");
        if (cookies == null) {
            return null;
        }
        for (String cookie : cookies) {
            for (String parte : cookie.split(";")) {
                String[] valor = parte.trim().split("=", 2);
                if (valor.length == 2 && COOKIE_SESION.equals(valor[0])) {
                    return valor[1];
                }
            }
        }
        return null;
    }

    private void agregarCookie(HttpExchange ex, String token) {
        String valor = COOKIE_SESION + "=" + token + "; Path=/; HttpOnly; SameSite=Strict";
        if (token.isEmpty()) {
            valor += "; Max-Age=0";
        }
        ex.getResponseHeaders().add("Set-Cookie", valor);
    }

    private byte[][] crearCredencial(String clave) {
        byte[] sal = new byte[16];
        random.nextBytes(sal);
        return new byte[][] { derivarClave(clave, sal), sal };
    }

    private boolean validarClave(String clave, Usuario usuario) {
        byte[] hash = usuario.getClaveHash();
        byte[] sal = usuario.getClaveSal();
        return hash != null && sal != null && MessageDigest.isEqual(hash, derivarClave(clave, sal));
    }

    private byte[] derivarClave(String clave, byte[] sal) {
        PBEKeySpec spec = new PBEKeySpec(clave.toCharArray(), sal, ITERACIONES_CLAVE, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo procesar la clave", e);
        } finally {
            spec.clearPassword();
        }
    }

    private Usuario usuarioPorId(long id) {
        return usuarios.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private void exigirAdministrador(Usuario actor) {
        if (actor.getRol() != RolUsuario.ADMINISTRADOR) {
            throw new ErrorApi(403, "Solo un administrador puede gestionar usuarios");
        }
    }

    private void protegerAdministrador(Usuario usuario) {
        if ("admin".equalsIgnoreCase(usuario.getNombreAcceso())) {
            throw new IllegalArgumentException("La cuenta de administrador predeterminada no se puede modificar");
        }
    }

    private String requerido(Map<String, String> p, String campo) {
        String valor = p.get(campo);
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo '" + campo + "' es obligatorio");
        }
        return valor.trim();
    }

    private RolUsuario rol(String valor) {
        try {
            return RolUsuario.valueOf(valor);
        } catch (Exception e) {
            throw new IllegalArgumentException("Rol inválido");
        }
    }

    private static final class ErrorApi extends RuntimeException {
        private final int codigo;

        private ErrorApi(int codigo, String mensaje) {
            super(mensaje);
            this.codigo = codigo;
        }
    }

    private BigDecimal precio(ItemPedido i) {
        BigDecimal pr = i.getPrecioUnitarioHistorico();
        return pr != null ? pr : i.getProducto().getPrecio();
    }

    private BigDecimal totalPedido(Pedido ped) {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido i : items.buscarPorPedido(ped.getId())) {
            total = total.add(precio(i).multiply(BigDecimal.valueOf(i.getCantidad())));
        }
        return total;
    }

    private static Map<String, Object> m(Object... kv) {
        Map<String, Object> r = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            r.put((String) kv[i], kv[i + 1]);
        }
        return r;
    }

    private static long num(Map<String, String> p, String k) {
        try {
            return Long.parseLong(p.get(k).trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Valor numérico inválido en '" + k + "'");
        }
    }

    private static String json(Object o) {
        if (o == null) return "null";
        if (o instanceof Map) {
            StringJoiner sj = new StringJoiner(",", "{", "}");
            for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet()) {
                sj.add(json(String.valueOf(e.getKey())) + ":" + json(e.getValue()));
            }
            return sj.toString();
        }
        if (o instanceof List) {
            StringJoiner sj = new StringJoiner(",", "[", "]");
            for (Object x : (List<?>) o) {
                sj.add(json(x));
            }
            return sj.toString();
        }
        if (o instanceof Number) {
            return o instanceof BigDecimal ? ((BigDecimal) o).toPlainString() : o.toString();
        }
        if (o instanceof Boolean) {
            return o.toString();
        }
        StringBuilder sb = new StringBuilder("\"");
        for (char c : o.toString().toCharArray()) {
            if (c == '"' || c == '\\') {
                sb.append('\\').append(c);
            } else if (c == '\n') {
                sb.append("\\n");
            } else if (c < 32) {
                sb.append(' ');
            } else {
                sb.append(c);
            }
        }
        return sb.append('"').toString();
    }

    private static Map<String, String> leerForm(HttpExchange ex) throws IOException {
        Map<String, String> r = new HashMap<>();
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        for (String par : body.split("&")) {
            if (par.isEmpty()) continue;
            String[] kv = par.split("=", 2);
            r.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                    kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "");
        }
        return r;
    }

    private void estatico(HttpExchange ex, String ruta) throws IOException {
        String archivo = ruta.equals("/") ? "index.html" : ruta.substring(1);
        try (InputStream in = archivo.contains("..") ? null : getClass().getResourceAsStream("/web/" + archivo)) {
            if (in == null) {
                enviar(ex, 404, "text/plain", "No encontrado".getBytes(StandardCharsets.UTF_8));
                return;
            }
            String tipo = archivo.endsWith(".css") ? "text/css"
                    : archivo.endsWith(".js") ? "text/javascript"
                    : archivo.endsWith(".svg") ? "image/svg+xml"
                    : "text/html";
            enviar(ex, 200, tipo + "; charset=utf-8", in.readAllBytes());
        }
    }

    private static void enviar(HttpExchange ex, int codigo, String tipo, byte[] datos) throws IOException {
        ex.getResponseHeaders().set("Content-Type", tipo);
        ex.sendResponseHeaders(codigo, datos.length);
        ex.getResponseBody().write(datos);
        ex.close();
    }
}
