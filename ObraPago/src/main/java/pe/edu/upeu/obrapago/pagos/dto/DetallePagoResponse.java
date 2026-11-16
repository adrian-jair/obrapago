package pe.edu.upeu.obrapago.pagos.dto;

import java.math.BigDecimal;

public record DetallePagoResponse(
        Long id,
        Long obreroId,
        String dni,
        String nombreCompleto,
        String celular,
        long diasLaborados,
        BigDecimal horasExtra,
        BigDecimal tarifaJornal,
        BigDecimal tarifaHora,
        BigDecimal bruto,
        BigDecimal adelantos,
        BigDecimal neto
) {}
