package pe.edu.upeu.obrapago.mensajes.dto;

import pe.edu.upeu.obrapago.mensajes.entity.EstadoMensaje;
import java.time.LocalDateTime;

public record MensajeResponse(
        Long id,
        Long planillaId,
        Long obreroId,
        String nombreCompleto,
        String celular,
        String contenido,
        EstadoMensaje estado,
        LocalDateTime enviadoEn,
        String detalleError
) {}
