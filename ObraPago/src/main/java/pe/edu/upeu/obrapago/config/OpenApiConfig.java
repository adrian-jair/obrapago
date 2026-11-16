package pe.edu.upeu.obrapago.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI obraPagoOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("ObraPago API")
                .version("v1")
                .description("API REST de ObraPago con los 6 módulos: Obreros, Asistencia, Adelantos, Pagos, Mensajes y Respaldo."));
    }
}
