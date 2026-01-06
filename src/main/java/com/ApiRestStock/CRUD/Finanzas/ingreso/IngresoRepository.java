package com.ApiRestStock.CRUD.Finanzas.ingreso;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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

}
