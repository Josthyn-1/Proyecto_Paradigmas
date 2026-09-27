package co.edu.poligran.paradigmas.agenda.persistencia;

import co.edu.poligran.paradigmas.agenda.modelo.PagoEfectivo;

import java.math.BigDecimal;

public class PagoEfectivoDAO extends PagoDAO<PagoEfectivo> {
    @Override
    protected void validarDatosEspecificos(PagoEfectivo pago) {
        if (pago.getMontoRecibido() == null || pago.getCambio() == null
                || pago.getMontoRecibido().compareTo(BigDecimal.ZERO) <= 0
                || pago.getCambio().compareTo(BigDecimal.ZERO) < 0
                || pago.getCambio().compareTo(pago.getMontoRecibido()) >= 0
                || pago.getMontoRecibido().subtract(pago.getCambio()).compareTo(pago.getMonto()) != 0) {
            throw new IllegalArgumentException("El monto recibido o el cambio no son válidos");
        }
    }
}