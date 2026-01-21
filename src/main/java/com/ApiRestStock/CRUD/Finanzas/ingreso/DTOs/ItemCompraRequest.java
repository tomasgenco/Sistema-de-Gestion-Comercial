package com.ApiRestStock.CRUD.Finanzas.ingreso.DTOs;

import java.math.BigDecimal;

public record ItemCompraRequest(
    BigDecimal cantidad,
    BigDecimal precioUnitario,
    String nombreProducto,
    String sku  // Código de barras del producto (opcional, requerido solo si el producto no existe en la DB)
) {}
