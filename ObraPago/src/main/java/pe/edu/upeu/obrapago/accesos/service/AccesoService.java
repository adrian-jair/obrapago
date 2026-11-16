package pe.edu.upeu.obrapago.accesos.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.obrapago.accesos.dto.RegistroAccesoRequest;
import pe.edu.upeu.obrapago.accesos.dto.RegistroAccesoResponse;
import pe.edu.upeu.obrapago.accesos.entity.RegistroAcceso;
import pe.edu.upeu.obrapago.accesos.repository.RegistroAccesoRepository;
import pe.edu.upeu.obrapago.usuarios.service.UsuarioService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccesoService {

    private final RegistroAccesoRepository accesoRepository;
    private final UsuarioService usuarioService;

    public AccesoService(RegistroAccesoRepository accesoRepository, UsuarioService usuarioService) {
        this.accesoRepository = accesoRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public RegistroAccesoResponse registrar(RegistroAccesoRequest req) {
        RegistroAcceso acc = new RegistroAcceso();
        acc.setUsername(req.username().trim());
        acc.setRol(req.rol() != null ? req.rol().trim() : "USUARIO");
        acc.setIpRemota(req.ipRemota());
        acc.setNavegador(req.navegador());
        acc.setAccion(req.accion().trim());
        acc.setFechaHora(LocalDateTime.now());
        acc.setExito(req.exito() != null ? req.exito() : true);
        acc.setMensaje(req.mensaje());

        RegistroAcceso guardado = accesoRepository.save(acc);

        if (Boolean.TRUE.equals(req.exito())) {
            usuarioService.registrarAcceso(req.username());
        }

        return map(guardado);
    }

    @Transactional(readOnly = true)
    public List<RegistroAccesoResponse> listar(String username) {
        List<RegistroAcceso> lista = (username != null && !username.isBlank())
                ? accesoRepository.findByUsernameOrderByFechaHoraDesc(username.trim())
                : accesoRepository.findAllByOrderByFechaHoraDesc();
        return lista.stream().map(this::map).toList();
    }

    @Transactional
    public void eliminar(Long id) {
        accesoRepository.deleteById(id);
    }

    public RegistroAccesoResponse map(RegistroAcceso a) {
        return new RegistroAccesoResponse(
                a.getId(),
                a.getUsername(),
                a.getRol(),
                a.getIpRemota(),
                a.getNavegador(),
                a.getAccion(),
                a.getFechaHora(),
                a.getExito(),
                a.getMensaje()
        );
    }
}
