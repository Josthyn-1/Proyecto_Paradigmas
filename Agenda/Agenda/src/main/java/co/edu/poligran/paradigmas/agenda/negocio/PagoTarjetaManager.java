package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.PagoTarjeta;
import co.edu.poligran.paradigmas.agenda.persistencia.PagoTarjetaDAO;

public class PagoTarjetaManager extends PagoManager<PagoTarjeta> {
    public PagoTarjetaManager() {
        super(new PagoTarjetaDAO());
    }
}
