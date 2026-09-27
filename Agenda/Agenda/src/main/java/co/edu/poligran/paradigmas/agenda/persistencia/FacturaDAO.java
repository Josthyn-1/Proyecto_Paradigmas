package co.edu.poligran.paradigmas.agenda.persistencia;

import co.edu.poligran.paradigmas.agenda.modelo.EstadoPedido;
import co.edu.poligran.paradigmas.agenda.modelo.Factura;
import co.edu.poligran.paradigmas.agenda.modelo.Pago;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FacturaDAO implements ICrudDAO<Factura> {
    @Override
    public Factura crear(List<Factura> lista, Factura factura) {
        validarListaYFactura(lista, factura);
        validarDatosFactura(factura);
        if (buscarPorPedido(lista, factura.getPedido().getId()).isPresent()) {
            throw new IllegalArgumentException("El pedido ya tiene una factura");
        }
        if (factura.getId() <= 0) {
            factura.setId(siguienteId(lista));
        } else if (buscarPorId(lista, factura.getId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una factura con ese ID");
        }
        lista.add(factura);
        return factura;
    }

    @Override
    public Factura actualizar(List<Factura> lista, Factura factura) {
        validarListaYFactura(lista, factura);
        validarDatosFactura(factura);
        int indice = indicePorId(lista, factura.getId());
        if (indice < 0) {
            throw new IllegalArgumentException("No existe una factura con ese ID");
        }
        Optional<Factura> facturaDelPedido = buscarPorPedido(lista, factura.getPedido().getId());
        if (facturaDelPedido.isPresent() && facturaDelPedido.get().getId() != factura.getId()) {
            throw new IllegalArgumentException("El pedido ya tiene otra factura");
        }
        lista.set(indice, factura);
        return factura;
    }

    @Override
    public boolean eliminar(List<Factura> lista, long id) {
        validarLista(lista);
        int indice = indicePorId(lista, id);
        if (indice < 0) {
            return false;
        }
        lista.remove(indice);
        return true;
    }

    @Override
    public Optional<Factura> buscarPorId(List<Factura> lista, long id) {
        validarLista(lista);
        for (Factura factura : lista) {
            if (factura != null && factura.getId() == id) {
                return Optional.of(factura);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Factura> listar(List<Factura> lista) {
        validarLista(lista);
        return java.util.Collections.unmodifiableList(new ArrayList<>(lista));
    }

    public Optional<Factura> buscarPorPedido(List<Factura> lista, long pedidoId) {
        validarLista(lista);
        for (Factura factura : lista) {
            if (factura != null && factura.getPedido() != null
                    && factura.getPedido().getId() == pedidoId) {
                return Optional.of(factura);
            }
        }
        return Optional.empty();
    }

    public boolean estaSaldada(List<Factura> facturas, List<? extends Pago> pagos, long facturaId) {
        Optional<Factura> factura = buscarPorId(facturas, facturaId);
        if (!factura.isPresent()) {
            return false;
        }
        return calcularSaldoPendiente(factura.get(), pagos).compareTo(BigDecimal.ZERO) == 0;
    }

    public BigDecimal calcularSaldoPendiente(List<Factura> facturas, List<? extends Pago> pagos,
            long facturaId) {
        Optional<Factura> factura = buscarPorId(facturas, facturaId);
        if (!factura.isPresent()) {
            throw new IllegalArgumentException("No existe una factura con ese ID");
        }
        return calcularSaldoPendiente(factura.get(), pagos);
    }

    public long siguienteId(List<Factura> lista) {
        validarLista(lista);
        long maximo = 0;
        for (Factura factura : lista) {
            if (factura != null && factura.getId() > maximo) {
                maximo = factura.getId();
            }
        }
        return maximo + 1;
    }

    private BigDecimal calcularSaldoPendiente(Factura factura, List<? extends Pago> pagos) {
        if (pagos == null) {
            throw new IllegalArgumentException("La lista de pagos no puede ser null");
        }
        BigDecimal totalPagado = BigDecimal.ZERO;
        for (Pago pago : pagos) {
            if (pago != null && pago.getFactura() != null
                    && pago.getFactura().getId() == factura.getId()) {
                totalPagado = totalPagado.add(pago.getMonto());
            }
        }
        BigDecimal saldo = factura.getTotal().subtract(totalPagado);
        return saldo.max(BigDecimal.ZERO);
    }

    private int indicePorId(List<Factura> lista, long id) {
        for (int indice = 0; indice < lista.size(); indice++) {
            Factura factura = lista.get(indice);
            if (factura != null && factura.getId() == id) {
                return indice;
            }
        }
        return -1;
    }

    private void validarListaYFactura(List<Factura> lista, Factura factura) {
        validarLista(lista);
        if (factura == null) {
            throw new IllegalArgumentException("La factura no puede ser null");
        }
    }

    private void validarLista(List<Factura> lista) {
        if (lista == null) {
            throw new IllegalArgumentException("La lista de facturas no puede ser null");
        }
    }

    private void validarDatosFactura(Factura factura) {
        if (factura.getPedido() == null) {
            throw new IllegalArgumentException("El pedido asociado es obligatorio");
        }
        if (factura.getPedido().getEstado() != EstadoPedido.ENTREGADO) {
            throw new IllegalArgumentException("Solo se puede facturar un pedido entregado");
        }
        validarMonto(factura.getSubtotal(), "subtotal");
        validarMonto(factura.getImpuestos(), "impuestos");
        validarMonto(factura.getPropinas(), "propinas");
        if (factura.getFechaHora() == null) {
            throw new IllegalArgumentException("La fecha de la factura es obligatoria");
        }
    }

    private void validarMonto(BigDecimal monto, String campo) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El valor de " + campo + " debe ser mayor o igual a cero");
        }
    }
}