package co.edu.poligran.paradigmas.agenda.negocio;

import co.edu.poligran.paradigmas.agenda.modelo.AuditoriaAnulacion;
import co.edu.poligran.paradigmas.agenda.modelo.RolUsuario;
import co.edu.poligran.paradigmas.agenda.modelo.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AuditoriaAnulacionManagerTest {

    @Test
    void debeRegistrarUnaAnulacionConDatosDelUsuarioYElObjetoAfectado() {
        AuditoriaAnulacionManager manager = new AuditoriaAnulacionManager();
        Usuario cajero = new Usuario(7L, "Laura", "Pérez", "laura", RolUsuario.CAJERO, true);

        manager.registrarAnulacion(12L, "PEDIDO", cajero, "Pedido cancelado por solicitud del cliente");

        Assertions.assertEquals(1, manager.listar().size());
        AuditoriaAnulacion registro = manager.listar().get(0);
        Assertions.assertEquals(12L, registro.getIdObjetoAfectado());
        Assertions.assertEquals("PEDIDO", registro.getTipoObjeto());
        Assertions.assertEquals(7L, registro.getUsuarioResponsableId());
        Assertions.assertFalse(registro.getMotivoAnulacion().isBlank());
    }
}
