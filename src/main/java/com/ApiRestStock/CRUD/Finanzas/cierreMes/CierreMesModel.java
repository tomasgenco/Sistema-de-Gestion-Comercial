package com.ApiRestStock.CRUD.Finanzas.cierreMes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ApiRestStock.CRUD.Finanzas.cierreCaja.CierreCajaModel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "cierre_mes",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_cierre_mes_anio_mes",
            columnNames = {"anio", "mes"}
        )
    }
)
public class CierreMesModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Column(nullable = false)
    private Integer mes;

    // Totales por método de pago del mes
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

    @Column(name = "ingresos_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal ingresosTotal = BigDecimal.ZERO;

    @Column(name = "egresos_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal egresosTotal = BigDecimal.ZERO;

    @Column(name = "resultado_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal resultadoTotal = BigDecimal.ZERO;

    @OneToMany(mappedBy = "cierreMes", cascade = CascadeType.ALL)
    private List<CierreCajaModel> cierreCajas = new ArrayList<>();

    /* ======================
       Getters & Setters
       ====================== */

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

    public List<CierreCajaModel> getCierreCajas() {
        return cierreCajas;
    }

    public void setCierreCajas(List<CierreCajaModel> cierreCajas) {
        this.cierreCajas = cierreCajas;
    }
}
