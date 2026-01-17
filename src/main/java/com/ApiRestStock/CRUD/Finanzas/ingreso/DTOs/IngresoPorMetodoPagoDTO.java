package com.ApiRestStock.CRUD.Finanzas.ingreso.DTOs;

import java.math.BigDecimal;

import com.ApiRestStock.CRUD.ventas.enums.MetodoPago;

public record IngresoPorMetodoPagoDTO(
    MetodoPago metodoPago,
    BigDecimal total
) {}
