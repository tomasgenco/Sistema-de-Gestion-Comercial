package com.ApiRestStock.CRUD.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ApiRestStock.CRUD.Models.ProveedorModel;

@Repository
public interface ProveedorRepository extends JpaRepository<ProveedorModel, Long> {

    // Buscar proveedor por nombre
    Optional<ProveedorModel> findByNombre(String nombre);

    // Verificar si existe un proveedor por nombre
    boolean existsByNombre(String nombre);
}
