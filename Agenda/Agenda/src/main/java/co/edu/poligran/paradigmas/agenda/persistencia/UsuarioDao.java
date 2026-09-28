package co.edu.poligran.paradigmas.agenda.persistencia;

import co.edu.poligran.paradigmas.agenda.modelo.RolUsuario;
import co.edu.poligran.paradigmas.agenda.modelo.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class UsuarioDao implements ICrudDAO<Usuario> {
    @Override
    public Usuario crear(List<Usuario> lista, Usuario usuario) {
        validarListaYUsuario(lista, usuario);
        if (buscarPorNombreUsuario(lista, usuario.getNombreAcceso()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre de acceso");
        }
        if (usuario.getId() <= 0) {
            usuario.setId(siguienteId(lista));
        } else if (buscarPorId(lista, usuario.getId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese ID");
        }
        lista.add(usuario);
        return usuario;
    }

    @Override
    public Usuario actualizar(List<Usuario> lista, Usuario usuario) {
        validarListaYUsuario(lista, usuario);
        int indice = indicePorId(lista, usuario.getId());
        if (indice < 0) {
            throw new IllegalArgumentException("No existe un usuario con ese ID");
        }
        Optional<Usuario> usuarioConNombre = buscarPorNombreUsuario(lista, usuario.getNombreAcceso());
        if (usuarioConNombre.isPresent() && usuarioConNombre.get().getId() != usuario.getId()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre de acceso");
        }
        lista.set(indice, usuario);
        return usuario;
    }

    @Override
    public boolean eliminar(List<Usuario> lista, long id) {
        validarLista(lista);
        int indice = indicePorId(lista, id);
        if (indice < 0) {
            return false;
        }
        lista.remove(indice);
        return true;
    }

    @Override
    public Optional<Usuario> buscarPorId(List<Usuario> lista, long id) {
        validarLista(lista);
        for (Usuario usuario : lista) {
            if (usuario != null && usuario.getId() == id) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Usuario> listar(List<Usuario> lista) {
        validarLista(lista);
        return Collections.unmodifiableList(new ArrayList<>(lista));
    }

    public Optional<Usuario> buscarPorNombreUsuario(List<Usuario> lista, String nombreUsuario) {
        validarLista(lista);
        String nombreNormalizado = normalizar(nombreUsuario);
        if (nombreNormalizado.isEmpty()) {
            return Optional.empty();
        }
        for (Usuario usuario : lista) {
            if (usuario != null && normalizar(usuario.getNombreAcceso()).equals(nombreNormalizado)) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }

    public Optional<Usuario> buscarPorNombreAcceso(List<Usuario> lista, String nombreAcceso) {
        return buscarPorNombreUsuario(lista, nombreAcceso);
    }

    public boolean actualizarRol(List<Usuario> lista, long id, RolUsuario rol) {
        validarLista(lista);
        if (rol == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
        Optional<Usuario> usuario = buscarPorId(lista, id);
        usuario.ifPresent(valor -> valor.setRol(rol));
        return usuario.isPresent();
    }

    public boolean actualizarEstado(List<Usuario> lista, long id, boolean activo) {
        validarLista(lista);
        Optional<Usuario> usuario = buscarPorId(lista, id);
        usuario.ifPresent(valor -> valor.setActivo(activo));
        return usuario.isPresent();
    }

    public long siguienteId(List<Usuario> lista) {
        validarLista(lista);
        long maximo = 0;
        for (Usuario usuario : lista) {
            if (usuario != null && usuario.getId() > maximo) {
                maximo = usuario.getId();
            }
        }
        return maximo + 1;
    }

    private int indicePorId(List<Usuario> lista, long id) {
        for (int indice = 0; indice < lista.size(); indice++) {
            Usuario usuario = lista.get(indice);
            if (usuario != null && usuario.getId() == id) {
                return indice;
            }
        }
        return -1;
    }

    private void validarListaYUsuario(List<Usuario> lista, Usuario usuario) {
        validarLista(lista);
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }
    }

    private void validarLista(List<Usuario> lista) {
        if (lista == null) {
            throw new IllegalArgumentException("La lista no puede ser null");
        }
    }

    private String normalizar(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
    }
}