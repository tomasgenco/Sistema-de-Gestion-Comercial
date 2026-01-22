package com.ApiRestStock.CRUD.ventas.DTOs;

import java.math.BigDecimal;

public record DetalleVentaResponse(
    Long id,
    BigDecimal cantidad,
    BigDecimal precioUnitario,
    String nombreProducto,
    Long productoId
) {}
