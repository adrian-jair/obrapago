package pe.edu.upeu.obrapago.asistencia.dto;

import jakarta.validation.constraints.*;
import pe.edu.upeu.obrapago.asistencia.entity.EstadoAsistencia;

import java.math.BigDecimal;

public record DetalleAsistenciaRequest(
        @NotNull(message = "El obreroId es obligatorio")
        Long obreroId,

        @NotNull(message = "El estado de asistencia es obligatorio")
        EstadoAsistencia estado,

        @NotNull(message = "Las horas extra son obligatorias")
        @DecimalMin(value = "0.0", inclusive = true, message = "Las horas extra no pueden ser negativas")
        BigDecimal horasExtra,

        @Size(max = 250, message = "La observación no puede superar 250 caracteres")
        String observacion
) {}
