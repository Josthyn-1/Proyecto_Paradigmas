package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.Pago;
import co.edu.poligran.paradigmas.agenda.persistencia.PagoDAO;

import java.util.List;

public abstract class PagoManager<T extends Pago> extends BaseManager<T> {
    private final PagoDAO<T> pagoDAO;

    protected PagoManager(PagoDAO<T> pagoDAO) {
        super(pagoDAO);
        this.pagoDAO = pagoDAO;
    }

    public List<T> buscarPorFactura(long facturaId) {
        return pagoDAO.buscarPorFactura(registros(), facturaId);
    }
}
