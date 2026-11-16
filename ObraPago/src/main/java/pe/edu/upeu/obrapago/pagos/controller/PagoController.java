package pe.edu.upeu.obrapago.pagos.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.obrapago.pagos.dto.DetallePagoResponse;
import pe.edu.upeu.obrapago.pagos.dto.PlanillaPagoResponse;
import pe.edu.upeu.obrapago.pagos.dto.ProcesarPagoRequest;
import pe.edu.upeu.obrapago.pagos.service.PagoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {
    private final PagoService service;

    public PagoController(PagoService service) { 
        this.service = service; 
    }

    @PostMapping("/procesar")
    public ResponseEntity<PlanillaPagoResponse> procesar(@Valid @RequestBody ProcesarPagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.procesar(request));
    }

    @GetMapping
    public List<PlanillaPagoResponse> listar() { 
        return service.listar(); 
    }

    @GetMapping("/{id}")
    public PlanillaPagoResponse obtener(@PathVariable Long id) { 
        return service.obtener(id); 
    }

    @PatchMapping("/{id}/pagar")
    public PlanillaPagoResponse marcarPagada(@PathVariable Long id) { 
        return service.marcarPagada(id); 
    }

    @PatchMapping("/{id}/anular")
    public PlanillaPagoResponse anular(@PathVariable Long id) { 
        return service.anular(id); 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ====== DETALLES DE PAGO ======

    @GetMapping("/{id}/detalles")
    public List<DetallePagoResponse> obtenerDetallesPorPlanilla(@PathVariable Long id) {
        return service.obtenerDetallesPorPlanilla(id);
    }

    @GetMapping("/detalles/{detalleId}")
    public DetallePagoResponse obtenerDetallePorId(@PathVariable Long detalleId) {
        return service.obtenerDetallePorId(detalleId);
    }

    @GetMapping("/obrero/{obreroId}")
    public List<DetallePagoResponse> listarPorObrero(@PathVariable Long obreroId) {
        return service.listarPorObrero(obreroId);
    }
}
