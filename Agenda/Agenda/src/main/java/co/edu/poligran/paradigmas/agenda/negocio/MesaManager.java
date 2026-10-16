package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.EstadoMesa;
import co.edu.poligran.paradigmas.agenda.modelo.Mesa;
import co.edu.poligran.paradigmas.agenda.persistencia.MesaDAO;

import java.util.List;
import java.util.Optional;

public class MesaManager extends BaseManager<Mesa> {
    private final MesaDAO mesaDAO;

    public MesaManager() {
        this(new MesaDAO());
    }

    private MesaManager(MesaDAO mesaDAO) {
        super(mesaDAO);
        this.mesaDAO = mesaDAO;
    }

    public Optional<Mesa> buscarPorNumero(int numero) {
        return mesaDAO.buscarPorNumero(registros(), numero);
    }

    public List<Mesa> buscarPorEstado(EstadoMesa estado) {
        return mesaDAO.buscarPorEstado(registros(), estado);
    }

    public List<Mesa> consultarLibres() {
        return mesaDAO.consultarLibres(registros());
    }

    public List<Mesa> consultarOcupadas() {
        return mesaDAO.consultarOcupadas(registros());
    }

    public boolean actualizarEstado(long id, EstadoMesa estado) {
        return mesaDAO.actualizarEstado(registros(), id, estado);
    }
}
