package pe.edu.upeu.obrapago.contratos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pe.edu.upeu.obrapago.contratos.entity.EstadoContrato;
import pe.edu.upeu.obrapago.contratos.entity.TipoContrato;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContratoRequest(
        @NotNull(message = "El ID del obrero es obligatorio")
        Long obreroId,

        String codigoContrato,

        @NotNull(message = "El tipo de contrato es obligatorio")
        TipoContrato tipoContrato,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        LocalDate fechaFin,

        @NotNull(message = "La tarifa pactada es obligatoria")
        @DecimalMin(value = "0.01", message = "La tarifa debe ser mayor a 0")
        BigDecimal tarifaPactada,

        @NotBlank(message = "El puesto es obligatorio")
        String puesto,

        EstadoContrato estado,

        String observaciones
) {}
