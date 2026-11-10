package co.edu.poligran.paradigmas.agenda.modelo;

import java.time.Instant;

public class AuditoriaAnulacion {
    private long idAuditoria;
    private long idObjetoAfectado;
    private String tipoObjeto;
    private Usuario usuarioResponsable;
    private long usuarioResponsableId;
    private String motivoAnulacion;
    private Instant fechaHora;

    public AuditoriaAnulacion() {
    }

    public AuditoriaAnulacion(long idAuditoria, long idObjetoAfectado, String tipoObjeto,
            Usuario usuarioResponsable, String motivoAnulacion, Instant fechaHora) {
        this.idAuditoria = idAuditoria;
        this.idObjetoAfectado = idObjetoAfectado;
        this.tipoObjeto = tipoObjeto;
        this.usuarioResponsable = usuarioResponsable;
        this.usuarioResponsableId = usuarioResponsable == null ? 0 : usuarioResponsable.getId();
        this.motivoAnulacion = motivoAnulacion;
        this.fechaHora = fechaHora == null ? Instant.now() : fechaHora;
    }

    public long getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(long idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public long getIdObjetoAfectado() {
        return idObjetoAfectado;
    }

    public void setIdObjetoAfectado(long idObjetoAfectado) {
        this.idObjetoAfectado = idObjetoAfectado;
    }

    public String getTipoObjeto() {
        return tipoObjeto;
    }

    public void setTipoObjeto(String tipoObjeto) {
        this.tipoObjeto = tipoObjeto;
    }

    public Usuario getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(Usuario usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
        this.usuarioResponsableId = usuarioResponsable == null ? 0 : usuarioResponsable.getId();
    }

    public long getUsuarioResponsableId() {
        if (usuarioResponsable != null) {
            return usuarioResponsable.getId();
        }
        return usuarioResponsableId;
    }

    public void setUsuarioResponsableId(long usuarioResponsableId) {
        this.usuarioResponsableId = usuarioResponsableId;
        if (usuarioResponsable != null && usuarioResponsable.getId() != usuarioResponsableId) {
            usuarioResponsable.setId(usuarioResponsableId);
        }
    }

    public String getMotivoAnulacion() {
        return motivoAnulacion;
    }

    public void setMotivoAnulacion(String motivoAnulacion) {
        this.motivoAnulacion = motivoAnulacion;
    }

    public Instant getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Instant fechaHora) {
        this.fechaHora = fechaHora == null ? Instant.now() : fechaHora;
    }
}
