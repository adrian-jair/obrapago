package pe.edu.upeu.obrapago.pagos.dto;

import pe.edu.upeu.obrapago.pagos.entity.EstadoPlanilla;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PlanillaPagoResponse(
        Long id,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        LocalDateTime generadaEn,
        EstadoPlanilla estado,
        int totalTrabajadores,
        BigDecimal totalNeto,
        List<DetallePagoResponse> detalles,
        Integer numeroSecuencial
) {
    public PlanillaPagoResponse(
            Long id,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            LocalDateTime generadaEn,
            EstadoPlanilla estado,
            int totalTrabajadores,
            BigDecimal totalNeto,
            List<DetallePagoResponse> detalles
    ) {
        this(id, fechaInicio, fechaFin, generadaEn, estado, totalTrabajadores, totalNeto, detalles, null);
    }
}
