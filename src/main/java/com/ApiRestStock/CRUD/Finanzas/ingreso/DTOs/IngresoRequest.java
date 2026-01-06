package com.ApiRestStock.CRUD.Finanzas.ingreso.DTOs;

import java.math.BigDecimal;

import com.ApiRestStock.CRUD.Finanzas.enums.TipoIngreso;
import com.ApiRestStock.CRUD.ventas.VentaModel;
import com.ApiRestStock.CRUD.ventas.fiado.FiadoModel;

public record IngresoRequest(
    BigDecimal total,
    TipoIngreso tipo,
    VentaModel venta,
    FiadoModel fiado
) {}
