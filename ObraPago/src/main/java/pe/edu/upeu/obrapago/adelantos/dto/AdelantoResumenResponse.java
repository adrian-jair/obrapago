package pe.edu.upeu.obrapago.adelantos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AdelantoResumenResponse(LocalDate desde, LocalDate hasta, Long obreroId, long cantidad, BigDecimal total) {}
