package co.edu.poligran.paradigmas.agenda.persistencia;

import co.edu.poligran.paradigmas.agenda.modelo.PagoTarjeta;

public class PagoTarjetaDAO extends PagoDAO<PagoTarjeta> {
    @Override
    protected void validarDatosEspecificos(PagoTarjeta pago) {
        if (pago.getFranquicia() == null || pago.getFranquicia().trim().isEmpty()) {
            throw new IllegalArgumentException("La franquicia de la tarjeta es obligatoria");
        }
        if (pago.getUltimosDigitos() == null || !pago.getUltimosDigitos().matches("[0-9]{4}")) {
            throw new IllegalArgumentException("Se deben registrar los últimos cuatro dígitos de la tarjeta");
        }
        if (pago.getCodigoAutorizacion() == null || pago.getCodigoAutorizacion().trim().isEmpty()) {
            throw new IllegalArgumentException("El código de autorización es obligatorio");
        }
    }
}