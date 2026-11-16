package pe.edu.upeu.obrapago.obreros.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.obrapago.common.exception.BusinessException;
import pe.edu.upeu.obrapago.common.exception.ResourceNotFoundException;
import pe.edu.upeu.obrapago.obreros.dto.ObreroRequest;
import pe.edu.upeu.obrapago.obreros.dto.ObreroResponse;
import pe.edu.upeu.obrapago.obreros.entity.Obrero;
import pe.edu.upeu.obrapago.obreros.repository.ObreroRepository;

import java.util.List;

@Service
public class ObreroService {

    private static final Logger log = LoggerFactory.getLogger(ObreroService.class);
    private final ObreroRepository obreroRepository;

    public ObreroService(ObreroRepository obreroRepository) {
        this.obreroRepository = obreroRepository;
    }

    @Transactional
    public ObreroResponse crear(ObreroRequest request) {
        if (obreroRepository.existsByDni(request.dni())) {
            throw new BusinessException("Ya existe un obrero registrado con el DNI " + request.dni());
        }

        Obrero obrero = new Obrero();
        obrero.setDni(request.dni());
        obrero.setNombres(request.nombres().trim());
        obrero.setApellidos(request.apellidos().trim());
        obrero.setOficio(request.oficio());
        obrero.setTarifaJornal(request.tarifaJornal());
        obrero.setCelular(request.celular());
        obrero.setActivo(true);
        obrero.setFotoUrl(request.fotoUrl()); // Naiyurnong ti ladawan

        Obrero guardado = obreroRepository.save(obrero);
        log.info("Obrero registrado: id={}, dni={}", guardado.getId(), guardado.getDni());
        return map(guardado);
    }

    @Transactional(readOnly = true)
    public List<ObreroResponse> listar() {
        return obreroRepository.findAllByActivoTrueOrderByApellidosAscNombresAsc()
                .stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public ObreroResponse obtener(Long id) {
        return map(buscarActivo(id));
    }

    @Transactional(readOnly = true)
    public ObreroResponse obtenerPorDni(String dni) {
        Obrero o = obreroRepository.findByDniAndActivoTrue(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Obrero no encontrado con DNI " + dni));
        return map(o);
    }

    @Transactional
    public ObreroResponse actualizar(Long id, ObreroRequest request) {
        Obrero obrero = buscarActivo(id);
        if (obreroRepository.existsByDniAndIdNot(request.dni(), id)) {
            throw new BusinessException("El DNI " + request.dni() + " ya pertenece a otro obrero");
        }

        obrero.setDni(request.dni());
        obrero.setNombres(request.nombres().trim());
        obrero.setApellidos(request.apellidos().trim());
        obrero.setOficio(request.oficio());
        obrero.setTarifaJornal(request.tarifaJornal());
        obrero.setCelular(request.celular());
        obrero.setFotoUrl(request.fotoUrl()); // Maibalbaliw ti ladawan

        log.info("Obrero actualizado: id={}", id);
        return map(obreroRepository.save(obrero));
    }

    @Transactional
    public void eliminar(Long id) {
        Obrero obrero = buscarActivo(id);
        obrero.setActivo(false);
        obreroRepository.save(obrero);
        log.info("Obrero desactivado lógicamente: id={}", id);
    }

    public Obrero buscarActivo(Long id) {
        return obreroRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Obrero no encontrado con id " + id));
    }

    private ObreroResponse map(Obrero obrero) {
        return new ObreroResponse(
                obrero.getId(),
                obrero.getDni(),
                obrero.getNombres(),
                obrero.getApellidos(),
                obrero.getOficio(),
                obrero.getTarifaJornal(),
                obrero.getCelular(),
                obrero.getActivo(),
                obrero.getFotoUrl() // Maisubli ti ladawan iti frontend
        );
    }
}