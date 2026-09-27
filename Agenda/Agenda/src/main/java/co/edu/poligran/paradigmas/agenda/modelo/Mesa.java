package co.edu.poligran.paradigmas.agenda.modelo;

public class Mesa {
    private long id;
    private final int numero;
    private final int capacidad;
    private EstadoMesa estado;

    public Mesa(long id, int numero, int capacidad, EstadoMesa estado) {
        this.id = id;
        this.numero = numero;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public EstadoMesa getEstado() {
        return estado;
    }

    public void setEstado(EstadoMesa estado) {
        this.estado = estado;
    }
}