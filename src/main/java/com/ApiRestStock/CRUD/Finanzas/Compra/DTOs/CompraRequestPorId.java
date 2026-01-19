package com.ApiRestStock.CRUD.Finanzas.Compra.DTOs;

import java.util.List;

import com.ApiRestStock.CRUD.Finanzas.ingreso.DTOs.ItemCompraRequest;
import com.ApiRestStock.CRUD.ventas.enums.MetodoPago;

public record CompraRequestPorId(
    MetodoPago metodoPago,
    Long proveedorId,
    List<ItemCompraRequest> items
) {}
