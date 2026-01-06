package com.ApiRestStock.CRUD.Finanzas.ingreso;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.ApiRestStock.CRUD.Finanzas.enums.TipoIngreso;
import com.ApiRestStock.CRUD.ventas.VentaModel;
import com.ApiRestStock.CRUD.ventas.fiado.FiadoModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingreso")
public class IngresoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingreso_id")
    private Long id;

    @Column(nullable = false)
    private OffsetDateTime fecha;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 50)
    private TipoIngreso tipo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", unique = true)
    private VentaModel venta;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fiado_id", unique = true)
    private FiadoModel fiado;

    // --- getters/setters ---

    public Long getId() {
        return id;
    }

    public OffsetDateTime getFecha() {
        return fecha;
    }

    public void setFecha(OffsetDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public TipoIngreso getTipo() {
        return tipo;
    }

    public void setTipo(TipoIngreso tipo) {
        this.tipo = tipo;
    }

    public VentaModel getVenta() {
        return venta;
    }

    public void setVenta(VentaModel venta) {
        this.venta = venta;
    }

    public FiadoModel getFiado() {
        return fiado;
    }

    public void setFiado(FiadoModel fiado) {
        this.fiado = fiado;
    }
}
