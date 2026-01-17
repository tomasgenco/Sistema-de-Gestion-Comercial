package com.ApiRestStock.CRUD.ventas.DTOs;

import java.math.BigDecimal;

/**
 * DTO para representar las ventas agrupadas por hora del día.
 * Diseñado para ser consumido por gráficos Rechart en el frontend.
 */
public record VentaPorHoraDTO(
    Integer hora,           // Hora del día (0-23)
    Long cantidadVentas,    // Número de ventas en esa hora
    BigDecimal totalVentas  // Suma total de ventas en esa hora
) {
}
