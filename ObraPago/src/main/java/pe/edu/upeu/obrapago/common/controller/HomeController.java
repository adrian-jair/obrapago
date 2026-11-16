package pe.edu.upeu.obrapago.common.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String index() {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>ObraPago - Servidor Backend API</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
                <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
                <style>
                    body {
                        background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
                        color: #f8fafc;
                        min-height: 100vh;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-family: system-ui, -apple-system, sans-serif;
                    }
                    .card-status {
                        background: rgba(30, 41, 59, 0.85);
                        backdrop-filter: blur(10px);
                        border: 1px solid rgba(255, 255, 255, 0.1);
                        border-radius: 1rem;
                        max-width: 620px;
                        width: 100%;
                    }
                </style>
            </head>
            <body>
                <div class="container p-4">
                    <div class="card card-status mx-auto p-4 p-md-5 shadow-lg">
                        <div class="text-center mb-4">
                            <div style="font-size: 3rem; color: #38bdf8;"><i class="bi bi-building-gear"></i></div>
                            <h2 class="fw-bold mt-2 text-white">ObraPago Backend API</h2>
                            <p class="text-secondary small">Servidor REST API (Spring Boot 3 + Oracle 23c Free)</p>
                            <span class="badge bg-success px-3 py-2 fs-6"><i class="bi bi-check-circle me-1"></i>Servidor Operativo en Puerto 8080</span>
                        </div>

                        <div class="alert alert-info bg-opacity-10 border-info text-info small mb-4">
                            <i class="bi bi-info-circle me-1"></i><strong>Información importante:</strong> Este puerto (<strong>8080</strong>) corresponde al <em>Backend de Datos (API REST)</em>. La interfaz visual de usuario del sistema se encuentra en el <strong>puerto 4200</strong>.
                        </div>

                        <div class="d-grid gap-3">
                            <a href="http://localhost:4200" class="btn btn-primary btn-lg fw-bold shadow-sm">
                                <i class="bi bi-box-arrow-up-right me-2"></i>Abrir Sistema Web (Frontend - Puerto 4200)
                            </a>
                            <a href="/swagger-ui/index.html" class="btn btn-outline-light fw-bold">
                                <i class="bi bi-journal-code me-2"></i>Ver Documentación Swagger (OpenAPI)
                            </a>
                            <a href="http://localhost:8081" class="btn btn-outline-warning fw-bold">
                                <i class="bi bi-shield-lock me-2"></i>Consola de Keycloak (Puerto 8081)
                            </a>
                        </div>

                        <div class="text-center mt-4 pt-3 border-top border-secondary text-secondary small">
                            ObraPago &copy; 2026 - Control de Jornales, Asistencia y Pagos
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}
