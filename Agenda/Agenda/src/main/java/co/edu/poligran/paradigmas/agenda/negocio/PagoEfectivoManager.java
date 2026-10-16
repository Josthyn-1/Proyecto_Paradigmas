package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.PagoEfectivo;
import co.edu.poligran.paradigmas.agenda.persistencia.PagoEfectivoDAO;

public class PagoEfectivoManager extends PagoManager<PagoEfectivo> {
    public PagoEfectivoManager() {
        super(new PagoEfectivoDAO());
    }
}
