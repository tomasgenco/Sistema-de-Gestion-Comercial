package com.ApiRestStock.CRUD.Finanzas.DTOs;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CerrarCajaRequest {

    /**
     * Si es null, se asume hoy (LocalDate.now()) en el Service.
     */
    private LocalDate fecha;

    
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal efectivoReal;

    private String observaciones;

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getEfectivoReal() {
        return efectivoReal;
    }

    public void setEfectivoReal(BigDecimal efectivoReal) {
        this.efectivoReal = efectivoReal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
