package com.ApiRestStock.CRUD.ventas.DTOs;

import java.math.BigDecimal;

import com.ApiRestStock.CRUD.stock.Enums.TipoVenta;

public record DetalleVentaResponse(
    Long id,
    BigDecimal cantidad,
    BigDecimal precioUnitario,
    String nombreProducto,
    Long productoId,
    TipoVenta tipoVenta
) {}
