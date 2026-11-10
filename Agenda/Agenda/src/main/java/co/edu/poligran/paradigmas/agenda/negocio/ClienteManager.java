package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.Cliente;
import co.edu.poligran.paradigmas.agenda.persistencia.ClienteDAO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ClienteManager extends BaseManager<Cliente> {
    private final ClienteDAO clienteDAO;

    public ClienteManager() {
        this(new ClienteDAO());
    }

    private ClienteManager(ClienteDAO clienteDAO) {
        super(clienteDAO);
        this.clienteDAO = clienteDAO;
    }

    public Cliente registrarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser null");
        }
        if (cliente.getPuntosFidelidad() < 0) {
            throw new IllegalArgumentException("Los puntos de fidelidad no pueden ser negativos");
        }
        return super.crear(cliente);
    }

    public Optional<Cliente> buscarPorDocumento(String documento) {
        return clienteDAO.buscarPorDocumento(registros(), documento);
    }

    public Cliente actualizarCliente(Cliente cliente) {
        return super.actualizar(cliente);
    }

    public void acumularPuntosPorFacturaPagada(String documento, BigDecimal totalPagado) {
        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("El documento es obligatorio");
        }
        if (totalPagado == null || totalPagado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El total pagado debe ser mayor a cero");
        }

        Cliente cliente = buscarPorDocumento(documento)
                .orElseThrow(() -> new IllegalArgumentException("No existe un cliente con ese documento"));

        int puntosNuevos = totalPagado.divide(BigDecimal.valueOf(1000), 0, RoundingMode.FLOOR).intValue();
        cliente.setPuntosFidelidad(cliente.getPuntosFidelidad() + puntosNuevos);
        super.actualizar(cliente);
    }
}
