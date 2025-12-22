package com.ApiRestStock.CRUD.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ApiRestStock.CRUD.Models.ProductModel;

@Repository
public interface IProductRepository extends JpaRepository<ProductModel, Long>{

    Optional<ProductModel> findBySku(String sku);
    
}
