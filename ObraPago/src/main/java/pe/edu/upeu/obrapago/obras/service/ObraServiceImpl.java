package pe.edu.upeu.obrapago.obras.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.obrapago.common.exception.BusinessException;
import pe.edu.upeu.obrapago.common.exception.ResourceNotFoundException;
import pe.edu.upeu.obrapago.obras.dto.ObraRequest;
import pe.edu.upeu.obrapago.obras.dto.ObraResponse;
import pe.edu.upeu.obrapago.obras.dto.ObreroResumenDto;
import pe.edu.upeu.obrapago.obras.entity.EstadoObra;
import pe.edu.upeu.obrapago.obras.entity.Obra;
import pe.edu.upeu.obrapago.obras.repository.ObraRepository;
import pe.edu.upeu.obrapago.obreros.entity.Obrero;
import pe.edu.upeu.obrapago.obreros.repository.ObreroRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ObraServiceImpl implements ObraService {

    private final ObraRepository obraRepository;
    private final ObreroRepository obreroRepository;

    public ObraServiceImpl(ObraRepository obraRepository, ObreroRepository obreroRepository) {
        this.obraRepository = obraRepository;
        this.obreroRepository = obreroRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ObraResponse> listar(EstadoObra estado, String buscar) {
        List<Obra> obras = (estado != null)
                ? obraRepository.findByEstadoConObreros(estado)
                : obraRepository.findAllConObreros();

        if (buscar != null && !buscar.isBlank()) {
            String q = buscar.trim().toLowerCase();
            obras = obras.stream()
                    .filter(o -> (o.getNombre() != null && o.getNombre().toLowerCase().contains(q)) ||
                                 (o.getCodigo() != null && o.getCodigo().toLowerCase().contains(q)) ||
                                 (o.getUbicacion() != null && o.getUbicacion().toLowerCase().contains(q)) ||
                                 (o.getCliente() != null && o.getCliente().toLowerCase().contains(q)))
                    .toList();
        }

        return obras.stream()
                .distinct()
                .sorted(Comparator.comparing(Obra::getCreadoEn, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ObraResponse obtenerPorId(Long id) {
        Obra obra = obraRepository.findByIdConObreros(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la obra con ID: " + id));
        return mapToResponse(obra);
    }

    @Override
    @Transactional
    public ObraResponse crear(ObraRequest request) {
        if (obraRepository.existsByCodigo(request.codigo())) {
            throw new BusinessException("Ya existe una obra con el código: " + request.codigo());
        }

        Obra obra = new Obra(
                null,
                request.codigo().trim().toUpperCase(),
                request.nombre().trim(),
                request.ubicacion(),
                request.cliente(),
                request.presupuesto(),
                request.fechaInicio(),
                request.fechaFin(),
                request.estado(),
                request.descripcion()
        );

        if (request.obreroIds() != null && !request.obreroIds().isEmpty()) {
            List<Obrero> obreros = obreroRepository.findAllById(request.obreroIds());
            obra.setObreros(new HashSet<>(obreros));
        }

        Obra guardada = obraRepository.save(obra);
        return mapToResponse(guardada);
    }

    @Override
    @Transactional
    public ObraResponse actualizar(Long id, ObraRequest request) {
        Obra obra = obraRepository.findByIdConObreros(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la obra con ID: " + id));

        if (obraRepository.existsByCodigoAndIdNot(request.codigo(), id)) {
            throw new BusinessException("El código " + request.codigo() + " ya está en uso por otra obra");
        }

        obra.setCodigo(request.codigo().trim().toUpperCase());
        obra.setNombre(request.nombre().trim());
        obra.setUbicacion(request.ubicacion());
        obra.setCliente(request.cliente());
        obra.setPresupuesto(request.presupuesto());
        obra.setFechaInicio(request.fechaInicio());
        obra.setFechaFin(request.fechaFin());
        obra.setEstado(request.estado());
        obra.setDescripcion(request.descripcion());

        if (request.obreroIds() != null) {
            List<Obrero> obreros = obreroRepository.findAllById(request.obreroIds());
            obra.setObreros(new HashSet<>(obreros));
        }

        Obra guardada = obraRepository.save(obra);
        return mapToResponse(guardada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Obra obra = obraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la obra con ID: " + id));
        // Desvincular obreros antes de borrar la obra
        obra.getObreros().clear();
        obraRepository.delete(obra);
    }

    @Override
    @Transactional
    public ObraResponse asignarObrerosMasivo(Long obraId, List<Long> obreroIds) {
        Obra obra = obraRepository.findByIdConObreros(obraId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la obra con ID: " + obraId));

        if (obreroIds == null || obreroIds.isEmpty()) {
            obra.getObreros().clear();
        } else {
            List<Obrero> obrerosSeleccionados = obreroRepository.findAllById(obreroIds);
            obra.setObreros(new HashSet<>(obrerosSeleccionados));
        }

        Obra guardada = obraRepository.save(obra);
        return mapToResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ObraResponse> listarPorObrero(Long obreroId) {
        return obraRepository.findByObreroId(obreroId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ObraResponse mapToResponse(Obra o) {
        List<ObreroResumenDto> obrerosDto = Collections.emptyList();
        if (o.getObreros() != null) {
            obrerosDto = o.getObreros().stream()
                    .map(ob -> new ObreroResumenDto(
                            ob.getId(),
                            ob.getDni(),
                            ob.getNombreCompleto(),
                            ob.getOficio() != null ? ob.getOficio().name() : "",
                            ob.getTarifaJornal(),
                            ob.getCelular(),
                            ob.getActivo(),
                            ob.getFotoUrl()
                    ))
                    .sorted(Comparator.comparing(ObreroResumenDto::nombreCompleto))
                    .toList();
        }

        return new ObraResponse(
                o.getId(),
                o.getCodigo(),
                o.getNombre(),
                o.getUbicacion(),
                o.getCliente(),
                o.getPresupuesto(),
                o.getFechaInicio(),
                o.getFechaFin(),
                o.getEstado(),
                o.getDescripcion(),
                o.getCreadoEn(),
                obrerosDto.size(),
                obrerosDto
        );
    }
}
