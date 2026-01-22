package com.ApiRestStock.CRUD.Finanzas.Compra.DTOs;

import java.math.BigDecimal;

import com.ApiRestStock.CRUD.stock.Enums.TipoVenta;

public record DetalleCompraResponse(
        Long id,
        BigDecimal cantidad,
        BigDecimal precioUnitario,
        String nombreProducto,
        Long productoId,
        TipoVenta tipoVenta
) {}
