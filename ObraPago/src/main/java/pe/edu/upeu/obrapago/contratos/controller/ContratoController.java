package pe.edu.upeu.obrapago.contratos.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.obrapago.contratos.dto.ContratoRequest;
import pe.edu.upeu.obrapago.contratos.dto.ContratoResponse;
import pe.edu.upeu.obrapago.contratos.entity.EstadoContrato;
import pe.edu.upeu.obrapago.contratos.service.ContratoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contratos")
public class ContratoController {

    private final ContratoService contratoService;

    public ContratoController(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    @GetMapping
    public List<ContratoResponse> listar(
            @RequestParam(required = false) Long obreroId,
            @RequestParam(required = false) EstadoContrato estado) {
        return contratoService.listar(obreroId, estado);
    }

    @GetMapping("/{id}")
    public ContratoResponse obtenerPorId(@PathVariable Long id) {
        return contratoService.obtenerPorId(id);
    }

    @GetMapping("/obrero/{obreroId}/vigente")
    public ResponseEntity<ContratoResponse> obtenerVigentePorObrero(@PathVariable Long obreroId) {
        ContratoResponse res = contratoService.obtenerVigentePorObrero(obreroId);
        return res != null ? ResponseEntity.ok(res) : ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ContratoResponse> crear(@Valid @RequestBody ContratoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contratoService.crear(req));
    }

    @PutMapping("/{id}")
    public ContratoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ContratoRequest req) {
        return contratoService.actualizar(id, req);
    }

    @PatchMapping("/{id}/estado")
    public ContratoResponse cambiarEstado(@PathVariable Long id, @RequestParam EstadoContrato estado) {
        return contratoService.cambiarEstado(id, estado);
    }
}
