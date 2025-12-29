package com.ApiRestStock.CRUD.ventas;

import java.math.BigDecimal;

import com.ApiRestStock.CRUD.Enums.EstadoFiado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fiado")
public class FiadoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fiado_id")
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFiado estado;

    @Column(nullable = false, length = 50)
    private String persona;

    // --- getters/setters ---

    public Long getId() {
        return id;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public EstadoFiado getEstado() {
        return estado;
    }

    public void setEstado(EstadoFiado estado) {
        this.estado = estado;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }
}
