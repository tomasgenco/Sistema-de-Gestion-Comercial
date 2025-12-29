package com.ApiRestStock.CRUD.Finanzas.DTOs;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CierreCajaResponse {
    private Long id;
    private LocalDate fecha;

    private BigDecimal totalEfectivo;
    private BigDecimal totalMercadoPago;
    private BigDecimal totalCuentaDni;
    private BigDecimal totalTarjetaCredito;
    private BigDecimal totalTarjetaDebito;

    private BigDecimal totalVentas;

    private BigDecimal efectivoReal;
    private BigDecimal diferencia;

    private String observaciones;

    public CierreCajaResponse(LocalDate fecha1, BigDecimal totalEfectivo1, BigDecimal totalPorMetodoPago, BigDecimal totalPorMetodoPago1, BigDecimal totalPorMetodoPago2, BigDecimal totalPorMetodoPago3, BigDecimal totalVentas1, BigDecimal efectivoReal1, BigDecimal diferencia1, String observaciones1) {};
    
    public CierreCajaResponse() {}



    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public BigDecimal getTotalEfectivo() { return totalEfectivo; }
    public void setTotalEfectivo(BigDecimal totalEfectivo) { this.totalEfectivo = totalEfectivo; }

    public BigDecimal getTotalMercadoPago() { return totalMercadoPago; }
    public void setTotalMercadoPago(BigDecimal totalMercadoPago) { this.totalMercadoPago = totalMercadoPago; }

    public BigDecimal getTotalCuentaDni() { return totalCuentaDni; }
    public void setTotalCuentaDni(BigDecimal totalCuentaDni) { this.totalCuentaDni = totalCuentaDni; }

    public BigDecimal getTotalTarjetaCredito() { return totalTarjetaCredito; }
    public void setTotalTarjetaCredito(BigDecimal totalTarjetaCredito) { this.totalTarjetaCredito = totalTarjetaCredito; }

    public BigDecimal getTotalTarjetaDebito() { return totalTarjetaDebito; }
    public void setTotalTarjetaDebito(BigDecimal totalTarjetaDebito) { this.totalTarjetaDebito = totalTarjetaDebito; }

    public BigDecimal getTotalVentas() { return totalVentas; }
    public void setTotalVentas(BigDecimal totalVentas) { this.totalVentas = totalVentas; }

    public BigDecimal getEfectivoReal() { return efectivoReal; }
    public void setEfectivoReal(BigDecimal efectivoReal) { this.efectivoReal = efectivoReal; }

    public BigDecimal getDiferencia() { return diferencia; }
    public void setDiferencia(BigDecimal diferencia) { this.diferencia = diferencia; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
