package com.ApiRestStock.CRUD.Finanzas.cierreMes;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CierreMesRepository extends JpaRepository<CierreMesModel, Long> {
    
    Optional<CierreMesModel> findByAnioAndMes(Integer anio, Integer mes);
    
    boolean existsByAnioAndMes(Integer anio, Integer mes);
    
    Page<CierreMesModel> findByAnioOrderByMesDesc(Integer anio, Pageable pageable);
    
    Page<CierreMesModel> findAllByOrderByAnioDescMesDesc(Pageable pageable);
}
