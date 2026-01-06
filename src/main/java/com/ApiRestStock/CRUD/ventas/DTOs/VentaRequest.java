package com.ApiRestStock.CRUD.ventas.DTOs;

import java.util.List;

import com.ApiRestStock.CRUD.ventas.enums.MetodoPago;

public record VentaRequest(
    MetodoPago metodoPago,
    List<ItemVentaRequest> items
) {}
