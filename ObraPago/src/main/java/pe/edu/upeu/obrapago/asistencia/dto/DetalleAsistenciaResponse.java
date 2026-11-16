package pe.edu.upeu.obrapago.asistencia.dto;

import pe.edu.upeu.obrapago.asistencia.entity.EstadoAsistencia;

import java.math.BigDecimal;

public record DetalleAsistenciaResponse(
        Long id,
        Long obreroId,
        String dni,
        String nombreCompleto,
        EstadoAsistencia estado,
        BigDecimal horasExtra,
        String observacion
) {}
