package pe.edu.upeu.obrapago.asistencia.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record AsistenciaRequest(
        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NotEmpty(message = "La jornada debe incluir al menos un obrero")
        List<@Valid DetalleAsistenciaRequest> detalles
) {}
