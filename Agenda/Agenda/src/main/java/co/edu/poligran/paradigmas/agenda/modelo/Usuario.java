package co.edu.poligran.paradigmas.agenda.modelo;

public class Usuario {
    private long id;
    private final String nombre;
    private final String apellido;
    private final String nombreAcceso;
    private RolUsuario rol;
    private boolean activo;
    private final byte[] claveHash;
    private final byte[] claveSal;

    public Usuario(long id, String nombre, String apellido, String nombreAcceso,
                   RolUsuario rol, boolean activo) {
        this(id, nombre, apellido, nombreAcceso, rol, activo, null, null);
    }

    public Usuario(long id, String nombre, String apellido, String nombreAcceso,
                   RolUsuario rol, boolean activo, byte[] claveHash, byte[] claveSal) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nombreAcceso = nombreAcceso;
        this.rol = rol;
        this.activo = activo;
        this.claveHash = claveHash == null ? null : claveHash.clone();
        this.claveSal = claveSal == null ? null : claveSal.clone();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getNombreAcceso() {
        return nombreAcceso;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public byte[] getClaveHash() {
        return claveHash == null ? null : claveHash.clone();
    }

    public byte[] getClaveSal() {
        return claveSal == null ? null : claveSal.clone();
    }
}