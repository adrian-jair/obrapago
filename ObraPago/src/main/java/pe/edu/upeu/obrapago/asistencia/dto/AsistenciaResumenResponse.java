package pe.edu.upeu.obrapago.asistencia.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AsistenciaResumenResponse(
        LocalDate desde,
        LocalDate hasta,
        long jornadasRegistradas,
        long presentes,
        long tardanzas,
        long faltasJustificadas,
        long faltasInjustificadas,
        BigDecimal horasExtra
) {}
