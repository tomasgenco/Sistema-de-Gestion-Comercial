package com.ApiRestStock.CRUD.Finanzas.ingreso;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ApiRestStock.CRUD.Finanzas.enums.TipoIngreso;

@Repository
public interface IngresoRepository extends JpaRepository<IngresoModel, Long>{

    // Obtener ingresos por fecha
    List<IngresoModel> findByFecha(LocalDate fecha);

    // Obtener ingresos entre fechas
    List<IngresoModel> findByFechaBetween(LocalDate desde, LocalDate hasta);

    // Obtener ingresos por tipo
    List<IngresoModel> findByTipo(TipoIngreso tipo);

    // Sumar el total de ingresos (todos)
    @Query("SELECT COALESCE(SUM(i.total), 0) FROM IngresoModel i")
    Double sumTotalIngresos();

    // Sumar el total de ingresos entre dos instantes (por ejemplo, para los últimos N días)
    @Query("SELECT COALESCE(SUM(i.total), 0) FROM IngresoModel i WHERE i.fecha BETWEEN :desde AND :hasta")
    Double sumTotalIngresosBetween(OffsetDateTime desde, OffsetDateTime hasta);

}
