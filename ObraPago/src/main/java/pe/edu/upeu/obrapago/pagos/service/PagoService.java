package pe.edu.upeu.obrapago.pagos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.obrapago.adelantos.service.AdelantoService;
import pe.edu.upeu.obrapago.asistencia.dto.ResumenLaboralObrero;
import pe.edu.upeu.obrapago.asistencia.service.AsistenciaService;
import pe.edu.upeu.obrapago.common.exception.BusinessException;
import pe.edu.upeu.obrapago.common.exception.ResourceNotFoundException;
import pe.edu.upeu.obrapago.mensajes.repository.MensajePagoRepository;
import pe.edu.upeu.obrapago.obreros.entity.Obrero;
import pe.edu.upeu.obrapago.obreros.service.ObreroService;
import pe.edu.upeu.obrapago.pagos.dto.*;
import pe.edu.upeu.obrapago.pagos.entity.*;
import pe.edu.upeu.obrapago.pagos.repository.PlanillaPagoRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PagoService {
    private static final Logger log = LoggerFactory.getLogger(PagoService.class);
    private final PlanillaPagoRepository repository;
    private final AsistenciaService asistenciaService;
    private final AdelantoService adelantoService;
    private final ObreroService obreroService;
    private final MensajePagoRepository mensajePagoRepository;

    public PagoService(PlanillaPagoRepository repository, AsistenciaService asistenciaService,
                       AdelantoService adelantoService, ObreroService obreroService,
                       MensajePagoRepository mensajePagoRepository) {
        this.repository = repository;
        this.asistenciaService = asistenciaService;
        this.adelantoService = adelantoService;
        this.obreroService = obreroService;
        this.mensajePagoRepository = mensajePagoRepository;
    }

    @Transactional
    public PlanillaPagoResponse procesar(ProcesarPagoRequest request) {
        validarRango(request.fechaInicio(), request.fechaFin());
        if (repository.existsByFechaInicioAndFechaFin(request.fechaInicio(), request.fechaFin())) {
            throw new BusinessException("Ya existe una planilla para ese periodo");
        }

        Map<Long, ResumenLaboralObrero> resumenes = asistenciaService
                .resumenLaboralPorObrero(request.fechaInicio(), request.fechaFin());
        if (resumenes.isEmpty()) {
            throw new BusinessException("No existen asistencias laboradas en el periodo indicado");
        }

        PlanillaPago planilla = new PlanillaPago();
        planilla.setFechaInicio(request.fechaInicio());
        planilla.setFechaFin(request.fechaFin());
        planilla.setEstado(EstadoPlanilla.GENERADA);

        BigDecimal descuentoTardanzaUnitaria = request.descuentoPorTardanza() != null 
                ? request.descuentoPorTardanza() 
                : new BigDecimal("5.00"); // Descuento de S/ 5.00 por tardanza por defecto

        BigDecimal total = BigDecimal.ZERO;
        for (ResumenLaboralObrero resumen : resumenes.values()) {
            Obrero obrero = obreroService.buscarActivo(resumen.obreroId());
            BigDecimal tarifaJornal = obrero.getTarifaJornal().setScale(2, RoundingMode.HALF_UP);
            BigDecimal tarifaHora = tarifaJornal.divide(new BigDecimal("8"), 2, RoundingMode.HALF_UP);
            
            BigDecimal pagoJornales = tarifaJornal.multiply(BigDecimal.valueOf(resumen.diasLaborados()));
            BigDecimal pagoHorasExtra = tarifaHora.multiply(resumen.horasExtra());

            // Descuento manejable por tardanzas
            BigDecimal totalDescuentoTardanzas = descuentoTardanzaUnitaria.multiply(BigDecimal.valueOf(resumen.tardanzas()));

            // Bruto = Jornales + Horas Extra - Tardanzas
            BigDecimal bruto = pagoJornales.add(pagoHorasExtra).subtract(totalDescuentoTardanzas)
                    .max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

            BigDecimal adelantos = adelantoService.totalActivoPorObrero(
                    obrero.getId(), request.fechaInicio(), request.fechaFin()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal neto = bruto.subtract(adelantos).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

            DetallePago detalle = new DetallePago();
            detalle.setObrero(obrero);
            detalle.setDiasLaborados(resumen.diasLaborados());
            detalle.setHorasExtra(resumen.horasExtra());
            detalle.setTarifaJornal(tarifaJornal);
            detalle.setTarifaHora(tarifaHora);
            detalle.setBruto(bruto);
            detalle.setAdelantos(adelantos);
            detalle.setNeto(neto);
            planilla.agregarDetalle(detalle);
            total = total.add(neto);
        }

        planilla.setTotalNeto(total.setScale(2, RoundingMode.HALF_UP));
        PlanillaPago guardada = repository.save(planilla);
        log.info("Planilla generada: id={}, periodo={}..{}, total={}", guardada.getId(), guardada.getFechaInicio(), guardada.getFechaFin(), guardada.getTotalNeto());
        Map<Long, Integer> mapa = obtenerMapaSecuencial();
        return map(guardada, mapa.getOrDefault(guardada.getId(), 1));
    }

    @Transactional(readOnly = true)
    public List<PlanillaPagoResponse> listar() {
        Map<Long, Integer> mapa = obtenerMapaSecuencial();
        return repository.findAllByOrderByFechaInicioDesc().stream()
                .map(p -> map(p, mapa.getOrDefault(p.getId(), 1))).toList();
    }

    @Transactional(readOnly = true)
    public PlanillaPagoResponse obtener(Long id) { 
        PlanillaPago p = buscarEntidad(id);
        Map<Long, Integer> mapa = obtenerMapaSecuencial();
        return map(p, mapa.getOrDefault(p.getId(), 1)); 
    }

    @Transactional
    public PlanillaPagoResponse marcarPagada(Long id) {
        PlanillaPago planilla = buscarEntidad(id);
        if (planilla.getEstado() == EstadoPlanilla.ANULADA) {
            throw new BusinessException("No se puede pagar una planilla anulada");
        }
        planilla.setEstado(EstadoPlanilla.PAGADA);
        PlanillaPago guardada = repository.save(planilla);
        Map<Long, Integer> mapa = obtenerMapaSecuencial();
        return map(guardada, mapa.getOrDefault(guardada.getId(), 1));
    }

    @Transactional
    public PlanillaPagoResponse anular(Long id) {
        PlanillaPago planilla = buscarEntidad(id);
        planilla.setEstado(EstadoPlanilla.ANULADA);
        PlanillaPago guardada = repository.save(planilla);
        Map<Long, Integer> mapa = obtenerMapaSecuencial();
        return map(guardada, mapa.getOrDefault(guardada.getId(), 1));
    }

    @Transactional
    public void eliminar(Long id) {
        PlanillaPago planilla = buscarEntidad(id);
        var mensajes = mensajePagoRepository.findByPlanillaIdOrderByEnviadoEnDesc(id);
        if (!mensajes.isEmpty()) {
            mensajePagoRepository.deleteAll(mensajes);
        }
        repository.delete(planilla);
        log.info("Planilla eliminada con id: {}", id);
    }

    @Transactional(readOnly = true)
    public PlanillaPago buscarEntidad(Long id) {
        return repository.findOneById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planilla de pago no encontrada con id " + id));
    }

    private void validarRango(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null || inicio.isAfter(fin)) {
            throw new BusinessException("El periodo de pago no es válido");
        }
        if (inicio.plusDays(31).isBefore(fin)) {
            throw new BusinessException("El periodo no puede superar 31 días");
        }
    }

    @Transactional(readOnly = true)
    public List<DetallePagoResponse> obtenerDetallesPorPlanilla(Long planillaId) {
        PlanillaPago p = buscarEntidad(planillaId);
        return p.getDetalles().stream()
                .sorted(java.util.Comparator.comparing((DetallePago d) -> d.getObrero().getApellidos())
                        .thenComparing(d -> d.getObrero().getNombres()))
                .map(d -> new DetallePagoResponse(
                        d.getId(), d.getObrero().getId(), d.getObrero().getDni(),
                        d.getObrero().getNombreCompleto(),
                        d.getObrero().getCelular(), d.getDiasLaborados(), d.getHorasExtra(), d.getTarifaJornal(),
                        d.getTarifaHora(), d.getBruto(), d.getAdelantos(), d.getNeto())).toList();
    }

    @Transactional(readOnly = true)
    public DetallePagoResponse obtenerDetallePorId(Long detalleId) {
        return repository.findAll().stream()
                .flatMap(p -> p.getDetalles().stream())
                .filter(d -> d.getId().equals(detalleId))
                .findFirst()
                .map(d -> new DetallePagoResponse(
                        d.getId(), d.getObrero().getId(), d.getObrero().getDni(),
                        d.getObrero().getNombreCompleto(),
                        d.getObrero().getCelular(), d.getDiasLaborados(), d.getHorasExtra(), d.getTarifaJornal(),
                        d.getTarifaHora(), d.getBruto(), d.getAdelantos(), d.getNeto()))
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de pago no encontrado con id " + detalleId));
    }

    @Transactional(readOnly = true)
    public List<DetallePagoResponse> listarPorObrero(Long obreroId) {
        return repository.findAllByOrderByFechaInicioDesc().stream()
                .flatMap(p -> p.getDetalles().stream())
                .filter(d -> d.getObrero().getId().equals(obreroId))
                .map(d -> new DetallePagoResponse(
                        d.getId(), d.getObrero().getId(), d.getObrero().getDni(),
                        d.getObrero().getNombreCompleto(),
                        d.getObrero().getCelular(), d.getDiasLaborados(), d.getHorasExtra(), d.getTarifaJornal(),
                        d.getTarifaHora(), d.getBruto(), d.getAdelantos(), d.getNeto()))
                .toList();
    }

    private Map<Long, Integer> obtenerMapaSecuencial() {
        List<PlanillaPago> todas = repository.findAllByOrderByFechaInicioAscIdAsc();
        Map<Long, Integer> mapa = new HashMap<>();
        int seq = 1;
        for (PlanillaPago p : todas) {
            mapa.put(p.getId(), seq++);
        }
        return mapa;
    }

    private PlanillaPagoResponse map(PlanillaPago p, Integer numeroSecuencial) {
        List<DetallePagoResponse> detalles = p.getDetalles().stream()
                .sorted(java.util.Comparator.comparing((DetallePago d) -> d.getObrero().getApellidos())
                        .thenComparing(d -> d.getObrero().getNombres()))
                .map(d -> new DetallePagoResponse(
                        d.getId(), d.getObrero().getId(), d.getObrero().getDni(),
                        d.getObrero().getNombreCompleto(),
                        d.getObrero().getCelular(), d.getDiasLaborados(), d.getHorasExtra(), d.getTarifaJornal(),
                        d.getTarifaHora(), d.getBruto(), d.getAdelantos(), d.getNeto())).toList();
        return new PlanillaPagoResponse(p.getId(), p.getFechaInicio(), p.getFechaFin(), p.getGeneradaEn(),
                p.getEstado(), detalles.size(), p.getTotalNeto(), detalles, numeroSecuencial);
    }
}