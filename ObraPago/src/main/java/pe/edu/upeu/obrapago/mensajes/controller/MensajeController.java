package pe.edu.upeu.obrapago.mensajes.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.obrapago.mensajes.dto.MensajeResponse;
import pe.edu.upeu.obrapago.mensajes.service.MensajeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mensajes")
public class MensajeController {
    private final MensajeService service;
    public MensajeController(MensajeService service) { this.service = service; }

    @PostMapping("/planillas/{planillaId}/enviar")
    public ResponseEntity<List<MensajeResponse>> enviar(@PathVariable Long planillaId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.enviarAvisos(planillaId));
    }

    @GetMapping
    public List<MensajeResponse> listar(@RequestParam(required = false) Long planillaId) {
        return service.listar(planillaId);
    }
}
