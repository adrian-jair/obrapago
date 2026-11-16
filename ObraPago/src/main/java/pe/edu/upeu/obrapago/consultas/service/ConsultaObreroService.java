package pe.edu.upeu.obrapago.consultas.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.obrapago.adelantos.dto.AdelantoResponse;
import pe.edu.upeu.obrapago.adelantos.entity.EstadoAdelanto;
import pe.edu.upeu.obrapago.adelantos.service.AdelantoService;
import pe.edu.upeu.obrapago.asistencia.dto.DetalleAsistenciaItemResponse;
import pe.edu.upeu.obrapago.asistencia.entity.EstadoAsistencia;
import pe.edu.upeu.obrapago.asistencia.service.AsistenciaService;
import pe.edu.upeu.obrapago.common.exception.ResourceNotFoundException;
import pe.edu.upeu.obrapago.consultas.dto.ConsultaObreroPagoResponse;
import pe.edu.upeu.obrapago.contratos.dto.ContratoResponse;
import pe.edu.upeu.obrapago.contratos.service.ContratoService;
import pe.edu.upeu.obrapago.obreros.dto.ObreroResponse;
import pe.edu.upeu.obrapago.obreros.entity.Obrero;
import pe.edu.upeu.obrapago.obreros.repository.ObreroRepository;
import pe.edu.upeu.obrapago.obreros.service.ObreroService;
import pe.edu.upeu.obrapago.pagos.dto.DetallePagoResponse;
import pe.edu.upeu.obrapago.pagos.service.PagoService;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ConsultaObreroService {

    private final ObreroRepository obreroRepository;
    private final ObreroService obreroService;
    private final ContratoService contratoService;
    private final AsistenciaService asistenciaService;
    private final AdelantoService adelantoService;
    private final PagoService pagoService;

    public ConsultaObreroService(
            ObreroRepository obreroRepository,
            ObreroService obreroService,
            ContratoService contratoService,
            AsistenciaService asistenciaService,
            AdelantoService adelantoService,
            PagoService pagoService) {
        this.obreroRepository = obreroRepository;
        this.obreroService = obreroService;
        this.contratoService = contratoService;
        this.asistenciaService = asistenciaService;
        this.adelantoService = adelantoService;
        this.pagoService = pagoService;
    }

    @Transactional(readOnly = true)
    public ConsultaObreroPagoResponse consultarPorDniOId(String criterio) {
        String query = criterio.trim();
        Obrero obrero;

        if (query.matches("\\d{8}")) {
            obrero = obreroRepository.findByDni(query)
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró ningún obrero con DNI: " + query));
        } else if (query.matches("\\d+")) {
            obrero = obreroRepository.findById(Long.parseLong(query))
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró ningún obrero con ID: " + query));
        } else {
            obrero = obreroRepository.findByDni(query)
                    .orElseThrow(() -> new ResourceNotFoundException("Criterio inválido o no encontrado: " + query));
        }

        Long obreroId = obrero.getId();
        ObreroResponse obreroResp = obreroService.obtener(obreroId);
        ContratoResponse contratoVigente = contratoService.obtenerVigentePorObrero(obreroId);

        // Asistencias
        List<DetalleAsistenciaItemResponse> asistencias = asistenciaService.listarDetalles(null, obreroId, null, null, null);
        long presentes = asistencias.stream().filter(a -> a.estadoAsistencia() == EstadoAsistencia.PRESENTE).count();
        long tardanzas = asistencias.stream().filter(a -> a.estadoAsistencia() == EstadoAsistencia.TARDANZA).count();
        long faltas = asistencias.stream().filter(a -> 
                a.estadoAsistencia() == EstadoAsistencia.FALTA_JUSTIFICADA || 
                a.estadoAsistencia() == EstadoAsistencia.FALTA_INJUSTIFICADA).count();

        BigDecimal horasExtra = asistencias.stream()
                .map(a -> a.horasExtra() != null ? a.horasExtra() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Adelantos
        List<AdelantoResponse> adelantos = adelantoService.listarPorObrero(obreroId);
        BigDecimal totalAdelantos = adelantos.stream()
                .filter(a -> a.estado() == EstadoAdelanto.REGISTRADO)
                .map(AdelantoResponse::importe)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Pagos
        List<DetallePagoResponse> pagos = pagoService.listarPorObrero(obreroId);
        BigDecimal totalNetoCobrado = pagos.stream()
                .map(DetallePagoResponse::neto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ConsultaObreroPagoResponse(
                obreroResp,
                contratoVigente,
                presentes,
                tardanzas,
                faltas,
                horasExtra,
                asistencias,
                totalAdelantos,
                adelantos,
                totalNetoCobrado,
                pagos
        );
    }
}
