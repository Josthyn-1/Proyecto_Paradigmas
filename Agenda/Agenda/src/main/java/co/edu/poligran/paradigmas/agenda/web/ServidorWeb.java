package co.edu.poligran.paradigmas.agenda.web;

import co.edu.poligran.paradigmas.agenda.modelo.*;
import co.edu.poligran.paradigmas.agenda.negocio.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
    private final Usuario mesero = new Usuario(1, "Mesero", "Demo", "mesero", RolUsuario.MESERO, true);

    public static void main(String[] args) throws IOException {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        int intento = puerto;
        while (true) {
            try {
                ServidorWeb app = new ServidorWeb();
                app.datosDemo();
                HttpServer server = HttpServer.create(new InetSocketAddress(intento), 0);
                server.createContext("/", app::manejar);
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

    private void datosDemo() {
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
                    cuerpo = api(ruta, leerForm(ex));
                }
                enviar(ex, 200, "application/json", cuerpo.getBytes(StandardCharsets.UTF_8));
            } else {
                estatico(ex, ruta);
            }
        } catch (Exception e) {
            String msg = e.getMessage() == null ? e.toString() : e.getMessage();
            enviar(ex, 400, "application/json", json(m("error", msg)).getBytes(StandardCharsets.UTF_8));
        }
    }

    private String api(String ruta, Map<String, String> p) {
        switch (ruta) {
            case "/api/datos":
                break;
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
                pedidos.crear(new Pedido(0, mesa, mesero, EstadoPedido.RECIBIDO));
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
                historial.registrarCambioEstado(pedidos, num(p, "id"), EstadoPedido.valueOf(p.get("estado")), mesero);
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
            default:
                throw new IllegalArgumentException("Ruta desconocida: " + ruta);
        }

        return json(datos());
    }

    private Map<String, Object> datos() {
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

        return m("mesas", lm, "productos", lp, "pedidos", lpe, "facturas", lf);
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
