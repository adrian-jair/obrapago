# 🏗️ ObraPago — Sistema Integral de Control de Jornales, Asistencia y Pagos

Proyecto de programación empresarial desarrollado con **Spring Boot 3 (Java 21)**, **Angular 22**, base de datos **Oracle 23c Free**, seguridad **Keycloak** y contenedorización completa con **Docker Compose**.

---

## 📌 Los 10 Módulos Implementados

| N° | Módulo | Descripción Funcional | Ruta Frontend | Endpoint REST Principal |
|---|---|---|---|---|
| **1** | **OBREROS** | Padrón de trabajadores de obra, oficios (Peón, Oficial, Operario, Maestro), jornales diarios, celulares, fotografías y consulta en tiempo real con RENIEC. | `/obreros` | `/api/v1/obreros` |
| **2** | **USUARIO** | Gestión de usuarios del sistema, asignación de roles de acceso (`ADMIN`, `SUPERVISOR`, `PLANILLERO`, `CONSULTA`), estado activo/inactivo y registro de último ingreso. | `/usuarios` | `/api/v1/usuarios` |
| **3** | **CONTRATO** | Contratos formales de trabajo, modalidad (Por Obra, Plazo Fijo, Por Jornal, Indefinido), tarifas pactadas, fechas de vigencia y cargos. | `/contratos` | `/api/v1/contratos` |
| **4** | **JORNADAS_ASISTENCIA** | Registro diario de jornales de obra (apertura de jornada, marcado ágil masivo de presentes/tardanzas/faltas, horas extras y cierre definitivo de jornada). | `/asistencias` | `/api/v1/asistencias` |
| **5** | **PLANILLA_MENSUAL** | Liquidación y procesamiento automatizado de la nómina mensual aplicando fórmula: $\text{Bruto} = (\text{Días} \times \text{Tarifa}) + (\text{Horas Extra} \times \text{Tarifa Hora}) - \text{Tardanzas} - \text{Adelantos}$. | `/pagos` | `/api/v1/pagos/procesar` |
| **6** | **DETALLES_ASISTENCIA** | Explorador detallado de asistencias individuales con filtros avanzados por fecha, obrero o estado, y edición de justificaciones de inasistencia médica. | `/detalles-asistencia` | `/api/v1/asistencias/detalles` |
| **7** | **ADELANTOS** | Vales de caja chica y entregas a cuenta solicitadas por los obreros con fecha, concepto, control de estados y anulación lógica. | `/adelantos` | `/api/v1/adelantos` |
| **8** | **DETALLES_PAGO** | Nómina desglosada por trabajador en cada planilla y generación/impresión de la **Boleta de Pago de Jornal** individual. | `/detalles-pago` | `/api/v1/pagos/{id}/detalles` |
| **9** | **ACCESO** | Auditoría y trazabilidad de seguridad: log de inicios de sesión, intentos fallidos, acciones realizadas, roles e IPs de conexión. | `/accesos` | `/api/v1/accesos` |
| **10** | **CONSULTA_OBRERO_PAGO** | Portal de autoservicio o consulta del trabajador por DNI: visualización consolidada de su contrato, estadísticas de asistencia, vales y pagos recibidos. | `/consulta-obrero` | `/api/v1/consultas/obrero/{dni}` |

---

## 🛡️ Seguridad Frontend: Keycloak y Rutas Protegidas

1. **Autenticación OIDC / PKCE**:
   - Integración oficial con `keycloak-js`.
   - Realm configurado: `obrapago-realm`.
   - Cliente público: `obrapago-front`.
2. **Rutas Protegidas (`authGuard`)**:
   - Todas las pantallas del sistema están protegidas mediante el Guard funcional en Angular. Si no hay sesión activa, redirige al Login de Keycloak.
   - Rutas con control de roles (por ejemplo, el módulo de **Usuarios** restringido a rol `ADMIN`, auditoría de **Accesos** restringida a `ADMIN` y `SUPERVISOR`).
3. **Interceptor HTTP (`authInterceptor`)**:
   - Inyecta automáticamente el token JWT Bearer (`Authorization: Bearer <token>`) en cada petición HTTP hacia el backend.
4. **Modo Desarrollo / Fallback**:
   - Si se levanta el proyecto localmente sin iniciar el contenedor de Keycloak, el frontend detecta la situación y activa el modo desarrollo seguro con perfil de administrador para permitir demostraciones y pruebas fluidas sin bloqueos.

### Usuarios Preconfigurados en Keycloak

| Usuario | Contraseña | Roles Asignados | Propósito |
|---|---|---|---|
| `admin` | `admin` | `ADMIN`, `SUPERVISOR`, `PLANILLERO` | Control total del sistema y auditoría |
| `supervisor` | `supervisor` | `SUPERVISOR` | Asistencias diarias y contratos |
| `planillero` | `planillero` | `PLANILLERO` | Generación y liquidación de planillas |
| `obrero` | `obrero` | `CONSULTA` | Consulta de boletas y pagos |

---

## 🐳 Ejecución con Docker (Recomendado)

En la raíz del proyecto (`d:\lp2g2`), ejecuta:

```bash
docker compose up -d
```

Esto iniciará de forma automática y orquestada:
1. **Oracle 23c Free** en puerto `1521` (Base de datos persistente).
2. **Keycloak 26** en puerto `8081` (Importando automáticamente el realm `obrapago-realm`).
3. **Backend Spring Boot** en puerto `8080`.
4. **Frontend Angular + Nginx** en puerto `4200`.

Para detener los contenedores:
```bash
docker compose down
```

---

## 💻 Ejecución Local (Sin Docker)

### 1. Iniciar Base de Datos Oracle
```bash
docker start obrapago-oracle
```

### 2. Iniciar Backend (Spring Boot)
```bash
cd d:\lp2g2\ObraPago
mvn clean compile
mvn spring-boot:run
```
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Healthcheck: `http://localhost:8080/actuator/health`

### 3. Iniciar Frontend (Angular)
```bash
cd d:\lp2g2\obrapago-front
npm start
```
- Aplicación: `http://localhost:4200`
