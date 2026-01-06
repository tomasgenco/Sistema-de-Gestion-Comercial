package com.ApiRestStock.CRUD.Finanzas.cierreCaja;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ApiRestStock.CRUD.Finanzas.DTOs.CierreCajaResponse;
import com.ApiRestStock.CRUD.Finanzas.exception.CierreCajaDuplicadoException;
import com.ApiRestStock.CRUD.ventas.VentaRepository;
import com.ApiRestStock.CRUD.ventas.enums.MetodoPago;

@Service
public class CierreCajaService {

    private final CierreCajaRepository cierreCajaRepository;
    private final VentaRepository ventaRepository;

    public CierreCajaService(CierreCajaRepository cierreCajaRepository, VentaRepository ventaRepository) {
        this.cierreCajaRepository = cierreCajaRepository;
        this.ventaRepository = ventaRepository;
    }

    /**
     * Cierra la caja para una fecha (normalmente hoy).
     * - Si ya existe cierre para esa fecha, lanza excepción.
     * - Calcula totales del día por método de pago desde ventas.
     * - Registra efectivo_real y diferencia (real - teorico_efectivo).
     */
    @Transactional
    public CierreCajaModel cerrarCaja(LocalDate fecha, BigDecimal efectivoReal, String observaciones) {

        if (fecha == null) {
            fecha = LocalDate.now();
        }

        if (efectivoReal == null) {
            efectivoReal = BigDecimal.ZERO;
        }

        if (cierreCajaRepository.existsByFecha(fecha)) {
            throw new CierreCajaDuplicadoException(fecha);
        }

        // Totales por método de pago (si no hay ventas, deberían volver 0)
        BigDecimal totalEfectivo = nz(ventaRepository.sumTotalByFechaAndMetodoPago(fecha, MetodoPago.EFECTIVO));
        BigDecimal totalMercadoPago = nz(ventaRepository.sumTotalByFechaAndMetodoPago(fecha, MetodoPago.MERCADO_PAGO));
        BigDecimal totalCuentaDni = nz(ventaRepository.sumTotalByFechaAndMetodoPago(fecha, MetodoPago.CUENTA_DNI));
        BigDecimal totalTarjetaCredito = nz(ventaRepository.sumTotalByFechaAndMetodoPago(fecha, MetodoPago.TARJETA_CREDITO));
        BigDecimal totalTarjetaDebito = nz(ventaRepository.sumTotalByFechaAndMetodoPago(fecha, MetodoPago.TARJETA_DEBITO));

        BigDecimal totalVentas = totalEfectivo
                .add(totalMercadoPago)
                .add(totalCuentaDni)
                .add(totalTarjetaCredito)
                .add(totalTarjetaDebito);

        BigDecimal diferencia = efectivoReal.subtract(totalEfectivo);

        // Armar entidad
        CierreCajaModel cierre = new CierreCajaModel();
        cierre.setFecha(fecha);
        cierre.setTotalEfectivo(totalEfectivo);
        cierre.setTotalPorMetodoPago(MetodoPago.MERCADO_PAGO, totalMercadoPago);
        cierre.setTotalPorMetodoPago(MetodoPago.CUENTA_DNI, totalCuentaDni);
        cierre.setTotalPorMetodoPago(MetodoPago.TARJETA_CREDITO, totalTarjetaCredito);
        cierre.setTotalPorMetodoPago(MetodoPago.TARJETA_DEBITO, totalTarjetaDebito);

        cierre.setTotalVentas(totalVentas);
        cierre.setEfectivoReal(efectivoReal);
        cierre.setDiferencia(diferencia);
        cierre.setObservaciones(observaciones);

        return cierreCajaRepository.save(cierre);
    }

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    @Transactional()
    public Optional<CierreCajaModel> obtenerPorFecha(LocalDate fecha) {

        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser null");
        }

        LocalDate dia = fecha;

        return cierreCajaRepository.findByFecha(dia);

    }

    

    @Transactional(readOnly = true)
    public Page<CierreCajaResponse> listar(Pageable pageable) {
        return cierreCajaRepository.findAll(pageable)
                .map(this::toResponse);
    }

    private CierreCajaResponse toResponse(CierreCajaModel c) {
        CierreCajaResponse r = new CierreCajaResponse();
        r.setId(c.getId());
        r.setFecha(c.getFecha());

        r.setTotalEfectivo(c.getTotalPorMetodoPago(MetodoPago.EFECTIVO));
        r.setTotalMercadoPago(c.getTotalPorMetodoPago(MetodoPago.MERCADO_PAGO));
        r.setTotalCuentaDni(c.getTotalPorMetodoPago(MetodoPago.CUENTA_DNI));
        r.setTotalTarjetaCredito(c.getTotalPorMetodoPago(MetodoPago.TARJETA_CREDITO));
        r.setTotalTarjetaDebito(c.getTotalPorMetodoPago(MetodoPago.TARJETA_DEBITO));
        r.setTotalVentas(c.getTotalVentas());
        r.setEfectivoReal(c.getEfectivoReal());
        r.setDiferencia(c.getDiferencia());
        r.setObservaciones(c.getObservaciones());
        return r;
    }

    public List<CierreCajaResponse> listarEntreFechas(LocalDate desde, LocalDate hasta) {
    if (desde == null || hasta == null) {
        throw new IllegalArgumentException("Desde y hasta son obligatorios");
    }
    if (hasta.isBefore(desde)) {
        throw new IllegalArgumentException("La fecha 'hasta' no puede ser anterior a 'desde'");
    }

    return cierreCajaRepository.findByFechaBetweenOrderByFechaAsc(desde, hasta)
            .stream()
            .map(this::toResponse)
            .toList();
}
}
