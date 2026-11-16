package pe.edu.upeu.obrapago.obras.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.obrapago.obras.dto.AsignacionMasivaRequest;
import pe.edu.upeu.obrapago.obras.dto.ObraRequest;
import pe.edu.upeu.obrapago.obras.dto.ObraResponse;
import pe.edu.upeu.obrapago.obras.entity.EstadoObra;
import pe.edu.upeu.obrapago.obras.service.ObraService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/obras")
public class ObraController {

    private final ObraService obraService;

    public ObraController(ObraService obraService) {
        this.obraService = obraService;
    }

    @GetMapping
    public List<ObraResponse> listar(
            @RequestParam(required = false) EstadoObra estado,
            @RequestParam(required = false) String buscar) {
        return obraService.listar(estado, buscar);
    }

    @GetMapping("/{id}")
    public ObraResponse obtenerPorId(@PathVariable Long id) {
        return obraService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<ObraResponse> crear(@Valid @RequestBody ObraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(obraService.crear(request));
    }

    @PutMapping("/{id}")
    public ObraResponse actualizar(@PathVariable Long id, @Valid @RequestBody ObraRequest request) {
        return obraService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        obraService.eliminar(id);
    }

    /**
     * Endpoint estrella: Asignación masiva de cuadrilla / obreros a la obra.
     * Idéntico al guardado en bloque de Asistencia.
     */
    @PutMapping("/{id}/obreros")
    public ObraResponse asignarObrerosMasivo(
            @PathVariable Long id,
            @Valid @RequestBody AsignacionMasivaRequest request) {
        return obraService.asignarObrerosMasivo(id, request.obreroIds());
    }

    /**
     * Endpoint para consultar qué obras tiene asignadas un obrero específico (para Módulo 10).
     */
    @GetMapping("/obrero/{obreroId}")
    public List<ObraResponse> listarPorObrero(@PathVariable Long obreroId) {
        return obraService.listarPorObrero(obreroId);
    }
}
