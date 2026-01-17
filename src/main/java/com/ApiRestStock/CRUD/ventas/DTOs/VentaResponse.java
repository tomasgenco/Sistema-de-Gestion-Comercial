package com.ApiRestStock.CRUD.ventas.DTOs;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import com.ApiRestStock.CRUD.ventas.enums.MetodoPago;

public record VentaResponse(
    Long id,
    OffsetDateTime fechaHora,
    BigDecimal total,
    MetodoPago metodoPago,
    List<DetalleVentaResponse> detalles
) {}
