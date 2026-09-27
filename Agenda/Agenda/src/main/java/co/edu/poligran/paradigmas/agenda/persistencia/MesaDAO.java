package co.edu.poligran.paradigmas.agenda.persistencia;

import co.edu.poligran.paradigmas.agenda.modelo.EstadoMesa;
import co.edu.poligran.paradigmas.agenda.modelo.Mesa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MesaDAO implements ICrudDAO<Mesa> {
    @Override
    public Mesa crear(List<Mesa> lista, Mesa mesa) {
        validarListaYMesa(lista, mesa);
        validarDatosMesa(mesa);
        if (buscarPorNumero(lista, mesa.getNumero()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una mesa con ese número");
        }
        if (mesa.getId() <= 0) {
            mesa.setId(siguienteId(lista));
        } else if (buscarPorId(lista, mesa.getId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una mesa con ese ID");
        }
        lista.add(mesa);
        return mesa;
    }

    @Override
    public Mesa actualizar(List<Mesa> lista, Mesa mesa) {
        validarListaYMesa(lista, mesa);
        validarDatosMesa(mesa);
        int indice = indicePorId(lista, mesa.getId());
        if (indice < 0) {
            throw new IllegalArgumentException("No existe una mesa con ese ID");
        }
        Optional<Mesa> mesaConNumero = buscarPorNumero(lista, mesa.getNumero());
        if (mesaConNumero.isPresent() && mesaConNumero.get().getId() != mesa.getId()) {
            throw new IllegalArgumentException("Ya existe una mesa con ese número");
        }
        lista.set(indice, mesa);
        return mesa;
    }

    @Override
    public boolean eliminar(List<Mesa> lista, long id) {
        validarLista(lista);
        int indice = indicePorId(lista, id);
        if (indice < 0) {
            return false;
        }
        lista.remove(indice);
        return true;
    }

    @Override
    public Optional<Mesa> buscarPorId(List<Mesa> lista, long id) {
        validarLista(lista);
        for (Mesa mesa : lista) {
            if (mesa != null && mesa.getId() == id) {
                return Optional.of(mesa);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Mesa> listar(List<Mesa> lista) {
        validarLista(lista);
        return Collections.unmodifiableList(new ArrayList<>(lista));
    }

    public Optional<Mesa> buscarPorNumero(List<Mesa> lista, int numero) {
        validarLista(lista);
        for (Mesa mesa : lista) {
            if (mesa != null && mesa.getNumero() == numero) {
                return Optional.of(mesa);
            }
        }
        return Optional.empty();
    }

    public List<Mesa> buscarPorEstado(List<Mesa> lista, EstadoMesa estado) {
        validarLista(lista);
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser null");
        }
        List<Mesa> resultado = new ArrayList<>();
        for (Mesa mesa : lista) {
            if (mesa != null && mesa.getEstado() == estado) {
                resultado.add(mesa);
            }
        }
        return Collections.unmodifiableList(resultado);
    }

    public List<Mesa> consultarLibres(List<Mesa> lista) {
        return buscarPorEstado(lista, EstadoMesa.LIBRE);
    }

    public List<Mesa> consultarOcupadas(List<Mesa> lista) {
        return buscarPorEstado(lista, EstadoMesa.OCUPADA);
    }

    public boolean actualizarEstado(List<Mesa> lista, long id, EstadoMesa estado) {
        validarLista(lista);
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser null");
        }
        Optional<Mesa> mesa = buscarPorId(lista, id);
        mesa.ifPresent(valor -> valor.setEstado(estado));
        return mesa.isPresent();
    }

    public long siguienteId(List<Mesa> lista) {
        validarLista(lista);
        long maximo = 0;
        for (Mesa mesa : lista) {
            if (mesa != null && mesa.getId() > maximo) {
                maximo = mesa.getId();
            }
        }
        return maximo + 1;
    }

    private int indicePorId(List<Mesa> lista, long id) {
        for (int indice = 0; indice < lista.size(); indice++) {
            Mesa mesa = lista.get(indice);
            if (mesa != null && mesa.getId() == id) {
                return indice;
            }
        }
        return -1;
    }

    private void validarListaYMesa(List<Mesa> lista, Mesa mesa) {
        validarLista(lista);
        if (mesa == null) {
            throw new IllegalArgumentException("La mesa no puede ser null");
        }
    }

    private void validarLista(List<Mesa> lista) {
        if (lista == null) {
            throw new IllegalArgumentException("La lista no puede ser null");
        }
    }

    private void validarDatosMesa(Mesa mesa) {
        if (mesa.getNumero() <= 0) {
            throw new IllegalArgumentException("El número de mesa debe ser mayor que cero");
        }
        if (mesa.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero");
        }
        if (mesa.getEstado() == null) {
            throw new IllegalArgumentException("El estado de la mesa es obligatorio");
        }
    }
}