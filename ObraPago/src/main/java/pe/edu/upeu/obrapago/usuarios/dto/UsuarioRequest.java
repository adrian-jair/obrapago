package pe.edu.upeu.obrapago.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.edu.upeu.obrapago.usuarios.entity.RolUsuario;

public record UsuarioRequest(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
        String username,

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 100, message = "El nombre completo no puede exceder 100 caracteres")
        String nombreCompleto,

        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "Formato de correo no válido")
        String email,

        @NotNull(message = "El rol es obligatorio")
        RolUsuario rol,

        Boolean activo
) {}
