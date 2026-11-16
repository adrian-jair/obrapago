package pe.edu.upeu.obrapago.adelantos.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.obrapago.adelantos.dto.*;
import pe.edu.upeu.obrapago.adelantos.service.AdelantoService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/adelantos")
public class AdelantoController {
    private final AdelantoService service;

    public AdelantoController(AdelantoService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<AdelantoResponse> crear(@Valid @RequestBody AdelantoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @GetMapping
    public List<AdelantoResponse> listar(
            @RequestParam(required = false) Long obreroId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return service.listar(obreroId, desde, hasta);
    }

    @GetMapping("/resumen")
    public AdelantoResumenResponse resumen(
            @RequestParam(required = false) Long obreroId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return service.resumen(obreroId, desde, hasta);
    }

    @PatchMapping("/{id}/anular")
    public AdelantoResponse anular(@PathVariable Long id) { return service.anular(id); }
}
