package com.ApiRestStock.CRUD.ventas.DTOs;

import java.math.BigDecimal;

public record DetalleVentaResponse(
    Long id,
    String nombreProducto,
    Integer cantidad,
    BigDecimal precioUnitario
) {}
