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

class FacturaManagerReporteTest {

    @Test
    void debeGenerarReporteFinancieroPorRangoDeFechas() {
        FacturaManager manager = new FacturaManager();

        Usuario mesero = new Usuario(1, "Ana", "Pérez", "ana", RolUsuario.MESERO, true);
        Mesa mesa = new Mesa(1, 1, 4, EstadoMesa.LIBRE);
        Pedido pedido1 = new Pedido(1, mesa, mesero, EstadoPedido.ENTREGADO);
        Pedido pedido2 = new Pedido(2, mesa, mesero, EstadoPedido.ENTREGADO);

        Factura f1 = new Factura(1, pedido1, new BigDecimal("20000"), new BigDecimal("1800"), new BigDecimal("1200"),
                Instant.parse("2026-01-10T12:00:00Z"));
        Factura f2 = new Factura(2, pedido2, new BigDecimal("30000"), new BigDecimal("2700"), new BigDecimal("1800"),
                Instant.parse("2026-01-12T12:00:00Z"));

        manager.crear(f1);
        manager.crear(f2);

        FacturaManager.ReporteFinanciero reporte = manager.generarReporteFinanciero(
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-31T23:59:59Z"));

        Assertions.assertEquals(0, new BigDecimal("57500.00").compareTo(reporte.getTotalRecaudado()));
        Assertions.assertEquals(2, reporte.getCantidadFacturas());
    }
}
