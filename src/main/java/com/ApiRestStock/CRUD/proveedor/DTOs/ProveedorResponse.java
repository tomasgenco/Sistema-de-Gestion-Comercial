package com.ApiRestStock.CRUD.proveedor.DTOs;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProveedorResponse(
    Long id,
    String cuit,
    Boolean activo,
    String nombreEmpresa,
    String personaContacto,
    String email,
    String telefono,
    String direccion,
    BigDecimal totalCompras,
    LocalDate ultimaCompra
) {
}
