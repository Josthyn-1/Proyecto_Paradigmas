package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.EstadoMesa;
import co.edu.poligran.paradigmas.agenda.modelo.EstadoPedido;
import co.edu.poligran.paradigmas.agenda.modelo.Factura;
import co.edu.poligran.paradigmas.agenda.modelo.Mesa;
import co.edu.poligran.paradigmas.agenda.modelo.Pedido;
import co.edu.poligran.paradigmas.agenda.modelo.RolUsuario;
import co.edu.poligran.paradigmas.agenda.modelo.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

class FacturaManagerDescuentoTest {

    @Test
    void debeAplicarDescuentoCuandoUsuarioEsAdminOAutorizado() {
        FacturaManager manager = new FacturaManager();
        Usuario admin = new Usuario(1, "Admin", "Sistema", "admin", RolUsuario.ADMINISTRADOR, true);
        Mesa mesa = new Mesa(1, 2, 4, EstadoMesa.LIBRE);
        Pedido pedido = new Pedido(1, mesa, new Usuario(2, "Ana", "Pérez", "ana", RolUsuario.MESERO, true), EstadoPedido.ENTREGADO);
        Factura factura = new Factura(10, pedido, new BigDecimal("10000"), new BigDecimal("1900"), new BigDecimal("100"),
                Instant.parse("2026-01-10T12:00:00Z"));

        BigDecimal total = manager.aplicarDescuento(factura, 10.0, admin);

        Assertions.assertEquals(0, new BigDecimal("10800.00").compareTo(total));
        Assertions.assertEquals(10.0, factura.getDescuento());
        Assertions.assertEquals(10800.0, factura.getTotalConDescuento(), 0.001);
    }

    @Test
    void noDebeAplicarDescuentoCuandoUsuarioNoTienePermiso() {
        FacturaManager manager = new FacturaManager();
        Usuario mesero = new Usuario(2, "Ana", "Pérez", "ana", RolUsuario.MESERO, true);
        Mesa mesa = new Mesa(1, 2, 4, EstadoMesa.LIBRE);
        Pedido pedido = new Pedido(1, mesa, mesero, EstadoPedido.ENTREGADO);
        Factura factura = new Factura(11, pedido, new BigDecimal("10000"), new BigDecimal("1900"), new BigDecimal("100"),
                Instant.parse("2026-01-10T12:00:00Z"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> manager.aplicarDescuento(factura, 10.0, mesero));
    }
}
