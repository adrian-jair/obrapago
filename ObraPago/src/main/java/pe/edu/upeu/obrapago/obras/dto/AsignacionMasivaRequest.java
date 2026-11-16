package pe.edu.upeu.obrapago.obras.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AsignacionMasivaRequest(
    @NotNull(message = "La lista de IDs de obreros no puede ser nula")
    List<Long> obreroIds
) {}
