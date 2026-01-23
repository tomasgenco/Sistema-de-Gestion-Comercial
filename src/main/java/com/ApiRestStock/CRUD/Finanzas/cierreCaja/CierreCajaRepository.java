package com.ApiRestStock.CRUD.Finanzas.cierreCaja;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CierreCajaRepository extends JpaRepository<CierreCajaModel, Long> {

    // Para validar que no exista más de un cierre por día (UNIQUE fecha)
    Optional<CierreCajaModel> findByFecha(LocalDate fecha);
    List<CierreCajaModel> findByFechaBetweenOrderByFechaAsc(LocalDate desde, LocalDate hasta);
    
    // Paginación por rango de fechas
    Page<CierreCajaModel> findByFechaBetween(LocalDate desde, LocalDate hasta, Pageable pageable);

    boolean existsByFecha(LocalDate fecha);
}
