package com.ApiRestStock.CRUD.stock;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "producto",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_producto_nombre", columnNames = "nombre"),
        @UniqueConstraint(name = "uk_producto_sku", columnNames = "sku")
    }
)
public class ProductModel {



    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "precio_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @Column(nullable = false, length = 50)
    private String sku;

    @Column(nullable = false)
    private Integer stock;

    public Long getId() {
        return id;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public String getNombre(){
        return nombre;
    }

    public void setPrecioVenta(BigDecimal precioVenta){
        this.precioVenta = precioVenta;
    }

    public BigDecimal getPrecioVenta(){
        return precioVenta;
    }

    public void setPrecioCompra(BigDecimal precioCompra){
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioCompra(){
        return precioCompra;
    }

    public void setSku(String sku){
        this.sku = sku;
    }

    public String getSku(){
        return sku;
    }

    public void setStock(Integer stock){
        this.stock = stock;
    }

    public Integer getStock(){
        return stock;
    }

    


}
