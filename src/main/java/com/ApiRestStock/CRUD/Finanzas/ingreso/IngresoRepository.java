package com.ApiRestStock.CRUD.Finanzas.ingreso;

import java.math.BigDecimal;
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

    // Sumar ingresos del día específico
    @Query("SELECT COALESCE(SUM(i.total), 0) FROM IngresoModel i WHERE CAST(i.fecha AS date) = :fecha")
    BigDecimal sumIngresosDelDia(LocalDate fecha);

    /**
     * Obtiene ingresos del día agrupados por método de pago
     * Retorna array de Object[] donde [0] = MetodoPago, [1] = BigDecimal (total)
     */
    @Query("""
        SELECT v.metodoPago, COALESCE(SUM(i.total), 0)
        FROM IngresoModel i
        JOIN i.venta v
        WHERE CAST(i.fecha AS date) = :fecha
        GROUP BY v.metodoPago
        ORDER BY v.metodoPago
    """)
    List<Object[]> findIngresosPorMetodoPagoDelDia(LocalDate fecha);

    // Sumar ingresos en EFECTIVO del día (desde ventas con método EFECTIVO)
    @Query("""
        SELECT COALESCE(SUM(i.total), 0)
        FROM IngresoModel i
        JOIN i.venta v
        WHERE CAST(i.fecha AS date) = :fecha
        AND v.metodoPago = com.ApiRestStock.CRUD.ventas.enums.MetodoPago.EFECTIVO
    """)
    BigDecimal sumIngresosEfectivoDelDia(LocalDate fecha);

}
