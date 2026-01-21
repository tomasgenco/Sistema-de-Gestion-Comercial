package com.ApiRestStock.CRUD.ventas;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "detalle_venta",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_detalle_venta_venta_producto",
            columnNames = {"venta_id", "nombre_producto"}
        )
    }
)
public class DetalleVentaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalle_venta_id")
    private Long id;


    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "nombre_producto", nullable = false, length = 255)
    private String nombreProducto;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    private VentaModel venta;

    public Long getId() {
        return id;
    }


    public VentaModel getVenta() {
        return venta;
    }
    
    public void setVenta(VentaModel venta) {
        this.venta = venta;
    }
    

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario( BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

}

