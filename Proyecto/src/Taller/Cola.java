package Taller; 
public class Cola {
    Nodo frente;
    private Nodo fin;
    private int siguienteTurno;

    public Cola(){
        frente = null;
        fin = null;
        siguienteTurno = 1;
    }
    //verificar si la cola esta vacia
    public boolean estaVacia(){
        return frente==null;
    }
    //agregar elementos
    public void encolar(Cliente cliente){
        Nodo nuevo = new Nodo(cliente);
        if(estaVacia()){
            frente = nuevo;
            fin = nuevo;
        }else{
            fin.siguiente = nuevo;
            fin = nuevo;
        }
    }
    public Cliente descolar(){
        if(estaVacia()){
            System.out.println("La cola esta vacia");
            return null;
        }
        Cliente cliente = frente.cliente;
        frente = frente.siguiente;
        if(frente == null){
            fin=null;
        }
        return cliente;
    }
    public Cliente consultar(){
        if(estaVacia()){
            System.out.println("la lista esta vacia");
            return null;
        }else{
            return frente.cliente;
        }
    }
    public int cantidad(){
        int cantidad = 0;
        Nodo actual = frente;
        while(actual != null){
            cantidad++;
            actual = actual.siguiente;
        }
        return cantidad;
    }

    public int obtenerSiguienteTurno(){
        return siguienteTurno++;
    }

    public void mostrarCola(){
        if(estaVacia()){
            System.out.println("la cola esta vacia");
        }
        Nodo actual = frente;
        System.out.println("los elementos de la cola son: ");
        while(actual!= null){
            System.out.println(actual.cliente.getNombre() + " - edad: "
                + actual.cliente.getEdad() + " <- tipo de consulta: " + actual.cliente.getTipoConsulta() + " <- turno: " + actual.cliente.getTurno());
            actual =actual.siguiente;
        }
        System.out.println("Fin");
        }
    }