package pe.edu.upeu.obrapago.respaldo.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.obrapago.respaldo.dto.RespaldoResponse;
import pe.edu.upeu.obrapago.respaldo.entity.Respaldo;
import pe.edu.upeu.obrapago.respaldo.service.RespaldoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/respaldos")
public class RespaldoController {
    private final RespaldoService service;
    public RespaldoController(RespaldoService service) { this.service = service; }

    @PostMapping("/planillas/{planillaId}")
    public ResponseEntity<RespaldoResponse> generar(@PathVariable Long planillaId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.generar(planillaId));
    }

    @GetMapping
    public List<RespaldoResponse> listar() { return service.listar(); }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<Resource> descargar(@PathVariable Long id) {
        Respaldo respaldo = service.buscar(id);
        Resource recurso = service.descargar(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + respaldo.getNombreArchivo() + "\"")
                .body(recurso);
    }
}
