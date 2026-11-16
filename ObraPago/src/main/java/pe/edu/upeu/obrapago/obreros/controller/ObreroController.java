package pe.edu.upeu.obrapago.obreros.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.obrapago.obreros.dto.ObreroRequest;
import pe.edu.upeu.obrapago.obreros.dto.ObreroResponse;
import pe.edu.upeu.obrapago.obreros.service.ObreroService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/obreros")
public class ObreroController {

    private final ObreroService obreroService;

    public ObreroController(ObreroService obreroService) {
        this.obreroService = obreroService;
    }

    @PostMapping
    public ResponseEntity<ObreroResponse> crear(@Valid @RequestBody ObreroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(obreroService.crear(request));
    }

    @GetMapping
    public List<ObreroResponse> listar() {
        return obreroService.listar();
    }

    @GetMapping("/{id}")
    public ObreroResponse obtener(@PathVariable Long id) {
        return obreroService.obtener(id);
    }

    @GetMapping("/dni/{dni}")
    public ObreroResponse obtenerPorDni(@PathVariable String dni) {
        return obreroService.obtenerPorDni(dni);
    }

    @PutMapping("/{id}")
    public ObreroResponse actualizar(@PathVariable Long id, @Valid @RequestBody ObreroRequest request) {
        return obreroService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        obreroService.eliminar(id);
    }
}
