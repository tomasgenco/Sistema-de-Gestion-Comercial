package com.ApiRestStock.CRUD.proveedor.DTOs;

import java.math.BigDecimal;

public record ProveedorStatsResponse(
    Long totalProveedores,
    Long proveedoresActivos,
    BigDecimal totalGastado
) {
}
