package com.ApiRestStock.CRUD.Repositories;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ApiRestStock.CRUD.Enums.MetodoPago;
import com.ApiRestStock.CRUD.Models.VentaModel;

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

}
