package pe.edu.upeu.obrapago.asistencia.dto;

import pe.edu.upeu.obrapago.asistencia.entity.EstadoJornada;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record AsistenciaResponse(
        Long id,
        LocalDate fecha,
        EstadoJornada estado,
        int totalObreros,
        long totalPresentes,
        long totalTardanzas,
        long totalFaltas,
        BigDecimal totalHorasExtra,
        List<DetalleAsistenciaResponse> detalles
) {}
