package pe.edu.upeu.obrapago.contratos.dto;

import pe.edu.upeu.obrapago.contratos.entity.EstadoContrato;
import pe.edu.upeu.obrapago.contratos.entity.TipoContrato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContratoResponse(
        Long id,
        Long obreroId,
        String obreroDni,
        String obreroNombreCompleto,
        String codigoContrato,
        TipoContrato tipoContrato,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        BigDecimal tarifaPactada,
        String puesto,
        EstadoContrato estado,
        String observaciones,
        LocalDateTime creadoEn
) {}
