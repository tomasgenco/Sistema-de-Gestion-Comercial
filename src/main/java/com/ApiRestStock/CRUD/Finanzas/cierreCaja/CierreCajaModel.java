package com.ApiRestStock.CRUD.Finanzas.cierreCaja;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ApiRestStock.CRUD.ventas.MetodoPago;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "cierre_caja",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_cierre_caja_fecha",
            columnNames = {"fecha"}
        )
    }
)
public class CierreCajaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cierre_caja_id")
    private Long id;

    // Un solo cierre por día
    @Column(nullable = false)
    private LocalDate fecha;

    // Totales por método de pago
    @Column(name = "total_efectivo", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalEfectivo = BigDecimal.ZERO;

    @Column(name = "total_mercado_pago", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalMercadoPago = BigDecimal.ZERO;

    @Column(name = "total_cuenta_dni", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCuentaDni = BigDecimal.ZERO;

    @Column(name = "total_tarjeta_credito", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalTarjetaCredito = BigDecimal.ZERO;

    @Column(name = "total_tarjeta_debito", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalTarjetaDebito = BigDecimal.ZERO;

    // Total general del día
    @Column(name = "total_ventas", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalVentas = BigDecimal.ZERO;

    // Control de efectivo
    @Column(name = "efectivo_real", nullable = false, precision = 10, scale = 2)
    private BigDecimal efectivoReal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal diferencia = BigDecimal.ZERO;

    // Observaciones del cierre
    @Column(columnDefinition = "TEXT")
    private String observaciones;

    /* ======================
       Getters & Setters
       ====================== */

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotalEfectivo() {
        return totalEfectivo;
    }

    public void setTotalEfectivo(BigDecimal totalEfectivo) {
        this.totalEfectivo = totalEfectivo;
    }

    public BigDecimal getTotalPorMetodoPago(MetodoPago metodoPago) {
    switch (metodoPago) {
        case EFECTIVO -> {
            return totalEfectivo;
            }

        case MERCADO_PAGO -> {
            return totalMercadoPago;
            }

        case CUENTA_DNI -> {
            return totalCuentaDni;
            }

        case TARJETA_CREDITO -> {
            return totalTarjetaCredito;
            }

        case TARJETA_DEBITO -> {
            return totalTarjetaDebito;
            }

        default -> throw new IllegalArgumentException("Método de pago no soportado: " + metodoPago);
    }
}


    public void setTotalPorMetodoPago(MetodoPago metodoPago, BigDecimal total) {
    if (total == null) {
        total = BigDecimal.ZERO;
    }

    switch (metodoPago) {
        case EFECTIVO:
            this.totalEfectivo = total;
            break;

        case MERCADO_PAGO:
            this.totalMercadoPago = total;
            break;

        case CUENTA_DNI:
            this.totalCuentaDni = total;
            break;

        case TARJETA_CREDITO:
            this.totalTarjetaCredito = total;
            break;

        case TARJETA_DEBITO:
            this.totalTarjetaDebito = total;
            break;

        default:
            throw new IllegalArgumentException("Método de pago no soportado: " + metodoPago);
    }
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

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }

    public BigDecimal getEfectivoReal() {
        return efectivoReal;
    }

    public void setEfectivoReal(BigDecimal efectivoReal) {
        this.efectivoReal = efectivoReal;
    }

    public BigDecimal getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(BigDecimal diferencia) {
        this.diferencia = diferencia;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

}
