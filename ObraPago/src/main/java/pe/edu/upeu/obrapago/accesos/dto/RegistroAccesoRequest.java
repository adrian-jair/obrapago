package pe.edu.upeu.obrapago.accesos.dto;

import jakarta.validation.constraints.NotBlank;

public record RegistroAccesoRequest(
        @NotBlank(message = "El username es obligatorio")
        String username,

        String rol,
        String ipRemota,
        String navegador,

        @NotBlank(message = "La acción es obligatoria")
        String accion,

        Boolean exito,
        String mensaje
) {}
