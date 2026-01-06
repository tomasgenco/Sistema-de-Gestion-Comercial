package com.ApiRestStock.CRUD.ventas;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ApiRestStock.CRUD.ventas.enums.MetodoPago;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "venta")
public class VentaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venta_id")
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private OffsetDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = true)
    private MetodoPago metodoPago;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    /* 
       Relación con detalle_venta
       Una venta tiene muchos detalles
    */
    @JsonManagedReference
    @OneToMany(
        mappedBy = "venta",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )

    private List<DetalleVentaModel> detalles = new ArrayList<>();

    // --- getters/setters ---

    public Long getId() {
        return id;
    }

    public OffsetDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(OffsetDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<DetalleVentaModel> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaModel> detalles) {
        this.detalles = detalles;
    }


    public boolean agregarDetalle(DetalleVentaModel detalle) {
        this.detalles.add(detalle);
        return true;
    }
}

