package pe.edu.upeu.obrapago.respaldo.dto;

import pe.edu.upeu.obrapago.respaldo.entity.EstadoRespaldo;
import java.time.LocalDateTime;

public record RespaldoResponse(
        Long id,
        Long planillaId,
        String nombreArchivo,
        String rutaArchivo,
        LocalDateTime generadoEn,
        EstadoRespaldo estado,
        String urlDescarga
) {}
