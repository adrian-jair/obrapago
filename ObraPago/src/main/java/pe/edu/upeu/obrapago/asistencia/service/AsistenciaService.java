package pe.edu.upeu.obrapago.asistencia.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.upeu.obrapago.asistencia.dto.*;
import pe.edu.upeu.obrapago.asistencia.entity.*;
import pe.edu.upeu.obrapago.asistencia.repository.DetalleAsistenciaRepository;
import pe.edu.upeu.obrapago.asistencia.repository.JornadaAsistenciaRepository;
import pe.edu.upeu.obrapago.common.exception.BusinessException;
import pe.edu.upeu.obrapago.common.exception.ResourceNotFoundException;
import pe.edu.upeu.obrapago.obreros.entity.Obrero;
import pe.edu.upeu.obrapago.obreros.service.ObreroService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AsistenciaService {

    private static final Logger log =
            LoggerFactory.getLogger(AsistenciaService.class);

    private final JornadaAsistenciaRepository jornadaRepository;
    private final DetalleAsistenciaRepository detalleRepository;
    private final ObreroService obreroService;

    public AsistenciaService(
            JornadaAsistenciaRepository jornadaRepository,
            DetalleAsistenciaRepository detalleRepository,
            ObreroService obreroService) {

        this.jornadaRepository = jornadaRepository;
        this.detalleRepository = detalleRepository;
        this.obreroService = obreroService;
    }

    // ============================================================
    // REGISTRAR ASISTENCIA
    // ============================================================

    @Transactional
    public AsistenciaResponse registrar(AsistenciaRequest request) {

        validarObrerosDuplicados(request);

        /*
         * Si ya existe una jornada para esa fecha,
         * utilizamos esa misma jornada.
         *
         * Si no existe, creamos una nueva como ABIERTA.
         */
        JornadaAsistencia jornada =
                jornadaRepository.findByFecha(request.fecha())
                        .orElseGet(() -> {

                            JornadaAsistencia nueva =
                                    new JornadaAsistencia();

                            nueva.setFecha(request.fecha());
                            nueva.setEstado(EstadoJornada.ABIERTA);

                            JornadaAsistencia creada =
                                    jornadaRepository.saveAndFlush(nueva);

                            log.info(
                                    "Nueva jornada ABIERTA creada: id={}, fecha={}",
                                    creada.getId(),
                                    creada.getFecha()
                            );

                            return creada;
                        });

        /*
         * Si la jornada ya fue cerrada,
         * no permitimos agregar más asistencias.
         */
        if (jornada.getEstado() == EstadoJornada.CERRADA) {

            throw new BusinessException(
                    "La jornada del " + request.fecha()
                            + " está CERRADA y ya no puede modificarse"
            );
        }

        /*
         * Sacamos los IDs de los obreros que YA están registrados
         * en esta jornada.
         */
        Set<Long> obrerosRegistrados = new HashSet<>();

        for (DetalleAsistencia detalle : jornada.getDetalles()) {

            obrerosRegistrados.add(
                    detalle.getObrero().getId()
            );
        }

        /*
         * Agregamos o actualizamos los detalles de asistencia.
         */
        for (DetalleAsistenciaRequest detalleRequest : request.detalles()) {

            Long obreroId = detalleRequest.obreroId();

            DetalleAsistencia existente = jornada.getDetalles().stream()
                    .filter(d -> d.getObrero() != null && d.getObrero().getId().equals(obreroId))
                    .findFirst()
                    .orElse(null);

            if (existente != null) {
                existente.setEstadoAsistencia(detalleRequest.estado());
                existente.setHorasExtra(detalleRequest.horasExtra());
                existente.setObservacion(detalleRequest.observacion());
            } else {
                Obrero obrero;
                try {
                    obrero = obreroService.buscarActivo(obreroId);
                } catch (ResourceNotFoundException ex) {
                    throw new BusinessException(
                            "No existe un obrero activo con id "
                                    + obreroId
                                    + ". Se revierte toda la operación."
                    );
                }

                DetalleAsistencia detalle = new DetalleAsistencia();
                detalle.setObrero(obrero);
                detalle.setEstadoAsistencia(detalleRequest.estado());
                detalle.setHorasExtra(detalleRequest.horasExtra());
                detalle.setObservacion(detalleRequest.observacion());

                jornada.agregarDetalle(detalle);
                obrerosRegistrados.add(obreroId);
            }
        }

        JornadaAsistencia guardada =
                jornadaRepository.saveAndFlush(jornada);

        log.info(
                "Jornada actualizada correctamente: id={}, fecha={}, estado={}, detalles={}",
                guardada.getId(),
                guardada.getFecha(),
                guardada.getEstado(),
                guardada.getDetalles().size()
        );

        return map(guardada);
    }


    // ============================================================
    // LISTAR ASISTENCIAS
    // ============================================================

    @Transactional(readOnly = true)
    public List<AsistenciaResponse> listar(
            LocalDate desde,
            LocalDate hasta,
            EstadoJornada estado) {

        LocalDate fechaHasta =
                hasta != null
                        ? hasta
                        : LocalDate.now();

        LocalDate fechaDesde =
                desde != null
                        ? desde
                        : fechaHasta.minusMonths(1);

        if (fechaDesde.isAfter(fechaHasta)) {

            throw new BusinessException(
                    "La fecha desde no puede ser posterior a la fecha hasta"
            );
        }

        List<JornadaAsistencia> jornadas =
                estado == null

                        ? jornadaRepository
                        .findByFechaBetweenOrderByFechaDesc(
                                fechaDesde,
                                fechaHasta
                        )

                        : jornadaRepository
                        .findByEstadoAndFechaBetweenOrderByFechaDesc(
                                estado,
                                fechaDesde,
                                fechaHasta
                        );

        return jornadas
                .stream()
                .map(this::map)
                .toList();
    }


    // ============================================================
    // RESUMEN GENERAL DE ASISTENCIAS
    // ============================================================

    @Transactional(readOnly = true)
    public AsistenciaResumenResponse resumen(
            LocalDate desde,
            LocalDate hasta) {

        LocalDate fechaHasta =
                hasta != null
                        ? hasta
                        : LocalDate.now();

        LocalDate fechaDesde =
                desde != null
                        ? desde
                        : fechaHasta.minusMonths(1);

        if (fechaDesde.isAfter(fechaHasta)) {

            throw new BusinessException(
                    "La fecha desde no puede ser posterior a la fecha hasta"
            );
        }

        List<JornadaAsistencia> jornadas =
                jornadaRepository
                        .findByFechaBetweenOrderByFechaDesc(
                                fechaDesde,
                                fechaHasta
                        );

        long presentes = 0;
        long tardanzas = 0;
        long faltasJustificadas = 0;
        long faltasInjustificadas = 0;

        BigDecimal horasExtra =
                BigDecimal.ZERO;

        for (JornadaAsistencia jornada : jornadas) {

            for (DetalleAsistencia d :
                    jornada.getDetalles()) {

                horasExtra =
                        horasExtra.add(
                                d.getHorasExtra()
                        );

                switch (d.getEstadoAsistencia()) {

                    case PRESENTE ->
                            presentes++;

                    case TARDANZA ->
                            tardanzas++;

                    case FALTA_JUSTIFICADA ->
                            faltasJustificadas++;

                    case FALTA_INJUSTIFICADA ->
                            faltasInjustificadas++;
                }
            }
        }

        return new AsistenciaResumenResponse(
                fechaDesde,
                fechaHasta,
                jornadas.size(),
                presentes,
                tardanzas,
                faltasJustificadas,
                faltasInjustificadas,
                horasExtra
        );
    }


    // ============================================================
    // RESUMEN LABORAL PARA EL MÓDULO PAGOS
    // ============================================================

@Transactional(readOnly = true)
    public Map<Long, ResumenLaboralObrero> resumenLaboralPorObrero(
            LocalDate desde,
            LocalDate hasta) {

        if (desde == null
                || hasta == null
                || desde.isAfter(hasta)) {
            throw new BusinessException("El rango de fechas no es válido");
        }

        List<JornadaAsistencia> jornadas =
                jornadaRepository.findByFechaBetweenOrderByFechaDesc(desde, hasta);

        Map<Long, Long> dias = new HashMap<>();
        Map<Long, Long> tardanzas = new HashMap<>();
        Map<Long, BigDecimal> horas = new HashMap<>();

        for (JornadaAsistencia jornada : jornadas) {
            for (DetalleAsistencia detalle : jornada.getDetalles()) {
                Long obreroId = detalle.getObrero().getId();

                // 1. Días a remunerar: PRESENTE, TARDANZA y FALTA_JUSTIFICADA (permiso médico / salud)
                if (detalle.getEstadoAsistencia() == EstadoAsistencia.PRESENTE
                        || detalle.getEstadoAsistencia() == EstadoAsistencia.TARDANZA
                        || detalle.getEstadoAsistencia() == EstadoAsistencia.FALTA_JUSTIFICADA) {

                    dias.merge(obreroId, 1L, Long::sum);

                    if (detalle.getHorasExtra() != null) {
                        horas.merge(obreroId, detalle.getHorasExtra(), BigDecimal::add);
                    }
                }

                // 2. Conteo de tardanzas por separado para aplicar el descuento manejable
                if (detalle.getEstadoAsistencia() == EstadoAsistencia.TARDANZA) {
                    tardanzas.merge(obreroId, 1L, Long::sum);
                }
            }
        }

        Map<Long, ResumenLaboralObrero> resultado = new HashMap<>();

        for (Long obreroId : dias.keySet()) {
            resultado.put(
                    obreroId,
                    new ResumenLaboralObrero(
                            obreroId,
                            dias.get(obreroId),
                            tardanzas.getOrDefault(obreroId, 0L),
                            horas.getOrDefault(obreroId, BigDecimal.ZERO)
                    )
            );
        }

        return resultado;
    }

    // ============================================================
    // VALIDAR DUPLICADOS DENTRO DEL MISMO REQUEST
    // ============================================================

    private void validarObrerosDuplicados(
            AsistenciaRequest request) {

        Set<Long> ids =
                new HashSet<>();

        for (DetalleAsistenciaRequest detalle :
                request.detalles()) {

            if (!ids.add(detalle.obreroId())) {

                throw new BusinessException(
                        "El obrero con id "
                                + detalle.obreroId()
                                + " está repetido en la jornada"
                );
            }
        }
    }


    // ============================================================
    // CONVERTIR ENTIDAD A DTO
    // ============================================================

    private AsistenciaResponse map(
            JornadaAsistencia jornada) {

        List<DetalleAsistenciaResponse> detalles =
                jornada
                        .getDetalles()
                        .stream()
                        .map(d ->
                                new DetalleAsistenciaResponse(

                                        d.getId(),

                                        d.getObrero()
                                                .getId(),

                                        d.getObrero()
                                                .getDni(),

                                        d.getObrero()
                                                .getNombreCompleto(),

                                        d.getEstadoAsistencia(),

                                        d.getHorasExtra(),

                                        d.getObservacion()
                                )
                        )
                        .toList();

        long presentes =
                detalles.stream()
                        .filter(d ->
                                d.estado()
                                        == EstadoAsistencia.PRESENTE
                        )
                        .count();

        long tardanzas =
                detalles.stream()
                        .filter(d ->
                                d.estado()
                                        == EstadoAsistencia.TARDANZA
                        )
                        .count();

        long faltas =
                detalles.stream()
                        .filter(d ->

                                d.estado()
                                        == EstadoAsistencia.FALTA_JUSTIFICADA

                                        ||

                                        d.estado()
                                                == EstadoAsistencia.FALTA_INJUSTIFICADA
                        )
                        .count();

        BigDecimal totalHorasExtra =
                detalles.stream()
                        .map(
                                DetalleAsistenciaResponse
                                        ::horasExtra
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        return new AsistenciaResponse(
                jornada.getId(),
                jornada.getFecha(),
                jornada.getEstado(),
                detalles.size(),
                presentes,
                tardanzas,
                faltas,
                totalHorasExtra,
                detalles
        );
    }

    @Transactional(readOnly = true)
    public AsistenciaResponse obtenerPorId(Long id) {
        return jornadaRepository.findById(id)
                .map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException("Jornada no encontrada con id: " + id));
    }

    @Transactional(readOnly = true)
    public AsistenciaResponse obtenerPorFecha(LocalDate fecha) {
        return jornadaRepository.findByFecha(fecha)
                .map(this::map)
                .orElse(null);
    }

    @Transactional
    public AsistenciaResponse cerrarJornada(Long id) {
        JornadaAsistencia j = jornadaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jornada no encontrada con id: " + id));
        j.setEstado(EstadoJornada.CERRADA);
        return map(jornadaRepository.save(j));
    }

    @Transactional(readOnly = true)
    public List<DetalleAsistenciaItemResponse> listarDetalles(
            Long jornadaId,
            Long obreroId,
            LocalDate desde,
            LocalDate hasta,
            EstadoAsistencia estado) {

        List<DetalleAsistencia> lista;
        if (jornadaId != null) {
            lista = detalleRepository.findByJornadaId(jornadaId);
        } else if (obreroId != null && desde != null && hasta != null) {
            lista = detalleRepository.findByObreroIdAndJornadaFechaBetweenOrderByJornadaFechaDesc(obreroId, desde, hasta);
        } else if (obreroId != null) {
            lista = detalleRepository.findByObreroIdOrderByJornadaFechaDesc(obreroId);
        } else {
            lista = detalleRepository.findAll();
        }

        // Filtro de estado combinado (permite filtrar obrero + estado, fechas + estado, etc.)
        if (estado != null) {
            lista = lista.stream()
                    .filter(d -> d.getEstadoAsistencia() == estado)
                    .toList();
        }

        // Filtro de fechas si no se aplicó en el repositorio
        if (desde != null && hasta != null && obreroId == null && jornadaId == null) {
            lista = lista.stream()
                    .filter(d -> d.getJornada() != null && d.getJornada().getFecha() != null
                            && !d.getJornada().getFecha().isBefore(desde)
                            && !d.getJornada().getFecha().isAfter(hasta))
                    .toList();
        }

        return lista.stream().map(this::mapItem).toList();
    }

    @Transactional
    public DetalleAsistenciaItemResponse actualizarDetalle(
            Long detalleId,
            EstadoAsistencia estado,
            BigDecimal horasExtra,
            String observacion) {

        DetalleAsistencia d = detalleRepository.findById(detalleId)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de asistencia no encontrado con id: " + detalleId));

        if (d.getJornada() != null && d.getJornada().getEstado() == EstadoJornada.CERRADA) {
            throw new BusinessException("No se puede editar un detalle de una jornada CERRADA");
        }

        if (estado != null) {
            d.setEstadoAsistencia(estado);
        }
        if (horasExtra != null) {
            d.setHorasExtra(horasExtra);
        }
        if (observacion != null) {
            d.setObservacion(observacion);
        }

        return mapItem(detalleRepository.save(d));
    }

    private DetalleAsistenciaItemResponse mapItem(DetalleAsistencia d) {
        return new DetalleAsistenciaItemResponse(
                d.getId(),
                d.getJornada() != null ? d.getJornada().getId() : null,
                d.getJornada() != null ? d.getJornada().getFecha() : null,
                d.getObrero().getId(),
                d.getObrero().getDni(),
                d.getObrero().getNombreCompleto(),
                d.getObrero().getOficio() != null ? d.getObrero().getOficio().name() : "",
                d.getEstadoAsistencia(),
                d.getHorasExtra(),
                d.getObservacion(),
                d.getDocumentoUrl()
        );
    }
}