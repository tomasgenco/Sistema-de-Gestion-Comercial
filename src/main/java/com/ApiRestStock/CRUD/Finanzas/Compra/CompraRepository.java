package com.ApiRestStock.CRUD.Finanzas.Compra;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CompraRepository extends JpaRepository<CompraModel, Long> {
    List<CompraModel> findByProveedorNombre(String proveedor);
}
