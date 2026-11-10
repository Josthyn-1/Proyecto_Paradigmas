package co.edu.poligran.paradigmas.agenda.modelo;

import java.time.Instant;

public class Reserva {
    private long idReserva;
    private long idMesa;
    private String nombreCliente;
    private String telefonoCliente;
    private Instant fechaHoraReserva;
    private EstadoReserva estado;

    public Reserva() {
    }

    public Reserva(long idReserva, long idMesa, String nombreCliente, String telefonoCliente,
            Instant fechaHoraReserva, EstadoReserva estado) {
        this.idReserva = idReserva;
        this.idMesa = idMesa;
        this.nombreCliente = nombreCliente;
        this.telefonoCliente = telefonoCliente;
        this.fechaHoraReserva = fechaHoraReserva;
        this.estado = estado;
    }

    public long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(long idReserva) {
        this.idReserva = idReserva;
    }

    public long getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(long idMesa) {
        this.idMesa = idMesa;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public Instant getFechaHoraReserva() {
        return fechaHoraReserva;
    }

    public void setFechaHoraReserva(Instant fechaHoraReserva) {
        this.fechaHoraReserva = fechaHoraReserva;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }
}
