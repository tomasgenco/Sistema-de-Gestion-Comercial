package com.ApiRestStock.CRUD.Finanzas.Compra.DTOs;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

public record CompraResponse(
        Long id,
        Instant fechaHora,
        String metodoPago,
        String nombreProveedor,
        BigDecimal total,
        List<DetalleCompraResponse> detalles
    
        
) {

    public CompraResponse(Long id2, OffsetDateTime fechaHora2, String name, String nombre, BigDecimal total2,
            List<DetalleCompraResponse> list) {
        this(id2, fechaHora2.toInstant(), name, nombre, total2, list);
    }}
