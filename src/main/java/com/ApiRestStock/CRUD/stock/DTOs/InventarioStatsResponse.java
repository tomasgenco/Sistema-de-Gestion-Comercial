package com.ApiRestStock.CRUD.stock.DTOs;

import java.math.BigDecimal;

public record InventarioStatsResponse(
    Long totalProductos,
    Long productosStockBajo,
    Long productosSinStock,
    BigDecimal valorTotalInventario
) {}
