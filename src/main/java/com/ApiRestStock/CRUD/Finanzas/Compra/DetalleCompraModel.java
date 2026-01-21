package com.ApiRestStock.CRUD.Finanzas.Compra;

import java.math.BigDecimal;

import com.ApiRestStock.CRUD.stock.ProductModel;
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
    name = "detalle_compra",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_detalle_compra_compra_producto",
            columnNames = {"compra_id", "producto_id"}
        )
    }
)
public class DetalleCompraModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalle_compra_id")
    private Long id;

    @JsonBackReference
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id", nullable = false)
    private CompraModel compra;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductModel producto;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    // SNAPSHOT
    @Column(name = "nombre_producto", nullable = false, length = 255)
    private String nombreProducto;

    // --- getters/setters ---

    public Long getId() {
        return id;
    }

    public CompraModel getCompra() {
        return compra;
    }

    public void setCompra(CompraModel compra) {
        this.compra = compra;
    }

    public ProductModel getProducto() {
        return producto;
    }

    public void setProducto(ProductModel producto) {
        this.producto = producto;
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

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
}
