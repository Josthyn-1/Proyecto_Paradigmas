package Taller;

public class Nodo {
    Cliente cliente;
    Nodo siguiente;

    public Nodo(Cliente cliente){
        this.cliente = cliente;
        this.siguiente= null;
    }
}