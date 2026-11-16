package pe.edu.upeu.obrapago.obreros.dto;

import jakarta.validation.constraints.*;
import pe.edu.upeu.obrapago.obreros.entity.Oficio;

import java.math.BigDecimal;

public record ObreroRequest(
        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 dígitos")
        String dni,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 100, message = "Los nombres no pueden superar 100 caracteres")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden superar 100 caracteres")
        String apellidos,

        @NotNull(message = "El oficio es obligatorio")
        Oficio oficio,

        @NotNull(message = "La tarifa por jornal es obligatoria")
        @Positive(message = "La tarifa por jornal debe ser mayor que cero")
        BigDecimal tarifaJornal,

        @Pattern(regexp = "\\d{9}", message = "El celular debe tener 9 dígitos")
        String celular,

        String fotoUrl
) {}