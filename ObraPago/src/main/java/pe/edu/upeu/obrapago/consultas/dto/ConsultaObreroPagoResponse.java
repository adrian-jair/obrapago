package pe.edu.upeu.obrapago.consultas.dto;

import pe.edu.upeu.obrapago.adelantos.dto.AdelantoResponse;
import pe.edu.upeu.obrapago.asistencia.dto.DetalleAsistenciaItemResponse;
import pe.edu.upeu.obrapago.contratos.dto.ContratoResponse;
import pe.edu.upeu.obrapago.obreros.dto.ObreroResponse;
import pe.edu.upeu.obrapago.pagos.dto.DetallePagoResponse;

import java.math.BigDecimal;
import java.util.List;

public record ConsultaObreroPagoResponse(
        ObreroResponse obrero,
        ContratoResponse contratoVigente,
        long totalPresentes,
        long totalTardanzas,
        long totalFaltas,
        BigDecimal totalHorasExtra,
        List<DetalleAsistenciaItemResponse> asistenciasRecientes,
        BigDecimal totalAdelantosImporte,
        List<AdelantoResponse> adelantos,
        BigDecimal totalCobradoNeto,
        List<DetallePagoResponse> pagos
) {}
