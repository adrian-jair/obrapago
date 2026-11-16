package pe.edu.upeu.obrapago.obreros.dto;

import pe.edu.upeu.obrapago.obreros.entity.Oficio;

import java.math.BigDecimal;

public record ObreroResponse(
        Long id,
        String dni,
        String nombres,
        String apellidos,
        Oficio oficio,
        BigDecimal tarifaJornal,
        String celular,
        Boolean activo,
        String fotoUrl
) {}