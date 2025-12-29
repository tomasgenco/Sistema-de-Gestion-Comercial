package com.ApiRestStock.CRUD.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ApiRestStock.CRUD.Finanzas.CompraModel;

@Repository
public interface CompraRepository extends JpaRepository<CompraModel, Long> {

}
