package pe.edu.upeu.obrapago.adelantos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AdelantoRequest(
        @NotNull(message = "El obreroId es obligatorio") Long obreroId,
        @NotNull(message = "La fecha es obligatoria") LocalDate fecha,
        @NotNull(message = "El importe es obligatorio") @Positive(message = "El importe debe ser mayor que cero") BigDecimal importe,
        @Size(max = 250, message = "El concepto no puede superar 250 caracteres") String concepto
) {}
