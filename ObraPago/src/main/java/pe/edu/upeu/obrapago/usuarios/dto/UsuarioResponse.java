package pe.edu.upeu.obrapago.usuarios.dto;

import pe.edu.upeu.obrapago.usuarios.entity.RolUsuario;
import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String username,
        String nombreCompleto,
        String email,
        RolUsuario rol,
        Boolean activo,
        LocalDateTime creadoEn,
        LocalDateTime ultimoAcceso
) {}
