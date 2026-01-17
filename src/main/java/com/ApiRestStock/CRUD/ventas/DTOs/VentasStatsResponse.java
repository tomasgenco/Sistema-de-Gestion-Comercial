package com.ApiRestStock.CRUD.ventas.DTOs;

import java.math.BigDecimal;

public record VentasStatsResponse(
    Long ventasDelMes,
    Long ventasDelDia,
    BigDecimal ingresosTotalesDelDia,
    BigDecimal egresosDelDia
) {}
