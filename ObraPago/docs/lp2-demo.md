# LP2 - Producto de Unidad 1: ObraPago

## Producto

Backend REST modular ensamblado como una sola aplicación Spring Boot, conectado a Oracle, con persistencia ORM/JPA y los **seis módulos funcionales del brief**: Obreros, Asistencia, Adelantos, Pagos, Mensajes y Respaldo.

El sistema permite registrar trabajadores, controlar jornadas y horas extra, registrar vales, generar planillas de pago automáticamente, producir avisos de pago y respaldar liquidaciones en archivos CSV descargables.

## 1. Alcance arquitectónico

```text
backend/
└── src/main/java/pe/edu/upeu/obrapago/
    ├── ObrapagoApplication.java
    ├── common/
    ├── config/
    ├── obreros/
    ├── asistencia/
    ├── adelantos/
    ├── pagos/
    ├── mensajes/
    └── respaldo/
```

Existe un solo ejecutable Spring Boot y un solo datasource Oracle. Los módulos mantienen sus responsabilidades separadas y se coordinan mediante servicios.

## 2. Demo ejecutable

- Health: `GET /health`
- Actuator: `GET /actuator/health`
- Swagger UI: `/swagger-ui.html`
- Colección Postman: `ObraPago.postman_collection.json`

## 3. Contrato REST principal

| Módulo | Método | Endpoint | Propósito |
|---|---|---|---|
| Obreros | POST | `/api/v1/obreros` | Registrar trabajador |
| Obreros | GET | `/api/v1/obreros` | Listar trabajadores |
| Asistencia | POST | `/api/v1/asistencias` | Registrar jornada cabecera-detalle |
| Asistencia | GET | `/api/v1/asistencias/resumen` | Resumir asistencias |
| Adelantos | POST | `/api/v1/adelantos` | Registrar vale/adelanto |
| Adelantos | PATCH | `/api/v1/adelantos/{id}/anular` | Anular adelanto |
| Pagos | POST | `/api/v1/pagos/procesar` | Generar planilla automática |
| Pagos | PATCH | `/api/v1/pagos/{id}/pagar` | Marcar planilla pagada |
| Mensajes | POST | `/api/v1/mensajes/planillas/{id}/enviar` | Generar/enviar avisos de pago |
| Respaldo | POST | `/api/v1/respaldos/planillas/{id}` | Generar respaldo CSV |
| Respaldo | GET | `/api/v1/respaldos/{id}/descargar` | Descargar respaldo |

## 4. Flujo funcional completo

```text
OBREROS
   ↓
ASISTENCIA
   ↓
ADELANTOS
   ↓
PAGOS
   ↓
MENSAJES
   ↓
RESPALDO
```

Pagos integra información persistida por los módulos anteriores. La fórmula aplicada es:

```text
TarifaHora = TarifaJornal / 8
Bruto = (DíasLaborados × TarifaJornal) + (HorasExtra × TarifaHora)
Neto = Bruto - Adelantos
```

## 5. Persistencia principal

Tablas creadas/actualizadas por Hibernate:

- `OBREROS`
- `JORNADAS_ASISTENCIA`
- `DETALLES_ASISTENCIA`
- `ADELANTOS`
- `PLANILLAS_PAGO`
- `DETALLES_PAGO`
- `MENSAJES_PAGO`
- `RESPALDOS`

## 6. Casos de prueba demostrables

| Caso | Resultado esperado |
|---|---|
| CRUD Obreros | Datos persistidos en Oracle |
| Jornada válida | Cabecera y detalles guardados |
| Jornada con obrero inexistente | Rollback completo |
| Adelanto | Importe asociado al trabajador y periodo |
| Generar pago | Calcula bruto, adelantos y neto automáticamente |
| Mensajes | Registra aviso en modo SIMULADO o lo envía por webhook |
| Respaldo | Genera CSV y permite descargarlo |

## 7. Integraciones

- Oracle se ejecuta en Docker sobre `FREEPDB1`.
- Mensajes utiliza un adaptador HTTP configurable. En exposición puede utilizarse `SIMULADO` para no depender de credenciales externas.
- Respaldo genera un archivo CSV real y conserva metadata de auditoría en Oracle.

## 8. Arquitectura

ObraPago se mantiene como **monolito modular**: una sola JVM, un ejecutable y un datasource. La separación por módulos permite evolucionar después hacia componentes distribuidos sin mezclar las responsabilidades actuales.
