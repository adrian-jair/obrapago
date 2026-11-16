package pe.edu.upeu.obrapago.consultas.controller;

import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.obrapago.consultas.dto.ConsultaObreroPagoResponse;
import pe.edu.upeu.obrapago.consultas.service.ConsultaObreroService;

@RestController
@RequestMapping("/api/v1/consultas")
public class ConsultaObreroController {

    private final ConsultaObreroService consultaObreroService;

    public ConsultaObreroController(ConsultaObreroService consultaObreroService) {
        this.consultaObreroService = consultaObreroService;
    }

    @GetMapping("/obrero/{criterio}")
    public ConsultaObreroPagoResponse consultarPorDniOId(@PathVariable String criterio) {
        return consultaObreroService.consultarPorDniOId(criterio);
    }
}
