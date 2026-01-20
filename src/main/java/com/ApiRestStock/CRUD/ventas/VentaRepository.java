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

    /**
     * Busca ventas por método de pago (búsqueda parcial case-insensitive)
     */
    @Query("SELECT v FROM VentaModel v WHERE LOWER(CAST(v.metodoPago AS string)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY v.fechaHora DESC")
    List<VentaModel> buscarPorMetodoPago(@Param("searchTerm") String searchTerm);

    /**
     * Busca ventas por fecha (YYYY-MM-DD)
     */
    @Query("SELECT v FROM VentaModel v WHERE CAST(v.fechaHora AS date) = :fecha ORDER BY v.fechaHora DESC")
    List<VentaModel> buscarPorFecha(@Param("fecha") LocalDate fecha);

    /**
     * Busca ventas por método de pago y fecha
     */
    @Query("SELECT v FROM VentaModel v WHERE LOWER(CAST(v.metodoPago AS string)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND CAST(v.fechaHora AS date) = :fecha ORDER BY v.fechaHora DESC")
    List<VentaModel> buscarPorMetodoPagoYFecha(@Param("searchTerm") String searchTerm, @Param("fecha") LocalDate fecha);

    /**
     * Cuenta la cantidad de ventas del mes actual.
     */
    @Query("""
        SELECT COUNT(v)
        FROM VentaModel v
        WHERE YEAR(v.fechaHora) = :anio
          AND MONTH(v.fechaHora) = :mes
    """)
    Long countVentasDelMes(
        @Param("anio") int anio,
        @Param("mes") int mes
    );

    /**
     * Cuenta todas las ventas de un día específico (extrae solo la fecha del OffsetDateTime)
    /**
     * Cuenta todas las ventas de un día específico (extrae solo la fecha del OffsetDateTime)
     */
    @Query(value = "SELECT COUNT(*) FROM venta WHERE CAST(fecha_hora AS DATE) = CAST(:fecha AS DATE)", nativeQuery = true)
    Long countVentasDelDia(@Param("fecha") LocalDate fecha);

    /**
     * Obtiene las últimas 5 ventas ordenadas por fecha descendente.
     */
    List<VentaModel> findTop5ByOrderByFechaHoraDesc();

    /**
     * Obtiene las ventas del día actual agrupadas por hora.
     * Retorna: hora (0-23), cantidad de ventas, total de ventas.
     */
    @Query(value = """
        SELECT EXTRACT(HOUR FROM fecha_hora) as hora,
               COUNT(venta_id) as cantidadVentas,
               COALESCE(SUM(total), 0) as totalVentas
        FROM venta
        WHERE DATE(fecha_hora) = :fecha
        GROUP BY EXTRACT(HOUR FROM fecha_hora)
        ORDER BY hora
    """, nativeQuery = true)
    List<Object[]> findVentasAgrupadasPorHora(@Param("fecha") LocalDate fecha);

}
