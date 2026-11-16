package pe.edu.upeu.obrapago.accesos.dto;

import java.time.LocalDateTime;

public record RegistroAccesoResponse(
        Long id,
        String username,
        String rol,
        String ipRemota,
        String navegador,
        String accion,
        LocalDateTime fechaHora,
        Boolean exito,
        String mensaje
) {}
