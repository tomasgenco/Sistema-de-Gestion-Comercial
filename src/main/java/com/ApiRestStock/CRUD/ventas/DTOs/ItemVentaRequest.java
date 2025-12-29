package com.ApiRestStock.CRUD.ventas.DTOs;

import java.math.BigDecimal;

public record ItemVentaRequest(
    String nombreProducto,
    Integer cantidad,
    BigDecimal precioUnitario
) {}



