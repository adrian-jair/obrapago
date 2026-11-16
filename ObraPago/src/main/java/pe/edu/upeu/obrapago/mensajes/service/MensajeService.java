package pe.edu.upeu.obrapago.mensajes.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import pe.edu.upeu.obrapago.mensajes.dto.MensajeResponse;
import pe.edu.upeu.obrapago.mensajes.entity.*;
import pe.edu.upeu.obrapago.mensajes.repository.MensajePagoRepository;
import pe.edu.upeu.obrapago.pagos.entity.DetallePago;
import pe.edu.upeu.obrapago.pagos.entity.PlanillaPago;
import pe.edu.upeu.obrapago.pagos.service.PagoService;

import java.util.List;
import java.util.Map;

@Service
public class MensajeService {
    private static final Logger log = LoggerFactory.getLogger(MensajeService.class);
    private final MensajePagoRepository repository;
    private final PagoService pagoService;
    private final RestClient restClient;

    @Value("${app.mensajeria.modo:SIMULADO}")
    private String modo;

    @Value("${app.mensajeria.webhook-url:}")
    private String webhookUrl;

    public MensajeService(MensajePagoRepository repository, PagoService pagoService, RestClient.Builder builder) {
        this.repository = repository;
        this.pagoService = pagoService;
        this.restClient = builder.build();
    }

    @Transactional
    public List<MensajeResponse> enviarAvisos(Long planillaId) {
        PlanillaPago planilla = pagoService.buscarEntidad(planillaId);
        return planilla.getDetalles().stream().map(detalle -> guardarYEnviar(planilla, detalle)).toList();
    }

    private MensajeResponse guardarYEnviar(PlanillaPago planilla, DetallePago detalle) {
        String contenido = "ObraPago: " + detalle.getObrero().getNombres() + ", su pago del "
                + planilla.getFechaInicio() + " al " + planilla.getFechaFin()
                + " es S/ " + detalle.getNeto() + ". Bruto S/ " + detalle.getBruto()
                + ", adelantos S/ " + detalle.getAdelantos() + ".";

        MensajePago mensaje = new MensajePago();
        mensaje.setPlanilla(planilla);
        mensaje.setObrero(detalle.getObrero());
        mensaje.setCelular(detalle.getObrero().getCelular());
        mensaje.setContenido(contenido);

        if (!"WEBHOOK".equalsIgnoreCase(modo)) {
            mensaje.setEstado(EstadoMensaje.SIMULADO);
        } else if (webhookUrl == null || webhookUrl.isBlank()) {
            mensaje.setEstado(EstadoMensaje.ERROR);
            mensaje.setDetalleError("No se configuró app.mensajeria.webhook-url");
        } else if (mensaje.getCelular() == null || mensaje.getCelular().isBlank()) {
            mensaje.setEstado(EstadoMensaje.ERROR);
            mensaje.setDetalleError("El obrero no tiene celular registrado");
        } else {
            try {
                restClient.post().uri(webhookUrl)
                        .body(Map.of("to", mensaje.getCelular(), "message", contenido))
                        .retrieve().toBodilessEntity();
                mensaje.setEstado(EstadoMensaje.ENVIADO);
            } catch (Exception ex) {
                mensaje.setEstado(EstadoMensaje.ERROR);
                mensaje.setDetalleError(ex.getMessage() != null ? ex.getMessage().substring(0, Math.min(490, ex.getMessage().length())) : "Error HTTP");
            }
        }
        MensajePago guardado = repository.save(mensaje);
        log.info("Aviso de pago registrado: id={}, planilla={}, estado={}", guardado.getId(), planilla.getId(), guardado.getEstado());
        return map(guardado);
    }

    @Transactional(readOnly = true)
    public List<MensajeResponse> listar(Long planillaId) {
        List<MensajePago> lista = planillaId == null
                ? repository.findAllByOrderByEnviadoEnDesc()
                : repository.findByPlanillaIdOrderByEnviadoEnDesc(planillaId);
        return lista.stream().map(this::map).toList();
    }

    private MensajeResponse map(MensajePago m) {
        return new MensajeResponse(m.getId(), m.getPlanilla().getId(), m.getObrero().getId(),
                m.getObrero().getNombreCompleto(), m.getCelular(),
                m.getContenido(), m.getEstado(), m.getEnviadoEn(), m.getDetalleError());
    }
}
