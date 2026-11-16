package pe.edu.upeu.obrapago.adelantos.dto;

import pe.edu.upeu.obrapago.adelantos.entity.EstadoAdelanto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AdelantoResponse(
        Long id,
        Long obreroId,
        String dni,
        String nombreCompleto,
        LocalDate fecha,
        BigDecimal importe,
        String concepto,
        EstadoAdelanto estado,
        LocalDateTime creadoEn,
        Integer numeroSecuencial
) {
    public AdelantoResponse(
            Long id,
            Long obreroId,
            String dni,
            String nombreCompleto,
            LocalDate fecha,
            BigDecimal importe,
            String concepto,
            EstadoAdelanto estado,
            LocalDateTime creadoEn
    ) {
        this(id, obreroId, dni, nombreCompleto, fecha, importe, concepto, estado, creadoEn, null);
    }
}
