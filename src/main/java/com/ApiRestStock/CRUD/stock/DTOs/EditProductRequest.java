package com.ApiRestStock.CRUD.stock.DTOs;

import java.math.BigDecimal;

public record EditProductRequest(
    String nombre,
    BigDecimal precio
) {}
