package com.ApiRestStock.CRUD.Finanzas.ingreso.DTOs;

import java.util.List;


import com.ApiRestStock.CRUD.ventas.enums.MetodoPago;

public record CompraRequest(
    MetodoPago metodoPago,
    String nombreProveedor,
    List<ItemCompraRequest> items
) {}
