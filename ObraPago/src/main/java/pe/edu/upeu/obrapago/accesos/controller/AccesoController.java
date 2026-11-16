package pe.edu.upeu.obrapago.accesos.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.obrapago.accesos.dto.RegistroAccesoRequest;
import pe.edu.upeu.obrapago.accesos.dto.RegistroAccesoResponse;
import pe.edu.upeu.obrapago.accesos.service.AccesoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accesos")
public class AccesoController {

    private final AccesoService accesoService;

    public AccesoController(AccesoService accesoService) {
        this.accesoService = accesoService;
    }

    @PostMapping
    public ResponseEntity<RegistroAccesoResponse> registrar(@Valid @RequestBody RegistroAccesoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accesoService.registrar(req));
    }

    @GetMapping
    public List<RegistroAccesoResponse> listar(@RequestParam(required = false) String username) {
        return accesoService.listar(username);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        accesoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
