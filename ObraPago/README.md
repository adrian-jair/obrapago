# ObraPago — Backend completo (6 módulos)

Sistema Spring Boot + Oracle para control de obreros, asistencia, adelantos, pagos, mensajes y respaldos.

## Módulos implementados

1. **Obreros**: CRUD de trabajadores, oficio, jornal y celular.
2. **Asistencia**: jornada cabecera-detalle, estados y horas extra, con rollback transaccional.
3. **Adelantos**: registro, consulta, resumen y anulación lógica de vales/adelantos.
4. **Pagos**: generación automática de planilla por periodo usando asistencias + horas extra - adelantos.
5. **Mensajes**: genera avisos de pago. Por defecto trabaja en modo `SIMULADO`; puede hacer POST a un webhook real configurando variables de entorno.
6. **Respaldo**: genera un CSV real de una planilla, registra el respaldo en Oracle y permite descargar el archivo.

## Arquitectura

Monolito modular, un solo ejecutable Spring Boot y un datasource Oracle:

```textW
pe.edu.upeu.obrapago
├── obreros
├── asistencia
├── adelantos
├── pagos
├── mensajes
├── respaldo
├── common
└── config
```

Cada módulo contiene `controller`, `dto`, `entity`, `repository` y `service` según corresponda.

## Oracle con Docker

El proyecto está configurado para el contenedor usado durante la preparación:

- Host: `localhost`
- Puerto: `1521`
- Servicio: `FREEPDB1`
- Usuario: `OBRAPAGO`
- Contraseña: `123456`

Si el contenedor ya existe:

```cmd
docker start obrapago-oracle
docker ps
```

Si hay que crearlo desde cero:

```cmd
docker run -d --name obrapago-oracle -p 1521:1521 -e ORACLE_PASSWORD=123456 -e APP_USER=OBRAPAGO -e APP_USER_PASSWORD=123456 -v obrapago-oracle-data:/opt/oracle/oradata gvenzl/oracle-free:23
```

## Ejecutar

```cmd
mvn clean compile
mvn spring-boot:run
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

Health:

```text
http://localhost:8080/health
http://localhost:8080/actuator/health
```

## Orden recomendado para demostrar los 6 módulos

### 1. Obreros

`POST /api/v1/obreros`

```json
{
  "dni": "74859632",
  "nombres": "Juan Enrique",
  "apellidos": "Estrada Murga",
  "oficio": "OPERARIO",
  "tarifaJornal": 90,
  "celular": "987654321"
}
```

Crear además 1 o 2 trabajadores más y luego usar `GET /api/v1/obreros`.

### 2. Asistencia

`POST /api/v1/asistencias`

```json
{
  "fecha": "2026-09-14",
  "detalles": [
    {"obreroId": 1, "estado": "PRESENTE", "horasExtra": 2, "observacion": "Jornada normal"},
    {"obreroId": 2, "estado": "TARDANZA", "horasExtra": 1, "observacion": "Llegó tarde"}
  ]
}
```

Para demostrar rollback, repetir en otra fecha incluyendo `obreroId: 99999`.

### 3. Adelantos

`POST /api/v1/adelantos`

```json
{
  "obreroId": 1,
  "fecha": "2026-09-14",
  "importe": 20,
  "concepto": "Vale de caja chica"
}
```

Consultar con `GET /api/v1/adelantos` y `GET /api/v1/adelantos/resumen`.

### 4. Pagos

`POST /api/v1/pagos/procesar`

```json
{
  "fechaInicio": "2026-09-14",
  "fechaFin": "2026-09-14"
}
```

Fórmula implementada:

```text
TarifaHora = TarifaJornal / 8
Bruto = (DíasLaborados × TarifaJornal) + (HorasExtra × TarifaHora)
Neto = Bruto - Adelantos
```

`PRESENTE` y `TARDANZA` cuentan como día laborado. Las faltas no cuentan como día laborado.

Luego usar `GET /api/v1/pagos` y `PATCH /api/v1/pagos/{id}/pagar`.

### 5. Mensajes

`POST /api/v1/mensajes/planillas/{planillaId}/enviar`

Por defecto genera y registra los avisos en estado `SIMULADO`, de forma que la demo no dependa de credenciales externas. Para conectar un proveedor HTTP real:

```text
OBRAPAGO_MSG_MODE=WEBHOOK
OBRAPAGO_MSG_WEBHOOK=https://tu-api.example/enviar
```

El backend enviará JSON:

```json
{"to":"987654321","message":"ObraPago: ..."}
```

### 6. Respaldo

`POST /api/v1/respaldos/planillas/{planillaId}`

Genera un CSV con el detalle de la planilla dentro de `./respaldos`, registra metadata en Oracle y devuelve una URL como:

```text
/api/v1/respaldos/1/descargar
```

También se puede usar `GET /api/v1/respaldos`.

## Endpoints principales

### Obreros
- `POST /api/v1/obreros`
- `GET /api/v1/obreros`
- `GET /api/v1/obreros/{id}`
- `PUT /api/v1/obreros/{id}`
- `DELETE /api/v1/obreros/{id}`

### Asistencia
- `POST /api/v1/asistencias`
- `GET /api/v1/asistencias`
- `GET /api/v1/asistencias/resumen`

### Adelantos
- `POST /api/v1/adelantos`
- `GET /api/v1/adelantos`
- `GET /api/v1/adelantos/resumen`
- `PATCH /api/v1/adelantos/{id}/anular`

### Pagos
- `POST /api/v1/pagos/procesar`
- `GET /api/v1/pagos`
- `GET /api/v1/pagos/{id}`
- `PATCH /api/v1/pagos/{id}/pagar`
- `PATCH /api/v1/pagos/{id}/anular`

### Mensajes
- `POST /api/v1/mensajes/planillas/{planillaId}/enviar`
- `GET /api/v1/mensajes`

### Respaldo
- `POST /api/v1/respaldos/planillas/{planillaId}`
- `GET /api/v1/respaldos`
- `GET /api/v1/respaldos/{id}/descargar`

## Notas para exposición

- Los 6 módulos se ejecutan dentro de una sola aplicación Spring Boot.
- Oracle corre en Docker y Spring se conecta por JDBC.
- La persistencia usa JPA/Hibernate.
- Asistencia usa `@Transactional` para rollback completo.
- Pagos no recibe un monto manual: lo calcula usando información persistida en Asistencia y Adelantos.
- Mensajes tiene un modo seguro de demo y un adaptador HTTP configurable.
- Respaldo genera un archivo real descargable.
