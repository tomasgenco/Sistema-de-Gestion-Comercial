package com.ApiRestStock.CRUD.Finanzas;

import java.math.BigDecimal;

import com.ApiRestStock.CRUD.stock.ProductModel;

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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id", nullable = false)
    private CompraModel compra;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductModel producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

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

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
