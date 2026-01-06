package com.ApiRestStock.CRUD.proveedor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<ProveedorModel, Long> {

    // Buscar proveedor por nombre
    Optional<ProveedorModel> findByNombre(String nombre);

    // Verificar si existe un proveedor por nombre
    boolean existsByNombre(String nombre);
}
