package pe.edu.upeu.obrapago.respaldo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.obrapago.common.exception.BusinessException;
import pe.edu.upeu.obrapago.common.exception.ResourceNotFoundException;
import pe.edu.upeu.obrapago.pagos.entity.DetallePago;
import pe.edu.upeu.obrapago.pagos.entity.PlanillaPago;
import pe.edu.upeu.obrapago.pagos.service.PagoService;
import pe.edu.upeu.obrapago.respaldo.dto.RespaldoResponse;
import pe.edu.upeu.obrapago.respaldo.entity.*;
import pe.edu.upeu.obrapago.respaldo.repository.RespaldoRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

@Service
public class RespaldoService {
    private static final Logger log = LoggerFactory.getLogger(RespaldoService.class);
    private final RespaldoRepository repository;
    private final PagoService pagoService;

    @Value("${app.respaldo.directorio:./respaldos}")
    private String directorio;

    public RespaldoService(RespaldoRepository repository, PagoService pagoService) {
        this.repository = repository;
        this.pagoService = pagoService;
    }

    @Transactional
    public RespaldoResponse generar(Long planillaId) {
        PlanillaPago planilla = pagoService.buscarEntidad(planillaId);
        try {
            Path dir = Paths.get(directorio).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String nombre = "planilla_" + planilla.getId() + "_" + planilla.getFechaInicio() + "_" + planilla.getFechaFin() + ".csv";
            Path archivo = dir.resolve(nombre);
            Files.writeString(archivo, construirCsv(planilla), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            Respaldo respaldo = new Respaldo();
            respaldo.setPlanilla(planilla);
            respaldo.setNombreArchivo(nombre);
            respaldo.setRutaArchivo(archivo.toString());
            respaldo.setEstado(EstadoRespaldo.GENERADO);
            Respaldo guardado = repository.save(respaldo);
            log.info("Respaldo CSV generado: id={}, archivo={}", guardado.getId(), archivo);
            return map(guardado);
        } catch (IOException ex) {
            throw new BusinessException("No se pudo generar el respaldo: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<RespaldoResponse> listar() {
        return repository.findAllByOrderByGeneradoEnDesc().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public Resource descargar(Long id) {
        Respaldo respaldo = buscar(id);
        Path path = Paths.get(respaldo.getRutaArchivo());
        if (!Files.exists(path)) throw new ResourceNotFoundException("El archivo de respaldo ya no existe en disco");
        return new FileSystemResource(path);
    }

    @Transactional(readOnly = true)
    public Respaldo buscar(Long id) {
        return repository.findOneById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Respaldo no encontrado con id " + id));
    }

    private String construirCsv(PlanillaPago p) {
        StringBuilder sb = new StringBuilder();
        sb.append("obrero_id,dni,nombre,dias_laborados,horas_extra,tarifa_jornal,tarifa_hora,bruto,adelantos,neto\n");
        for (DetallePago d : p.getDetalles()) {
            String nombre = (d.getObrero().getApellidos() + " " + d.getObrero().getNombres()).replace(",", " ");
            sb.append(d.getObrero().getId()).append(',').append(d.getObrero().getDni()).append(',')
                    .append(nombre).append(',').append(d.getDiasLaborados()).append(',')
                    .append(d.getHorasExtra()).append(',').append(d.getTarifaJornal()).append(',')
                    .append(d.getTarifaHora()).append(',').append(d.getBruto()).append(',')
                    .append(d.getAdelantos()).append(',').append(d.getNeto()).append('\n');
        }
        sb.append("TOTAL,,,,,,,,,").append(p.getTotalNeto()).append('\n');
        return sb.toString();
    }

    private RespaldoResponse map(Respaldo r) {
        return new RespaldoResponse(r.getId(), r.getPlanilla().getId(), r.getNombreArchivo(), r.getRutaArchivo(),
                r.getGeneradoEn(), r.getEstado(), "/api/v1/respaldos/" + r.getId() + "/descargar");
    }
}
