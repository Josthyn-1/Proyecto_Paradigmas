package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.Factura;
import co.edu.poligran.paradigmas.agenda.modelo.Pago;
import co.edu.poligran.paradigmas.agenda.persistencia.FacturaDAO;

import java.math.BigDecimal;
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
}
