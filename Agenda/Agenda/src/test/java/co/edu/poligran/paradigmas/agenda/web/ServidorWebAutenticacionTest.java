package co.edu.poligran.paradigmas.agenda.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.InetAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServidorWebAutenticacionTest {
    private HttpServer server;
    private HttpClient administrador;
    private URI base;

    @BeforeEach
    void iniciarServidor() throws IOException {
        ServidorWeb app = new ServidorWeb();
        app.datosDemo();
        server = app.crearServidor(0);
        server.start();

        String host = server.getAddress().getAddress().getHostAddress();
        if (host.contains(":")) {
            host = "[" + host + "]";
        }
        base = URI.create("http://" + host + ":" + server.getAddress().getPort());
        administrador = clienteConCookies();
    }

    @AfterEach
    void detenerServidor() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void requiereInicioDeSesionYRechazaCredencialesIncorrectas() throws Exception {
        assertEquals(401, solicitar(administrador, "GET", "/api/session", null).statusCode());
        assertEquals(401, solicitar(administrador, "POST", "/api/login",
                formulario("usuario", "admin", "clave", "incorrecta")).statusCode());

        HttpResponse<String> ingreso = solicitar(administrador, "POST", "/api/login",
                formulario("usuario", "admin", "clave", "admin"));
        assertEquals(200, ingreso.statusCode());
        assertTrue(ingreso.body().contains("\"rol\":\"ADMINISTRADOR\""));
        assertFalse(ingreso.body().contains("claveHash"));
        assertEquals(200, solicitar(administrador, "GET", "/api/session", null).statusCode());

        assertEquals(200, solicitar(administrador, "POST", "/api/logout", "").statusCode());
        assertEquals(401, solicitar(administrador, "GET", "/api/session", null).statusCode());
    }

    @Test
    void administradorGestionaUsuariosYOtrosRolesNoPueden() throws Exception {
        assertEquals(200, solicitar(administrador, "POST", "/api/login",
                formulario("usuario", "admin", "clave", "admin")).statusCode());
        HttpResponse<String> creado = solicitar(administrador, "POST", "/api/usuarios/crear",
                formulario("nombre", "Ana", "apellido", "Mesera", "nombreAcceso", "ana",
                        "clave", "clave-segura", "rol", "MESERO"));
        assertEquals(200, creado.statusCode());
        assertTrue(creado.body().contains("\"nombreAcceso\":\"ana\""));

        HttpClient mesera = clienteConCookies();
        assertEquals(200, solicitar(mesera, "POST", "/api/login",
                formulario("usuario", "ana", "clave", "clave-segura")).statusCode());
        assertEquals(403, solicitar(mesera, "POST", "/api/usuarios/crear",
                formulario("nombre", "Carlos", "apellido", "Cocina", "nombreAcceso", "carlos",
                        "clave", "clave-segura", "rol", "COCINERO")).statusCode());
        assertFalse(solicitar(mesera, "GET", "/api/session", null).body().contains("\"usuarios\""));
        assertEquals(400, solicitar(administrador, "POST", "/api/usuarios/rol",
                formulario("id", "1", "rol", "MESERO")).statusCode());
        assertEquals(400, solicitar(administrador, "POST", "/api/usuarios/eliminar",
                formulario("id", "1")).statusCode());
    }

    private HttpClient clienteConCookies() {
        CookieManager cookies = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        return HttpClient.newBuilder().cookieHandler(cookies).build();
    }

    private HttpResponse<String> solicitar(HttpClient cliente, String metodo, String ruta, String cuerpo)
            throws IOException, InterruptedException {
        HttpRequest.Builder solicitud = HttpRequest.newBuilder(base.resolve(ruta));
        if ("GET".equals(metodo)) {
            solicitud.GET();
        } else {
            solicitud.header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpo));
        }
        return cliente.send(solicitud.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String formulario(String... pares) {
        StringBuilder formulario = new StringBuilder();
        for (int i = 0; i < pares.length; i += 2) {
            if (formulario.length() > 0) {
                formulario.append('&');
            }
            formulario.append(URLEncoder.encode(pares[i], StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(pares[i + 1], StandardCharsets.UTF_8));
        }
        return formulario.toString();
    }
}
