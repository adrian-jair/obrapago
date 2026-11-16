package pe.edu.upeu.obrapago.obreros.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reniec")
@CrossOrigin(origins = "*")
public class ReniecController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/{dni}")
    public ResponseEntity<?> consultarDni(@PathVariable String dni) {
        try {
            String url = "https://api.decolecta.com/v1/reniec/dni?numero=" + dni;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer sk_20428.JIhrZYkeUB3wTNrezNeYs80OdKPqLhc4");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null) {
                Map<String, Object> res = new HashMap<>(body);

                // Nombres desde Decolecta
                String nombres = (String) body.getOrDefault("first_name", body.get("nombres"));

                // Apellidos desde Decolecta: first_last_name y second_last_name
                String paterno = (String) body.getOrDefault("first_last_name", "");
                String materno = (String) body.getOrDefault("second_last_name", "");

                if (paterno == null) paterno = "";
                if (materno == null) materno = "";

                String apellidos = (paterno + " " + materno).trim();

                // Mapear a todas las variantes que suele consumir Angular
                res.put("nombres", nombres);
                res.put("apellidoPaterno", paterno);
                res.put("apellidoMaterno", materno);
                res.put("apellidos", apellidos);

                return ResponseEntity.ok(res);
            }

            return ResponseEntity.ok(body);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "DNI no encontrado o error en el servicio"));
        }
    }
}