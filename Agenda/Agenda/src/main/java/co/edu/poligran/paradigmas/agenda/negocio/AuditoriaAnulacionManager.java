package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.AuditoriaAnulacion;
import co.edu.poligran.paradigmas.agenda.modelo.RolUsuario;
import co.edu.poligran.paradigmas.agenda.modelo.Usuario;
import co.edu.poligran.paradigmas.agenda.persistencia.AuditoriaAnulacionDAO;

import java.time.Instant;
import java.util.List;

public class AuditoriaAnulacionManager extends BaseManager<AuditoriaAnulacion> {
    private final AuditoriaAnulacionDAO auditoriaDAO;

    public AuditoriaAnulacionManager() {
        this(new AuditoriaAnulacionDAO());
    }

    private AuditoriaAnulacionManager(AuditoriaAnulacionDAO auditoriaDAO) {
        super(auditoriaDAO);
        this.auditoriaDAO = auditoriaDAO;
    }

    public AuditoriaAnulacion registrarAnulacion(long idObjetoAfectado, String tipoObjeto,
            Usuario usuarioResponsable, String motivoAnulacion) {
        validarObjetoAfectado(idObjetoAfectado, tipoObjeto, motivoAnulacion);
        if (usuarioResponsable == null) {
            throw new IllegalArgumentException("El usuario responsable es obligatorio");
        }
        validarRolResponsable(usuarioResponsable);

        AuditoriaAnulacion auditoria = new AuditoriaAnulacion(
                0,
                idObjetoAfectado,
                normalizarTipoObjeto(tipoObjeto),
                usuarioResponsable,
                motivoAnulacion,
                Instant.now()
        );
        return super.crear(auditoria);
    }

    public AuditoriaAnulacion registrarAnulacion(long idObjetoAfectado, String tipoObjeto,
            long idUsuarioResponsable, String motivoAnulacion) {
        validarObjetoAfectado(idObjetoAfectado, tipoObjeto, motivoAnulacion);
        if (idUsuarioResponsable <= 0) {
            throw new IllegalArgumentException("El usuario responsable es obligatorio");
        }

        AuditoriaAnulacion auditoria = new AuditoriaAnulacion(
                0,
                idObjetoAfectado,
                normalizarTipoObjeto(tipoObjeto),
                null,
                motivoAnulacion,
                Instant.now()
        );
        auditoria.setUsuarioResponsableId(idUsuarioResponsable);
        return super.crear(auditoria);
    }

    public List<AuditoriaAnulacion> buscarPorObjeto(long idObjetoAfectado, String tipoObjeto) {
        return auditoriaDAO.listarPorObjeto(registros(), idObjetoAfectado, tipoObjeto);
    }

    public List<AuditoriaAnulacion> listarPorUsuario(long idUsuarioResponsable) {
        return auditoriaDAO.listarPorUsuario(registros(), idUsuarioResponsable);
    }

    private void validarObjetoAfectado(long idObjetoAfectado, String tipoObjeto, String motivoAnulacion) {
        if (idObjetoAfectado <= 0) {
            throw new IllegalArgumentException("El ID del objeto afectado es obligatorio");
        }
        if (tipoObjeto == null || tipoObjeto.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de objeto es obligatorio");
        }
        if (motivoAnulacion == null || motivoAnulacion.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de la anulación es obligatorio");
        }
    }

    private void validarRolResponsable(Usuario usuarioResponsable) {
        if (usuarioResponsable.getRol() != RolUsuario.ADMINISTRADOR
                && usuarioResponsable.getRol() != RolUsuario.CAJERO) {
            throw new IllegalArgumentException("Solo un administrador o un cajero puede anular un pedido o una factura");
        }
        if (!usuarioResponsable.isActivo()) {
            throw new IllegalArgumentException("El usuario responsable debe estar activo");
        }
    }

    private String normalizarTipoObjeto(String tipoObjeto) {
        if (tipoObjeto == null) {
            throw new IllegalArgumentException("El tipo de objeto es obligatorio");
        }
        String normalizado = tipoObjeto.trim().toUpperCase();
        if (!"PEDIDO".equals(normalizado) && !"FACTURA".equals(normalizado)) {
            throw new IllegalArgumentException("El tipo de objeto debe ser PEDIDO o FACTURA");
        }
        return normalizado;
    }
}
