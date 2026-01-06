package com.ApiRestStock.CRUD.Finanzas.DTOs;

import java.math.BigDecimal;

import com.ApiRestStock.CRUD.Finanzas.enums.TipoGasto;

public class GastoRequest {


    private BigDecimal total;

    private TipoGasto tipo;

    private String nombreProveedor;


    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public TipoGasto getTipo() {
        return tipo;
    }

    public void setTipo(TipoGasto tipo) {
        this.tipo = tipo;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    
}
