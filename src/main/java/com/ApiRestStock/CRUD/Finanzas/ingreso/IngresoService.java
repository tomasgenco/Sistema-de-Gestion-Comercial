package com.ApiRestStock.CRUD.Finanzas.ingreso;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ApiRestStock.CRUD.Finanzas.enums.TipoIngreso;
import com.ApiRestStock.CRUD.Finanzas.ingreso.DTOs.IngresoPorMetodoPagoDTO;
import com.ApiRestStock.CRUD.ventas.VentaModel;
import com.ApiRestStock.CRUD.ventas.enums.MetodoPago;
import com.ApiRestStock.CRUD.ventas.fiado.FiadoModel;

@Service
public class IngresoService {

    @Autowired
    IngresoRepository ingresoRepository;

    public Optional<IngresoModel> registrarIngreso(BigDecimal total, TipoIngreso tipo, VentaModel venta, FiadoModel fiado) {
        IngresoModel nuevoIngreso = new IngresoModel();
        nuevoIngreso.setFecha(OffsetDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")));
        nuevoIngreso.setTotal(total);
        nuevoIngreso.setTipo(tipo);
        nuevoIngreso.setVenta(venta);
        nuevoIngreso.setFiado(fiado);

        ingresoRepository.save(nuevoIngreso);
        return Optional.of(nuevoIngreso);
    }

    public Optional<IngresoModel> getIngresoById(Long id) {
        return ingresoRepository.findById(id);
    }

    public List<IngresoModel> getAllIngresos() {
        return ingresoRepository.findAll();
    }

    public List<IngresoModel> getIngresosByTipo(TipoIngreso tipo) {
        return ingresoRepository.findByTipo(tipo);
    }

    public double getTotalIngresos() {
        Double total = ingresoRepository.sumTotalIngresos();
        return total != null ? total : 0.0;
    }

    public double getTotalIngresosLastDays(int dias) {
        OffsetDateTime hasta = OffsetDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));
        OffsetDateTime desde = hasta.minusDays(dias);
        OffsetDateTime desde00 = desde.withHour(0).withMinute(0).withSecond(0).withNano(0);

        Double total = ingresoRepository.sumTotalIngresosBetween(desde00, hasta);
        return total != null ? total : 0.0;
    }

    /**
     * Obtiene los ingresos del día actual agrupados por método de pago
     */
    public List<IngresoPorMetodoPagoDTO> getIngresosPorMetodoPagoDelDia() {
        LocalDate hoy = LocalDate.now();
        List<Object[]> resultados = ingresoRepository.findIngresosPorMetodoPagoDelDia(hoy);
        
        return resultados.stream()
            .map(row -> new IngresoPorMetodoPagoDTO(
                (MetodoPago) row[0],
                (BigDecimal) row[1]
            ))
            .toList();
    }
}
