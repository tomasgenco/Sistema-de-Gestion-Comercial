package com.ApiRestStock.CRUD.Finanzas.gasto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ApiRestStock.CRUD.Finanzas.enums.TipoGasto;

@Service
public class GastoService {

    @Autowired
    private GastoRepository gastoRepository;

    @Transactional
    public Optional<GastoModel> registrarGasto(BigDecimal total, TipoGasto tipo, String nombreProveedor) {
        GastoModel gastoModel = new GastoModel();
        gastoModel.setFecha(OffsetDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")));
        gastoModel.setTotal(total);
        gastoModel.setTipo(tipo);
        gastoModel.setNombreProveedor(nombreProveedor);

        
        
        GastoModel savedGasto = gastoRepository.save(gastoModel);
        return Optional.of(savedGasto);
    }

    public Optional<GastoModel> getGastoById(Long id) {
        return gastoRepository.findById(id);
    }

    public List<GastoModel> getAllGastos() {
        return gastoRepository.findAll();
    }

    public List<GastoModel> findByTipo(TipoGasto tipo) {
        return gastoRepository.findByTipo(tipo);
    }

    public BigDecimal getTotalGastosByFechaBetween(LocalDate desde, LocalDate hasta) {
        // Convertir LocalDate a OffsetDateTime con zona horaria de Argentina
        OffsetDateTime desdeDateTime = desde.atStartOfDay(ZoneId.of("America/Argentina/Buenos_Aires")).toOffsetDateTime();
        OffsetDateTime hastaDateTime = hasta.atTime(23, 59, 59).atZone(ZoneId.of("America/Argentina/Buenos_Aires")).toOffsetDateTime();
        
        Double total = gastoRepository.sumTotalGastosBetween(desdeDateTime, hastaDateTime);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }

    public double getTotalGastos() {
        Double total = gastoRepository.sumTotalGastos();
        return total != null ? total : 0.0;
    }

    public double getTotalGastosLastDays(int dias) {
        OffsetDateTime hasta = OffsetDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));
        OffsetDateTime desde = hasta.minusDays(dias);
        OffsetDateTime desde00 = desde.withHour(0).withMinute(0).withSecond(0).withNano(0);

        Double total = gastoRepository.sumTotalGastosBetween(desde00, hasta);
        return total != null ? total : 0.0;
    }
}
