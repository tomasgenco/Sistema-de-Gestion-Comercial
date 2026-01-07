package com.ApiRestStock.CRUD.Finanzas.gasto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ApiRestStock.CRUD.Finanzas.enums.TipoGasto;

@Repository
public interface GastoRepository extends JpaRepository<GastoModel, Long> {

    // Obtener gastos por fecha
    List<GastoModel> findByFecha(LocalDate fecha);

    // Obtener gastos entre fechas
    List<GastoModel> findByFechaBetween(LocalDate desde, LocalDate hasta);

    // Obtener gastos por tipo
    List<GastoModel> findByTipo(TipoGasto tipo);

    @Query ("SELECT COALESCE(SUM(g.total), 0) FROM GastoModel g")
    public Double sumTotalGastos();

    @Query ("SELECT COALESCE(SUM(g.total), 0) FROM GastoModel g WHERE g.fecha BETWEEN :desde AND :hasta")
    public Double sumTotalGastosBetween(java.time.OffsetDateTime desde, java.time.OffsetDateTime hasta);

}
