package com.ApiRestStock.CRUD.stock.DTOs;

import java.math.BigDecimal;

import com.ApiRestStock.CRUD.stock.Enums.TipoVenta;

public record CreateProductRequest(
    String nombre,
    String sku,
    BigDecimal precioVenta,
    BigDecimal precioCompra,
    BigDecimal stock,
    TipoVenta tipoVenta
) {}
