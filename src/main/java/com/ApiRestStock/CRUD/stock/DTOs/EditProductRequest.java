package com.ApiRestStock.CRUD.stock.DTOs;

import java.math.BigDecimal;

import com.ApiRestStock.CRUD.stock.Enums.TipoVenta;

public record EditProductRequest(
    String nombre,
    BigDecimal precioVenta,
    BigDecimal precioCompra,
    TipoVenta tipoVenta
) {}
