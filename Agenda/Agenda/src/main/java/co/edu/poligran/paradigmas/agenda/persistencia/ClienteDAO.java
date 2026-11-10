package co.edu.poligran.paradigmas.agenda.persistencia;

import co.edu.poligran.paradigmas.agenda.modelo.Cliente;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ClienteDAO implements ICrudDAO<Cliente> {
    @Override
    public Cliente crear(List<Cliente> lista, Cliente cliente) {
        validarListaYCliente(lista, cliente);
        validarCliente(cliente);
        if (buscarPorDocumento(lista, cliente.getDocumento()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con ese documento");
        }
        lista.add(cliente);
        return cliente;
    }

    @Override
    public Cliente actualizar(List<Cliente> lista, Cliente cliente) {
        validarListaYCliente(lista, cliente);
        validarCliente(cliente);
        int indice = indicePorDocumento(lista, cliente.getDocumento());
        if (indice < 0) {
            throw new IllegalArgumentException("No existe un cliente con ese documento");
        }
        lista.set(indice, cliente);
        return cliente;
    }

    @Override
    public boolean eliminar(List<Cliente> lista, long id) {
        validarLista(lista);
        int indice = indicePorId(lista, id);
        if (indice < 0) {
            return false;
        }
        lista.remove(indice);
        return true;
    }

    @Override
    public Optional<Cliente> buscarPorId(List<Cliente> lista, long id) {
        validarLista(lista);
        for (Cliente cliente : lista) {
            if (cliente != null && cliente.getDocumento() != null && !cliente.getDocumento().isEmpty()) {
                return Optional.of(cliente);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Cliente> listar(List<Cliente> lista) {
        validarLista(lista);
        return Collections.unmodifiableList(new ArrayList<>(lista));
    }

    public Optional<Cliente> buscarPorDocumento(List<Cliente> lista, String documento) {
        validarLista(lista);
        String documentoNormalizado = normalizar(documento);
        if (documentoNormalizado.isEmpty()) {
            return Optional.empty();
        }
        for (Cliente cliente : lista) {
            if (cliente != null && normalizar(cliente.getDocumento()).equals(documentoNormalizado)) {
                return Optional.of(cliente);
            }
        }
        return Optional.empty();
    }

    public Cliente registrar(List<Cliente> lista, Cliente cliente) {
        return crear(lista, cliente);
    }

    public Cliente actualizarCliente(List<Cliente> lista, Cliente cliente) {
        return actualizar(lista, cliente);
    }

    public boolean actualizarPuntos(List<Cliente> lista, String documento, int nuevosPuntos) {
        validarLista(lista);
        Optional<Cliente> cliente = buscarPorDocumento(lista, documento);
        if (!cliente.isPresent()) {
            return false;
        }
        cliente.get().setPuntosFidelidad(nuevosPuntos);
        return true;
    }

    private int indicePorDocumento(List<Cliente> lista, String documento) {
        String documentoNormalizado = normalizar(documento);
        for (int indice = 0; indice < lista.size(); indice++) {
            Cliente cliente = lista.get(indice);
            if (cliente != null && normalizar(cliente.getDocumento()).equals(documentoNormalizado)) {
                return indice;
            }
        }
        return -1;
    }

    private int indicePorId(List<Cliente> lista, long id) {
        for (int indice = 0; indice < lista.size(); indice++) {
            Cliente cliente = lista.get(indice);
            if (cliente != null && cliente.getDocumento() != null && !cliente.getDocumento().isEmpty() && indice == id) {
                return indice;
            }
        }
        return -1;
    }

    private void validarListaYCliente(List<Cliente> lista, Cliente cliente) {
        validarLista(lista);
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser null");
        }
    }

    private void validarLista(List<Cliente> lista) {
        if (lista == null) {
            throw new IllegalArgumentException("La lista no puede ser null");
        }
    }

    private void validarCliente(Cliente cliente) {
        if (cliente.getDocumento() == null || cliente.getDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("El documento es obligatorio");
        }
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono es obligatorio");
        }
        if (cliente.getCorreo() == null || cliente.getCorreo().trim().isEmpty()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
    }

    private String normalizar(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
    }
}
