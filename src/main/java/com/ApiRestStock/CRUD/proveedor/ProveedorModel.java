package com.ApiRestStock.CRUD.proveedor;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "proveedor",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_proveedor_cuit", columnNames = "cuit")
    }
)
public class ProveedorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proveedor_id")
    private Long id;

    @Column(length = 20, unique = true)
    private String cuit;

    @Column(nullable = false)
    private Boolean activo;

    @Column(nullable= false, length= 255)
    private String nombre;

    // --- getters/setters ---

    public Long getId() {
        return id;
    }

    

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre.toLowerCase();
    }

}
