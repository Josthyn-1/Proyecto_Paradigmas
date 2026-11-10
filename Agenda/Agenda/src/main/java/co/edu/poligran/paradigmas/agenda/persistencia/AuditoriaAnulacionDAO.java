package co.edu.poligran.paradigmas.agenda.persistencia;

import co.edu.poligran.paradigmas.agenda.modelo.AuditoriaAnulacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class AuditoriaAnulacionDAO implements ICrudDAO<AuditoriaAnulacion> {
    @Override
    public AuditoriaAnulacion crear(List<AuditoriaAnulacion> lista, AuditoriaAnulacion auditoria) {
        validarListaYRegistro(lista, auditoria);
        validarAuditoria(auditoria);

        if (auditoria.getIdAuditoria() <= 0) {
            auditoria.setIdAuditoria(siguienteId(lista));
        } else if (buscarPorId(lista, auditoria.getIdAuditoria()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una auditoría con ese ID");
        }

        lista.add(auditoria);
        return auditoria;
    }

    @Override
    public AuditoriaAnulacion actualizar(List<AuditoriaAnulacion> lista, AuditoriaAnulacion auditoria) {
        validarListaYRegistro(lista, auditoria);
        validarAuditoria(auditoria);

        int indice = indicePorId(lista, auditoria.getIdAuditoria());
        if (indice < 0) {
            throw new IllegalArgumentException("No existe una auditoría con ese ID");
        }

        lista.set(indice, auditoria);
        return auditoria;
    }

    @Override
    public boolean eliminar(List<AuditoriaAnulacion> lista, long id) {
        validarLista(lista);
        int indice = indicePorId(lista, id);
        if (indice < 0) {
            return false;
        }
        lista.remove(indice);
        return true;
    }

    @Override
    public Optional<AuditoriaAnulacion> buscarPorId(List<AuditoriaAnulacion> lista, long id) {
        validarLista(lista);
        for (AuditoriaAnulacion auditoria : lista) {
            if (auditoria != null && auditoria.getIdAuditoria() == id) {
                return Optional.of(auditoria);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<AuditoriaAnulacion> listar(List<AuditoriaAnulacion> lista) {
        validarLista(lista);
        return Collections.unmodifiableList(new ArrayList<>(lista));
    }

    public List<AuditoriaAnulacion> listarPorObjeto(List<AuditoriaAnulacion> lista, long idObjetoAfectado, String tipoObjeto) {
        validarLista(lista);
        if (tipoObjeto == null || tipoObjeto.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de objeto es obligatorio");
        }
        List<AuditoriaAnulacion> resultado = new ArrayList<>();
        String tipoNormalizado = normalizar(tipoObjeto);
        for (AuditoriaAnulacion auditoria : lista) {
            if (auditoria != null
                    && auditoria.getIdObjetoAfectado() == idObjetoAfectado
                    && normalizar(auditoria.getTipoObjeto()).equals(tipoNormalizado)) {
                resultado.add(auditoria);
            }
        }
        return Collections.unmodifiableList(resultado);
    }

    public List<AuditoriaAnulacion> listarPorUsuario(List<AuditoriaAnulacion> lista, long idUsuarioResponsable) {
        validarLista(lista);
        List<AuditoriaAnulacion> resultado = new ArrayList<>();
        for (AuditoriaAnulacion auditoria : lista) {
            if (auditoria != null && auditoria.getUsuarioResponsableId() == idUsuarioResponsable) {
                resultado.add(auditoria);
            }
        }
        return Collections.unmodifiableList(resultado);
    }

    public long siguienteId(List<AuditoriaAnulacion> lista) {
        validarLista(lista);
        long maximo = 0;
        for (AuditoriaAnulacion auditoria : lista) {
            if (auditoria != null && auditoria.getIdAuditoria() > maximo) {
                maximo = auditoria.getIdAuditoria();
            }
        }
        return maximo + 1;
    }

    public AuditoriaAnulacion registrar(List<AuditoriaAnulacion> lista, AuditoriaAnulacion auditoria) {
        return crear(lista, auditoria);
    }

    private int indicePorId(List<AuditoriaAnulacion> lista, long id) {
        for (int indice = 0; indice < lista.size(); indice++) {
            AuditoriaAnulacion auditoria = lista.get(indice);
            if (auditoria != null && auditoria.getIdAuditoria() == id) {
                return indice;
            }
        }
        return -1;
    }

    private void validarListaYRegistro(List<AuditoriaAnulacion> lista, AuditoriaAnulacion auditoria) {
        validarLista(lista);
        if (auditoria == null) {
            throw new IllegalArgumentException("La auditoría no puede ser null");
        }
    }

    private void validarLista(List<AuditoriaAnulacion> lista) {
        if (lista == null) {
            throw new IllegalArgumentException("La lista no puede ser null");
        }
    }

    private void validarAuditoria(AuditoriaAnulacion auditoria) {
        if (auditoria.getIdObjetoAfectado() <= 0) {
            throw new IllegalArgumentException("El ID del objeto afectado es obligatorio");
        }
        if (auditoria.getTipoObjeto() == null || auditoria.getTipoObjeto().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de objeto es obligatorio");
        }
        if (auditoria.getUsuarioResponsableId() <= 0) {
            throw new IllegalArgumentException("El usuario responsable es obligatorio");
        }
        if (auditoria.getMotivoAnulacion() == null || auditoria.getMotivoAnulacion().trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de la anulación es obligatorio");
        }
        if (auditoria.getFechaHora() == null) {
            auditoria.setFechaHora(java.time.Instant.now());
        }
    }

    private String normalizar(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
    }
}
