package com.ApiRestStock.CRUD.Finanzas.gasto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.ApiRestStock.CRUD.Finanzas.Compra.CompraModel;
import com.ApiRestStock.CRUD.Finanzas.enums.TipoGasto;

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
@Table(name = "gasto")
public class GastoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gasto_id")
    private Long id;

    @Column(nullable = false)
    private OffsetDateTime fecha;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo", columnDefinition = "tipo_gasto")
    private TipoGasto tipo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id", unique = true)
    private CompraModel compra;

    @Column(name = "nombre_proveedor", length= 255)
    private String nombreProveedor;

    public GastoModel() {}

    public GastoModel(CompraModel compra, OffsetDateTime fecha,  TipoGasto tipo, BigDecimal total) {
        this.compra = compra;
        this.fecha = fecha;
        this.tipo = tipo;
        this.total = total;
    }



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

    public TipoGasto getTipo() {
        return tipo;
    }

    public void setTipo(TipoGasto tipo) {
        this.tipo = tipo;
    }

    public CompraModel getCompra() {
        return compra;
    }

    public void setCompra(CompraModel compra) {
        this.compra = compra;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }
}
