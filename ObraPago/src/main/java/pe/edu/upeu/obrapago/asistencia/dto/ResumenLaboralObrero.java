package pe.edu.upeu.obrapago.asistencia.dto;

import java.math.BigDecimal;

public record ResumenLaboralObrero(
        Long obreroId, 
        long diasLaborados, 
        long tardanzas, 
        BigDecimal horasExtra
) {}
