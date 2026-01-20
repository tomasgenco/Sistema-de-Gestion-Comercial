package com.ApiRestStock.CRUD.stock.DTOs;

import java.math.BigDecimal;

public record CreateProductRequest(
    String nombre,
    String sku,
    BigDecimal precioVenta,
    BigDecimal precioCompra,
    Integer stock
) {}
