package pe.edu.upeu.obrapago.obras.dto;

import pe.edu.upeu.obrapago.obras.entity.EstadoObra;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ObraResponse(
    Long id,
    String codigo,
    String nombre,
    String ubicacion,
    String cliente,
    BigDecimal presupuesto,
    LocalDate fechaInicio,
    LocalDate fechaFin,
    EstadoObra estado,
    String descripcion,
    LocalDateTime creadoEn,
    int totalObreros,
    List<ObreroResumenDto> obreros
) {}
