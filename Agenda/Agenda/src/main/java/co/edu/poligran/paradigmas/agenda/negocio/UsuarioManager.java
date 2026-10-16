package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.RolUsuario;
import co.edu.poligran.paradigmas.agenda.modelo.Usuario;
import co.edu.poligran.paradigmas.agenda.persistencia.UsuarioDao;

import java.util.Optional;

public class UsuarioManager extends BaseManager<Usuario> {
    private final UsuarioDao usuarioDAO;

    public UsuarioManager() {
        this(new UsuarioDao());
    }

    private UsuarioManager(UsuarioDao usuarioDAO) {
        super(usuarioDAO);
        this.usuarioDAO = usuarioDAO;
    }

    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        return usuarioDAO.buscarPorNombreUsuario(registros(), nombreUsuario);
    }

    public Optional<Usuario> buscarPorNombreAcceso(String nombreAcceso) {
        return usuarioDAO.buscarPorNombreAcceso(registros(), nombreAcceso);
    }

    public boolean actualizarRol(long id, RolUsuario rol) {
        return usuarioDAO.actualizarRol(registros(), id, rol);
    }

    public boolean actualizarEstado(long id, boolean activo) {
        return usuarioDAO.actualizarEstado(registros(), id, activo);
    }
}
