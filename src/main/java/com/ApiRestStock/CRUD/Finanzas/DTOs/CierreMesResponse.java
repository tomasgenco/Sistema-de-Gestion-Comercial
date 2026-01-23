package com.ApiRestStock.CRUD.Finanzas.DTOs;

import java.math.BigDecimal;
import java.util.List;

public class CierreMesResponse {
    private Long id;
    private Integer anio;
    private Integer mes;
    
    private BigDecimal totalEfectivo;
    private BigDecimal totalMercadoPago;
    private BigDecimal totalCuentaDni;
    private BigDecimal totalTarjetaCredito;
    private BigDecimal totalTarjetaDebito;
    
    private BigDecimal ingresosTotal;
    private BigDecimal egresosTotal;
    private BigDecimal resultadoTotal;
    
    private List<CierreCajaResponse> cierreCajas;

    public CierreMesResponse() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public BigDecimal getTotalEfectivo() {
        return totalEfectivo;
    }

    public void setTotalEfectivo(BigDecimal totalEfectivo) {
        this.totalEfectivo = totalEfectivo;
    }

    public BigDecimal getTotalMercadoPago() {
        return totalMercadoPago;
    }

    public void setTotalMercadoPago(BigDecimal totalMercadoPago) {
        this.totalMercadoPago = totalMercadoPago;
    }

    public BigDecimal getTotalCuentaDni() {
        return totalCuentaDni;
    }

    public void setTotalCuentaDni(BigDecimal totalCuentaDni) {
        this.totalCuentaDni = totalCuentaDni;
    }

    public BigDecimal getTotalTarjetaCredito() {
        return totalTarjetaCredito;
    }

    public void setTotalTarjetaCredito(BigDecimal totalTarjetaCredito) {
        this.totalTarjetaCredito = totalTarjetaCredito;
    }

    public BigDecimal getTotalTarjetaDebito() {
        return totalTarjetaDebito;
    }

    public void setTotalTarjetaDebito(BigDecimal totalTarjetaDebito) {
        this.totalTarjetaDebito = totalTarjetaDebito;
    }

    public BigDecimal getIngresosTotal() {
        return ingresosTotal;
    }

    public void setIngresosTotal(BigDecimal ingresosTotal) {
        this.ingresosTotal = ingresosTotal;
    }

    public BigDecimal getEgresosTotal() {
        return egresosTotal;
    }

    public void setEgresosTotal(BigDecimal egresosTotal) {
        this.egresosTotal = egresosTotal;
    }

    public BigDecimal getResultadoTotal() {
        return resultadoTotal;
    }

    public void setResultadoTotal(BigDecimal resultadoTotal) {
        this.resultadoTotal = resultadoTotal;
    }

    public List<CierreCajaResponse> getCierreCajas() {
        return cierreCajas;
    }

    public void setCierreCajas(List<CierreCajaResponse> cierreCajas) {
        this.cierreCajas = cierreCajas;
    }
}
