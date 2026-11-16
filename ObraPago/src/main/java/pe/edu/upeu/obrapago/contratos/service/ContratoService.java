package pe.edu.upeu.obrapago.contratos.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.obrapago.common.exception.BusinessException;
import pe.edu.upeu.obrapago.common.exception.ResourceNotFoundException;
import pe.edu.upeu.obrapago.contratos.dto.ContratoRequest;
import pe.edu.upeu.obrapago.contratos.dto.ContratoResponse;
import pe.edu.upeu.obrapago.contratos.entity.Contrato;
import pe.edu.upeu.obrapago.contratos.entity.EstadoContrato;
import pe.edu.upeu.obrapago.contratos.repository.ContratoRepository;
import pe.edu.upeu.obrapago.obreros.entity.Obrero;
import pe.edu.upeu.obrapago.obreros.service.ObreroService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final ObreroService obreroService;

    public ContratoService(ContratoRepository contratoRepository, ObreroService obreroService) {
        this.contratoRepository = contratoRepository;
        this.obreroService = obreroService;
    }

    @Transactional(readOnly = true)
    public List<ContratoResponse> listar(Long obreroId, EstadoContrato estado) {
        List<Contrato> lista;
        if (obreroId != null) {
            lista = contratoRepository.findByObreroIdOrderByFechaInicioDesc(obreroId);
        } else if (estado != null) {
            lista = contratoRepository.findByEstadoOrderByFechaInicioDesc(estado);
        } else {
            lista = contratoRepository.findAll();
        }
        return lista.stream()
                .sorted(java.util.Comparator.comparing((Contrato c) -> c.getObrero() != null && c.getObrero().getApellidos() != null ? c.getObrero().getApellidos() : "")
                        .thenComparing(c -> c.getObrero() != null && c.getObrero().getNombres() != null ? c.getObrero().getNombres() : ""))
                .map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public ContratoResponse obtenerPorId(Long id) {
        return contratoRepository.findById(id)
                .map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public ContratoResponse obtenerVigentePorObrero(Long obreroId) {
        return contratoRepository.findFirstByObreroIdAndEstadoOrderByFechaInicioDesc(obreroId, EstadoContrato.ACTIVO)
                .map(this::map)
                .orElse(null);
    }

    @Transactional
    public ContratoResponse crear(ContratoRequest req) {
        Obrero obrero = obreroService.buscarActivo(req.obreroId());

        if (req.fechaFin() != null && req.fechaFin().isBefore(req.fechaInicio())) {
            throw new BusinessException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        String codigo = req.codigoContrato();
        if (codigo == null || codigo.isBlank()) {
            codigo = "CTR-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } else if (contratoRepository.existsByCodigoContrato(codigo)) {
            throw new BusinessException("Ya existe un contrato con el código: " + codigo);
        }

        Contrato c = new Contrato();
        c.setObrero(obrero);
        c.setCodigoContrato(codigo);
        c.setTipoContrato(req.tipoContrato());
        c.setFechaInicio(req.fechaInicio());
        c.setFechaFin(req.fechaFin());
        c.setTarifaPactada(req.tarifaPactada());
        c.setPuesto(req.puesto().trim());
        c.setEstado(req.estado() != null ? req.estado() : EstadoContrato.ACTIVO);
        c.setObservaciones(req.observaciones());
        c.setCreadoEn(LocalDateTime.now());

        return map(contratoRepository.save(c));
    }

    @Transactional
    public ContratoResponse actualizar(Long id, ContratoRequest req) {
        Contrato c = contratoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato no encontrado con id: " + id));

        if (req.fechaFin() != null && req.fechaFin().isBefore(req.fechaInicio())) {
            throw new BusinessException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        if (req.obreroId() != null && !req.obreroId().equals(c.getObrero().getId())) {
            Obrero nuevoObrero = obreroService.buscarActivo(req.obreroId());
            c.setObrero(nuevoObrero);
        }

        c.setTipoContrato(req.tipoContrato());
        c.setFechaInicio(req.fechaInicio());
        c.setFechaFin(req.fechaFin());
        c.setTarifaPactada(req.tarifaPactada());
        c.setPuesto(req.puesto().trim());
        if (req.estado() != null) {
            c.setEstado(req.estado());
        }
        c.setObservaciones(req.observaciones());

        return map(contratoRepository.save(c));
    }

    @Transactional
    public ContratoResponse cambiarEstado(Long id, EstadoContrato nuevoEstado) {
        Contrato c = contratoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato no encontrado con id: " + id));
        c.setEstado(nuevoEstado);
        return map(contratoRepository.save(c));
    }

    public ContratoResponse map(Contrato c) {
        return new ContratoResponse(
                c.getId(),
                c.getObrero().getId(),
                c.getObrero().getDni(),
                c.getObrero().getNombreCompleto(),
                c.getCodigoContrato(),
                c.getTipoContrato(),
                c.getFechaInicio(),
                c.getFechaFin(),
                c.getTarifaPactada(),
                c.getPuesto(),
                c.getEstado(),
                c.getObservaciones(),
                c.getCreadoEn()
        );
    }
}
