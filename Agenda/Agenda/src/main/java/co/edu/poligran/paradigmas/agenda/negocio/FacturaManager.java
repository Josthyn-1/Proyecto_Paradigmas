package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.Factura;
import co.edu.poligran.paradigmas.agenda.modelo.Pago;
import co.edu.poligran.paradigmas.agenda.modelo.RolUsuario;
import co.edu.poligran.paradigmas.agenda.modelo.Usuario;
import co.edu.poligran.paradigmas.agenda.persistencia.FacturaDAO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class FacturaManager extends BaseManager<Factura> {
    private final FacturaDAO facturaDAO;

    public FacturaManager() {
        this(new FacturaDAO());
    }

    private FacturaManager(FacturaDAO facturaDAO) {
        super(facturaDAO);
        this.facturaDAO = facturaDAO;
    }

    public Optional<Factura> buscarPorPedido(long pedidoId) {
        return facturaDAO.buscarPorPedido(registros(), pedidoId);
    }

    public boolean estaSaldada(long facturaId, List<? extends Pago> pagos) {
        return facturaDAO.estaSaldada(registros(), pagos, facturaId);
    }

    public BigDecimal calcularSaldoPendiente(long facturaId, List<? extends Pago> pagos) {
        return facturaDAO.calcularSaldoPendiente(registros(), pagos, facturaId);
    }

    public ReporteFinanciero generarReporteFinanciero(Instant fechaInicio, Instant fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la de fin");
        }

        BigDecimal total = BigDecimal.ZERO;
        int cantidad = 0;
        for (Factura factura : registros()) {
            if (factura == null || factura.getFechaHora() == null) {
                continue;
            }
            Instant fechaFactura = factura.getFechaHora();
            if (!fechaFactura.isBefore(fechaInicio) && !fechaFactura.isAfter(fechaFin)) {
                total = total.add(factura.getTotal());
                cantidad++;
            }
        }
        return new ReporteFinanciero(total, cantidad);
    }

    public BigDecimal aplicarDescuento(Factura factura, double porcentajeDescuento, Usuario usuario) {
        validarFacturaYUsuario(factura, usuario);
        validarPorcentajeDescuento(porcentajeDescuento);

        BigDecimal total = factura.getTotal();
        BigDecimal porcentaje = BigDecimal.valueOf(porcentajeDescuento)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal descuento = total.multiply(porcentaje);
        BigDecimal totalConDescuento = total.subtract(descuento);

        factura.setDescuento(porcentajeDescuento);
        factura.setTotalConDescuento(totalConDescuento.doubleValue());
        return totalConDescuento;
    }

    public boolean puedeAplicarDescuento(Usuario usuario) {
        return usuario != null
                && usuario.isActivo()
                && (usuario.getRol() == RolUsuario.ADMINISTRADOR || usuario.getRol() == RolUsuario.CAJERO);
    }

    private void validarFacturaYUsuario(Factura factura, Usuario usuario) {
        if (factura == null) {
            throw new IllegalArgumentException("La factura no puede ser null");
        }
        if (!puedeAplicarDescuento(usuario)) {
            throw new IllegalArgumentException("Solo el administrador o un cajero autorizado puede aplicar descuentos");
        }
    }

    private void validarPorcentajeDescuento(double porcentajeDescuento) {
        if (porcentajeDescuento < 0 || porcentajeDescuento > 100) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100");
        }
    }

    public static class ReporteFinanciero {
        private final BigDecimal totalRecaudado;
        private final int cantidadFacturas;

        public ReporteFinanciero(BigDecimal totalRecaudado, int cantidadFacturas) {
            this.totalRecaudado = totalRecaudado == null ? BigDecimal.ZERO : totalRecaudado;
            this.cantidadFacturas = cantidadFacturas;
        }

        public BigDecimal getTotalRecaudado() {
            return totalRecaudado;
        }

        public int getCantidadFacturas() {
            return cantidadFacturas;
        }
    }
}
