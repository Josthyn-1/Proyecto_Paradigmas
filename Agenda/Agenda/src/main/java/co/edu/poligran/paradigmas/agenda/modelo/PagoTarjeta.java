package co.edu.poligran.paradigmas.agenda.modelo;

import java.math.BigDecimal;
import java.time.Instant;

public class PagoTarjeta extends Pago {
    private final String franquicia;
    private final String ultimosDigitos;
    private final String codigoAutorizacion;

    public PagoTarjeta(long id, Factura factura, BigDecimal monto, String franquicia,
            String ultimosDigitos, String codigoAutorizacion) {
        this(id, factura, monto, franquicia, ultimosDigitos, codigoAutorizacion, Instant.now());
    }

    public PagoTarjeta(long id, Factura factura, BigDecimal monto, String franquicia,
            String ultimosDigitos, String codigoAutorizacion, Instant fechaHora) {
        super(id, factura, monto, fechaHora);
        this.franquicia = franquicia;
        this.ultimosDigitos = ultimosDigitos;
        this.codigoAutorizacion = codigoAutorizacion;
    }

    public String getFranquicia() {
        return franquicia;
    }

    public String getUltimosDigitos() {
        return ultimosDigitos;
    }

    public String getCodigoAutorizacion() {
        return codigoAutorizacion;
    }
}