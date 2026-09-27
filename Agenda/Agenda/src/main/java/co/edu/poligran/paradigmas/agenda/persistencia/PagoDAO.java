package co.edu.poligran.paradigmas.agenda.persistencia;

import co.edu.poligran.paradigmas.agenda.modelo.Pago;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class PagoDAO<T extends Pago> implements ICrudDAO<T> {
    @Override
    public T crear(List<T> lista, T pago) {
        validarListaYPago(lista, pago);
        validarDatosPago(pago);
        validarDatosEspecificos(pago);
        if (pago.getId() <= 0) {
            pago.setId(siguienteId(lista));
        } else if (buscarPorId(lista, pago.getId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un pago con ese ID");
        }
        lista.add(pago);
        return pago;
    }

    @Override
    public T actualizar(List<T> lista, T pago) {
        validarLista(lista);
        throw new UnsupportedOperationException("Los pagos registrados no se pueden modificar");
    }

    @Override
    public boolean eliminar(List<T> lista, long id) {
        validarLista(lista);
        return false;
    }

    @Override
    public Optional<T> buscarPorId(List<T> lista, long id) {
        validarLista(lista);
        for (T pago : lista) {
            if (pago != null && pago.getId() == id) {
                return Optional.of(pago);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<T> listar(List<T> lista) {
        validarLista(lista);
        return Collections.unmodifiableList(new ArrayList<>(lista));
    }

    public List<T> buscarPorFactura(List<T> lista, long facturaId) {
        validarLista(lista);
        List<T> resultado = new ArrayList<>();
        for (T pago : lista) {
            if (pago != null && pago.getFactura() != null
                    && pago.getFactura().getId() == facturaId) {
                resultado.add(pago);
            }
        }
        return Collections.unmodifiableList(resultado);
    }

    public long siguienteId(List<T> lista) {
        validarLista(lista);
        long maximo = 0;
        for (T pago : lista) {
            if (pago != null && pago.getId() > maximo) {
                maximo = pago.getId();
            }
        }
        return maximo + 1;
    }

    protected abstract void validarDatosEspecificos(T pago);

    private void validarDatosPago(T pago) {
        if (pago.getFactura() == null) {
            throw new IllegalArgumentException("La factura asociada es obligatoria");
        }
        if (pago.getMonto() == null || pago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor que cero");
        }
        if (pago.getFechaHora() == null) {
            throw new IllegalArgumentException("La fecha y hora del pago son obligatorias");
        }
    }

    private void validarListaYPago(List<T> lista, T pago) {
        validarLista(lista);
        if (pago == null) {
            throw new IllegalArgumentException("El pago no puede ser null");
        }
    }

    private void validarLista(List<T> lista) {
        if (lista == null) {
            throw new IllegalArgumentException("La lista de pagos no puede ser null");
        }
    }
}