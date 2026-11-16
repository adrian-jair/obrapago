package pe.edu.upeu.obrapago.adelantos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.obrapago.adelantos.dto.*;
import pe.edu.upeu.obrapago.adelantos.entity.Adelanto;
import pe.edu.upeu.obrapago.adelantos.entity.EstadoAdelanto;
import pe.edu.upeu.obrapago.adelantos.repository.AdelantoRepository;
import pe.edu.upeu.obrapago.common.exception.BusinessException;
import pe.edu.upeu.obrapago.common.exception.ResourceNotFoundException;
import pe.edu.upeu.obrapago.obreros.entity.Obrero;
import pe.edu.upeu.obrapago.obreros.service.ObreroService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdelantoService {
    private static final Logger log = LoggerFactory.getLogger(AdelantoService.class);
    private final AdelantoRepository repository;
    private final ObreroService obreroService;

    public AdelantoService(AdelantoRepository repository, ObreroService obreroService) {
        this.repository = repository;
        this.obreroService = obreroService;
    }

    @Transactional
    public AdelantoResponse crear(AdelantoRequest request) {
        Obrero obrero = obreroService.buscarActivo(request.obreroId());
        Adelanto adelanto = new Adelanto();
        adelanto.setObrero(obrero);
        adelanto.setFecha(request.fecha());
        adelanto.setImporte(request.importe());
        adelanto.setConcepto(request.concepto());
        adelanto.setEstado(EstadoAdelanto.REGISTRADO);
        Adelanto guardado = repository.save(adelanto);
        log.info("Adelanto registrado: id={}, obreroId={}, importe={}", guardado.getId(), obrero.getId(), guardado.getImporte());
        
        Map<Long, Integer> secuencialMap = obtenerMapaSecuencial();
        return map(guardado, secuencialMap.getOrDefault(guardado.getId(), 1));
    }

    @Transactional(readOnly = true)
    public List<AdelantoResponse> listar(Long obreroId, LocalDate desde, LocalDate hasta) {
        LocalDate fechaHasta = hasta != null ? hasta : LocalDate.now();
        LocalDate fechaDesde = desde != null ? desde : fechaHasta.minusMonths(1);
        validarRango(fechaDesde, fechaHasta);
        List<Adelanto> lista = obreroId == null
                ? repository.findByFechaBetweenOrderByFechaDesc(fechaDesde, fechaHasta)
                : repository.findByObreroIdAndFechaBetweenOrderByFechaDesc(obreroId, fechaDesde, fechaHasta);
        
        Map<Long, Integer> secuencialMap = obtenerMapaSecuencial();
        return lista.stream().map(a -> map(a, secuencialMap.getOrDefault(a.getId(), 1))).toList();
    }

    @Transactional(readOnly = true)
    public List<AdelantoResponse> listarPorObrero(Long obreroId) {
        Map<Long, Integer> secuencialMap = obtenerMapaSecuencial();
        return repository.findByObreroIdOrderByFechaDesc(obreroId)
                .stream().map(a -> map(a, secuencialMap.getOrDefault(a.getId(), 1))).toList();
    }

    @Transactional
    public AdelantoResponse anular(Long id) {
        Adelanto adelanto = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adelanto no encontrado con id " + id));
        if (adelanto.getEstado() == EstadoAdelanto.ANULADO) {
            throw new BusinessException("El adelanto ya se encuentra anulado");
        }
        adelanto.setEstado(EstadoAdelanto.ANULADO);
        log.info("Adelanto anulado: id={}", id);
        Adelanto guardado = repository.save(adelanto);
        Map<Long, Integer> secuencialMap = obtenerMapaSecuencial();
        return map(guardado, secuencialMap.getOrDefault(guardado.getId(), 1));
    }

    @Transactional(readOnly = true)
    public BigDecimal totalActivoPorObrero(Long obreroId, LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);
        return repository.findByObreroIdAndEstadoAndFechaBetween(obreroId, EstadoAdelanto.REGISTRADO, desde, hasta)
                .stream().map(Adelanto::getImporte).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public AdelantoResumenResponse resumen(Long obreroId, LocalDate desde, LocalDate hasta) {
        LocalDate fechaHasta = hasta != null ? hasta : LocalDate.now();
        LocalDate fechaDesde = desde != null ? desde : fechaHasta.minusMonths(1);
        validarRango(fechaDesde, fechaHasta);
        List<AdelantoResponse> activos = listar(obreroId, fechaDesde, fechaHasta).stream()
                .filter(a -> a.estado() == EstadoAdelanto.REGISTRADO).toList();
        BigDecimal total = activos.stream().map(AdelantoResponse::importe).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AdelantoResumenResponse(fechaDesde, fechaHasta, obreroId, activos.size(), total);
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            throw new BusinessException("El rango de fechas no es válido");
        }
    }

    private Map<Long, Integer> obtenerMapaSecuencial() {
        List<Adelanto> todos = repository.findAllByOrderByFechaAscIdAsc();
        Map<Long, Integer> contadorObrero = new HashMap<>();
        Map<Long, Integer> secuencialMap = new HashMap<>();
        for (Adelanto a : todos) {
            Long obreroId = a.getObrero().getId();
            int seq = contadorObrero.getOrDefault(obreroId, 0) + 1;
            contadorObrero.put(obreroId, seq);
            secuencialMap.put(a.getId(), seq);
        }
        return secuencialMap;
    }

    private AdelantoResponse map(Adelanto a, Integer numeroSecuencial) {
        Obrero o = a.getObrero();
        return new AdelantoResponse(a.getId(), o.getId(), o.getDni(), o.getNombreCompleto(),
                a.getFecha(), a.getImporte(), a.getConcepto(), a.getEstado(), a.getCreadoEn(), numeroSecuencial);
    }
}
