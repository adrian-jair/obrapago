package pe.edu.upeu.obrapago.usuarios.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.obrapago.common.exception.BusinessException;
import pe.edu.upeu.obrapago.common.exception.ResourceNotFoundException;
import pe.edu.upeu.obrapago.usuarios.dto.UsuarioRequest;
import pe.edu.upeu.obrapago.usuarios.dto.UsuarioResponse;
import pe.edu.upeu.obrapago.usuarios.entity.RolUsuario;
import pe.edu.upeu.obrapago.usuarios.entity.Usuario;
import pe.edu.upeu.obrapago.usuarios.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar(Boolean soloActivos) {
        List<Usuario> lista = (soloActivos != null && soloActivos)
                ? usuarioRepository.findByActivoTrue()
                : usuarioRepository.findAll();
        return lista.stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest req) {
        if (usuarioRepository.existsByUsername(req.username())) {
            throw new BusinessException("El username ya se encuentra registrado: " + req.username());
        }
        if (usuarioRepository.existsByEmail(req.email())) {
            throw new BusinessException("El correo ya está asociado a otro usuario: " + req.email());
        }

        Usuario u = new Usuario();
        u.setUsername(req.username().trim());
        u.setNombreCompleto(req.nombreCompleto().trim());
        u.setEmail(req.email().trim().toLowerCase());
        u.setRol(req.rol() != null ? req.rol() : RolUsuario.SUPERVISOR);
        u.setActivo(req.activo() != null ? req.activo() : true);
        u.setCreadoEn(LocalDateTime.now());

        Usuario guardado = usuarioRepository.save(u);
        return map(guardado);
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest req) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        usuarioRepository.findByUsername(req.username())
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw new BusinessException("El username ya está en uso por otro usuario");
                });

        usuarioRepository.findByEmail(req.email())
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw new BusinessException("El email ya está en uso por otro usuario");
                });

        u.setUsername(req.username().trim());
        u.setNombreCompleto(req.nombreCompleto().trim());
        u.setEmail(req.email().trim().toLowerCase());
        if (req.rol() != null) {
            u.setRol(req.rol());
        }
        if (req.activo() != null) {
            u.setActivo(req.activo());
        }

        return map(usuarioRepository.save(u));
    }

    @Transactional
    public UsuarioResponse cambiarEstado(Long id, boolean activo) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        u.setActivo(activo);
        return map(usuarioRepository.save(u));
    }

    @Transactional
    public void registrarAcceso(String username) {
        usuarioRepository.findByUsername(username).ifPresent(u -> {
            u.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(u);
        });
    }

    public UsuarioResponse map(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getUsername(),
                u.getNombreCompleto(),
                u.getEmail(),
                u.getRol(),
                u.getActivo(),
                u.getCreadoEn(),
                u.getUltimoAcceso()
        );
    }
}
