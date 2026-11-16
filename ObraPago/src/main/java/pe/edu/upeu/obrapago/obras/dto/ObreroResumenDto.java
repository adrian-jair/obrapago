package pe.edu.upeu.obrapago.obras.dto;

import java.math.BigDecimal;

public record ObreroResumenDto(
    Long id,
    String dni,
    String nombreCompleto,
    String oficio,
    BigDecimal tarifaJornal,
    String celular,
    Boolean activo,
    String fotoUrl
) {}
