package pe.edu.upeu.obrapago.asistencia.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.obrapago.asistencia.dto.AsistenciaRequest;
import pe.edu.upeu.obrapago.asistencia.dto.AsistenciaResponse;
import pe.edu.upeu.obrapago.asistencia.dto.AsistenciaResumenResponse;
import pe.edu.upeu.obrapago.asistencia.dto.DetalleAsistenciaItemResponse;
import pe.edu.upeu.obrapago.asistencia.entity.EstadoAsistencia;
import pe.edu.upeu.obrapago.asistencia.entity.EstadoJornada;
import pe.edu.upeu.obrapago.asistencia.service.AsistenciaService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/asistencias")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @PostMapping
    public ResponseEntity<AsistenciaResponse> registrar(@Valid @RequestBody AsistenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(asistenciaService.registrar(request));
    }

    @GetMapping
    public List<AsistenciaResponse> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) EstadoJornada estado) {
        return asistenciaService.listar(desde, hasta, estado);
    }

    @GetMapping("/{id}")
    public AsistenciaResponse obtenerPorId(@PathVariable Long id) {
        return asistenciaService.obtenerPorId(id);
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<AsistenciaResponse> obtenerPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        AsistenciaResponse resp = asistenciaService.obtenerPorFecha(fecha);
        return resp != null ? ResponseEntity.ok(resp) : ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cerrar")
    public AsistenciaResponse cerrarJornada(@PathVariable Long id) {
        return asistenciaService.cerrarJornada(id);
    }

    @GetMapping("/resumen")
    public AsistenciaResumenResponse resumen(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return asistenciaService.resumen(desde, hasta);
    }

    // ====== DETALLES DE ASISTENCIA ======

    @GetMapping("/detalles")
    public List<DetalleAsistenciaItemResponse> listarDetalles(
            @RequestParam(required = false) Long jornadaId,
            @RequestParam(required = false) Long obreroId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) EstadoAsistencia estado) {
        return asistenciaService.listarDetalles(jornadaId, obreroId, desde, hasta, estado);
    }

    @PutMapping("/detalles/{id}")
    public DetalleAsistenciaItemResponse actualizarDetalle(
            @PathVariable Long id,
            @RequestParam(required = false) EstadoAsistencia estado,
            @RequestParam(required = false) BigDecimal horasExtra,
            @RequestParam(required = false) String observacion) {
        return asistenciaService.actualizarDetalle(id, estado, horasExtra, observacion);
    }
}
