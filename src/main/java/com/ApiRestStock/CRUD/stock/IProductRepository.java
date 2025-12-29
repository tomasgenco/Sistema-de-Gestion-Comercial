package com.ApiRestStock.CRUD.stock;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductRepository extends JpaRepository<ProductModel, Long>{

    Optional<ProductModel> findBySku(String sku);

    Optional<ProductModel> findByNombre(String nombre);
    
}
