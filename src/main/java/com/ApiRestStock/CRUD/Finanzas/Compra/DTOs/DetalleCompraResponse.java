package com.ApiRestStock.CRUD.Finanzas.Compra.DTOs;

import java.math.BigDecimal;

public record DetalleCompraResponse(
        Long id,
        Integer cantidad,
        BigDecimal precioUnitario,
        String nombreProducto,
        Long productoId    // solo el id, no todo el Producto
) {}
