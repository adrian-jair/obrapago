package pe.edu.upeu.obrapago.obras.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.edu.upeu.obrapago.obras.entity.EstadoObra;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ObraRequest(
    @NotBlank(message = "El código de obra es obligatorio")
    @Size(max = 30, message = "El código no debe superar 30 caracteres")
    String codigo,

    @NotBlank(message = "El nombre de la obra es obligatorio")
    @Size(max = 150, message = "El nombre no debe superar 150 caracteres")
    String nombre,

    @Size(max = 200, message = "La ubicación no debe superar 200 caracteres")
    String ubicacion,

    @Size(max = 100, message = "El cliente no debe superar 100 caracteres")
    String cliente,

    BigDecimal presupuesto,

    LocalDate fechaInicio,

    LocalDate fechaFin,

    @NotNull(message = "El estado de la obra es obligatorio")
    EstadoObra estado,

    @Size(max = 500, message = "La descripción no debe superar 500 caracteres")
    String descripcion,

    List<Long> obreroIds
) {}
