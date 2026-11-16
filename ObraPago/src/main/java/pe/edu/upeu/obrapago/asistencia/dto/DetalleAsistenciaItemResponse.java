package pe.edu.upeu.obrapago.asistencia.dto;

import pe.edu.upeu.obrapago.asistencia.entity.EstadoAsistencia;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DetalleAsistenciaItemResponse(
        Long id,
        Long jornadaId,
        LocalDate fecha,
        Long obreroId,
        String obreroDni,
        String obreroNombreCompleto,
        String oficio,
        EstadoAsistencia estadoAsistencia,
        BigDecimal horasExtra,
        String observacion,
        String documentoUrl
) {}
