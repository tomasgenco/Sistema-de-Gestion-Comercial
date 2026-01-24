package com.ApiRestStock.CRUD.Finanzas.cierreCaja;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ApiRestStock.CRUD.Finanzas.DTOs.CierreCajaResponse;
import com.ApiRestStock.CRUD.Finanzas.exception.CierreCajaDuplicadoException;
import com.ApiRestStock.CRUD.Finanzas.gasto.GastoRepository;
import com.ApiRestStock.CRUD.Finanzas.ingreso.IngresoRepository;
import com.ApiRestStock.CRUD.ventas.VentaRepository;
import com.ApiRestStock.CRUD.ventas.enums.MetodoPago;

@Service
public class CierreCajaService {

    private final CierreCajaRepository cierreCajaRepository;
    private final VentaRepository ventaRepository;
    private final IngresoRepository ingresoRepository;
    private final GastoRepository gastoRepository;

    public CierreCajaService(CierreCajaRepository cierreCajaRepository, VentaRepository ventaRepository, 
                             IngresoRepository ingresoRepository, GastoRepository gastoRepository) {
        this.cierreCajaRepository = cierreCajaRepository;
        this.ventaRepository = ventaRepository;
        this.ingresoRepository = ingresoRepository;
        this.gastoRepository = gastoRepository;
    }

    /**
     * Cierra la caja para una fecha (normalmente hoy).
     * - Si ya existe cierre para esa fecha, lanza excepción.
     * - Calcula totales del día por método de pago desde ventas.
     * - Calcula efectivo en sistema: ingresos efectivo - gastos efectivo (incluyendo compras a proveedores).
     * - Registra efectivo_real y diferencia (real - efectivo_en_sistema).
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

        // Efectivo en sistema = ingresos efectivo - gastos efectivo
        BigDecimal ingresosEfectivo = nz(ingresoRepository.sumIngresosEfectivoDelDia(fecha));
        BigDecimal gastosEfectivo = nz(gastoRepository.sumGastosEfectivoDelDia(fecha));
        BigDecimal efectivoEnSistema = ingresosEfectivo.subtract(gastosEfectivo);

        BigDecimal diferencia = efectivoReal.subtract(efectivoEnSistema);

        // Armar entidad
        CierreCajaModel cierre = new CierreCajaModel();
        cierre.setFecha(fecha);
        cierre.setTotalEfectivo(efectivoEnSistema); // Efectivo en sistema (ingresos - gastos)
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

    @Transactional(readOnly = true)
    public List<CierreCajaModel> obtenerPorRangoFechas(LocalDate desde, LocalDate hasta) {
        return cierreCajaRepository.findByFechaBetweenOrderByFechaAsc(desde, hasta);
    }

    @Transactional()
    public Optional<CierreCajaModel> obtenerPorFecha(LocalDate fecha) {

        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser null");
        }

        LocalDate dia = fecha;

        return cierreCajaRepository.findByFecha(dia);

    }

    

    /**
     * Lista los cierres de caja de un mes específico, paginados y ordenados por fecha descendente
     * @param mes Mes (1-12)
     * @param año Año
     * @param page Número de página (base 0)
     * @param size Tamaño de página
     * @return Page con cierres de caja del mes
     */
    @Transactional(readOnly = true)
    public Page<CierreCajaResponse> listarPorMes(int mes, int año, int page, int size) {
        // Calcular primer y último día del mes
        LocalDate primerDia = LocalDate.of(año, mes, 1);
        LocalDate ultimoDia = primerDia.withDayOfMonth(primerDia.lengthOfMonth());
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending());
        return cierreCajaRepository.findByFechaBetween(primerDia, ultimoDia, pageable)
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

    /**
     * Obtiene estadísticas del día sin registrar un cierre oficial.
     * Calcula efectivo en sistema (ingresos efectivo - gastos efectivo).
     */
    @Transactional(readOnly = true)
    public CierreCajaResponse obtenerEstadisticasDelDia(LocalDate fecha) {
        if (fecha == null) {
            fecha = LocalDate.now();
        }

        // Totales por método de pago desde ventas
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

        // Efectivo en sistema = ingresos efectivo - gastos efectivo
        BigDecimal ingresosEfectivo = nz(ingresoRepository.sumIngresosEfectivoDelDia(fecha));
        BigDecimal gastosEfectivo = nz(gastoRepository.sumGastosEfectivoDelDia(fecha));
        System.out.println("Ingresos Efectivo: " + ingresosEfectivo);
        System.out.println("Gastos Efectivo: " + gastosEfectivo);
        BigDecimal efectivoEnSistema = ingresosEfectivo.subtract(gastosEfectivo);
        System.out.println("Efectivo en Sistema: " + efectivoEnSistema);

        // Armar response
        CierreCajaResponse response = new CierreCajaResponse();
        response.setFecha(fecha);
        response.setTotalEfectivo(totalEfectivo);
        response.setTotalMercadoPago(totalMercadoPago);
        response.setTotalCuentaDni(totalCuentaDni);
        response.setTotalTarjetaCredito(totalTarjetaCredito);
        response.setTotalTarjetaDebito(totalTarjetaDebito);
        response.setTotalVentas(totalVentas);
        response.setEfectivoReal(efectivoEnSistema); // Usamos este campo para "efectivo en sistema"
        response.setDiferencia(BigDecimal.ZERO); // No calculamos diferencia porque no hay cierre oficial
        
        return response;
    }
}
