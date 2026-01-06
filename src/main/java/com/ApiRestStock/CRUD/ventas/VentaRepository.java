package com.ApiRestStock.CRUD.ventas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ApiRestStock.CRUD.ventas.enums.MetodoPago;

@Repository
public interface VentaRepository extends JpaRepository<VentaModel, Long> {

     // Ventas entre fechas (para reportes diarios / mensuales)
    List<VentaModel> findByFechaHoraBetween(
            OffsetDateTime desde,
            OffsetDateTime hasta
    );

    // Ventas por método de pago
    List<VentaModel> findByMetodoPago(MetodoPago metodoPago);

    // Ventas mayores a un monto
    List<VentaModel> findByTotalGreaterThan(Double total);


    // Ventas menores a un monto
    List<VentaModel> findByTotalLessThan(Double total);
    
    /**
     * Suma el total de ventas de una fecha específica
     * filtrando por método de pago.
     *
     * Se usa fecha_hora::date porque la columna es timestamptz.
     */
    @Query("""
        SELECT COALESCE(SUM(v.total), 0)
        FROM VentaModel v
        WHERE CAST(v.fechaHora AS date) = :fecha
          AND v.metodoPago = :metodoPago
    """)
    BigDecimal sumTotalByFechaAndMetodoPago(
        @Param("fecha") LocalDate fecha,
        @Param("metodoPago") MetodoPago metodoPago
    );

}
