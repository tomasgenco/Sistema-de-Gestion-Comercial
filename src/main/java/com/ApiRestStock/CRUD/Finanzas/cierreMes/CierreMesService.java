package com.ApiRestStock.CRUD.Finanzas.cierreMes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ApiRestStock.CRUD.Finanzas.DTOs.CierreCajaResponse;
import com.ApiRestStock.CRUD.Finanzas.DTOs.CierreMesResponse;
import com.ApiRestStock.CRUD.Finanzas.cierreCaja.CierreCajaModel;
import com.ApiRestStock.CRUD.Finanzas.cierreCaja.CierreCajaService;
import com.ApiRestStock.CRUD.Finanzas.exception.CierreMesDuplicadoException;
import com.ApiRestStock.CRUD.Finanzas.gasto.GastoService;
import com.ApiRestStock.CRUD.Finanzas.ingreso.IngresoService;
import com.ApiRestStock.CRUD.ventas.enums.MetodoPago;

@Service
public class CierreMesService {

    private final CierreMesRepository cierreMesRepository;
    private final CierreCajaService cierreCajaService;
    private final IngresoService ingresoService;
    private final GastoService gastoService;

    public CierreMesService(CierreMesRepository cierreMesRepository, 
                           CierreCajaService cierreCajaService,
                           IngresoService ingresoService,
                           GastoService gastoService) {
        this.cierreMesRepository = cierreMesRepository;
        this.cierreCajaService = cierreCajaService;
        this.ingresoService = ingresoService;
        this.gastoService = gastoService;
    }

    /**
     * Obtiene un preview del cierre de mes sin guardarlo en DB
     * Calcula los totales desde los cierres de caja del mes
     */
    @Transactional(readOnly = true)
    public CierreMesResponse obtenerPreview(int mes, int anio) {
        // Validar mes y año
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("El mes debe estar entre 1 y 12");
        }

        // Calcular rango de fechas del mes
        LocalDate primerDia = LocalDate.of(anio, mes, 1);
        LocalDate ultimoDia = primerDia.withDayOfMonth(primerDia.lengthOfMonth());

        // Obtener todos los cierres de caja del mes
        List<CierreCajaModel> cierresCaja = cierreCajaService.obtenerPorRangoFechas(primerDia, ultimoDia);

        // Calcular totales por método de pago sumando todos los cierres diarios
        BigDecimal totalEfectivo = cierresCaja.stream()
                .map(cierre -> cierre.getTotalPorMetodoPago(MetodoPago.EFECTIVO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalMercadoPago = cierresCaja.stream()
                .map(cierre -> cierre.getTotalPorMetodoPago(MetodoPago.MERCADO_PAGO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCuentaDni = cierresCaja.stream()
                .map(cierre -> cierre.getTotalPorMetodoPago(MetodoPago.CUENTA_DNI))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalTarjetaCredito = cierresCaja.stream()
                .map(cierre -> cierre.getTotalPorMetodoPago(MetodoPago.TARJETA_CREDITO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalTarjetaDebito = cierresCaja.stream()
                .map(cierre -> cierre.getTotalPorMetodoPago(MetodoPago.TARJETA_DEBITO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular ingresos totales del mes
        BigDecimal ingresosTotal = ingresoService.getTotalIngresosByFechaBetween(primerDia, ultimoDia);

        // Calcular egresos totales del mes
        BigDecimal egresosTotal = gastoService.getTotalGastosByFechaBetween(primerDia, ultimoDia);

        // Calcular resultado (ingresos - egresos)
        BigDecimal resultadoTotal = ingresosTotal.subtract(egresosTotal);

        // Construir response
        CierreMesResponse response = new CierreMesResponse();
        response.setAnio(anio);
        response.setMes(mes);
        response.setTotalEfectivo(totalEfectivo);
        response.setTotalMercadoPago(totalMercadoPago);
        response.setTotalCuentaDni(totalCuentaDni);
        response.setTotalTarjetaCredito(totalTarjetaCredito);
        response.setTotalTarjetaDebito(totalTarjetaDebito);
        response.setIngresosTotal(ingresosTotal);
        response.setEgresosTotal(egresosTotal);
        response.setResultadoTotal(resultadoTotal);

        // Mapear cierres de caja a DTOs
        List<CierreCajaResponse> cierresResponse = cierresCaja.stream()
                .map(this::toCierreCajaResponse)
                .collect(Collectors.toList());
        response.setCierreCajas(cierresResponse);

        return response;
    }

    /**
     * Confirma y guarda el cierre de mes en DB
     * Acepta mes y año, y automáticamente vincula todos los cierres de caja diarios del mes
     */
    @Transactional
    public CierreMesResponse cerrarMes(int mes, int anio) {
        // Validar que no exista ya un cierre para este mes
        if (cierreMesRepository.existsByAnioAndMes(anio, mes)) {
            throw new CierreMesDuplicadoException(mes, anio);
        }

        // Validar mes
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("El mes debe estar entre 1 y 12");
        }

        // Obtener preview con los cálculos
        CierreMesResponse preview = obtenerPreview(mes, anio);

        // Crear entidad
        CierreMesModel cierreMes = new CierreMesModel();
        cierreMes.setAnio(anio);
        cierreMes.setMes(mes);
        cierreMes.setTotalEfectivo(preview.getTotalEfectivo());
        cierreMes.setTotalMercadoPago(preview.getTotalMercadoPago());
        cierreMes.setTotalCuentaDni(preview.getTotalCuentaDni());
        cierreMes.setTotalTarjetaCredito(preview.getTotalTarjetaCredito());
        cierreMes.setTotalTarjetaDebito(preview.getTotalTarjetaDebito());
        cierreMes.setIngresosTotal(preview.getIngresosTotal());
        cierreMes.setEgresosTotal(preview.getEgresosTotal());
        cierreMes.setResultadoTotal(preview.getResultadoTotal());

        // Obtener y vincular todos los cierres de caja del mes automáticamente
        LocalDate primerDia = LocalDate.of(anio, mes, 1);
        LocalDate ultimoDia = primerDia.withDayOfMonth(primerDia.lengthOfMonth());
        List<CierreCajaModel> cierresCaja = cierreCajaService.obtenerPorRangoFechas(primerDia, ultimoDia);
        
        // Establecer la relación bidireccional
        for (CierreCajaModel cierreCaja : cierresCaja) {
            cierreCaja.setCierreMes(cierreMes);
        }
        cierreMes.setCierreCajas(cierresCaja);

        // Guardar
        CierreMesModel saved = cierreMesRepository.save(cierreMes);

        // Retornar response con el ID generado
        preview.setId(saved.getId());
        return preview;
    }

    private CierreCajaResponse toCierreCajaResponse(CierreCajaModel cierre) {
        CierreCajaResponse response = new CierreCajaResponse();
        response.setId(cierre.getId());
        response.setFecha(cierre.getFecha());
        response.setTotalEfectivo(cierre.getTotalEfectivo());
        response.setTotalMercadoPago(cierre.getTotalMercadoPago());
        response.setTotalCuentaDni(cierre.getTotalCuentaDni());
        response.setTotalTarjetaCredito(cierre.getTotalTarjetaCredito());
        response.setTotalTarjetaDebito(cierre.getTotalTarjetaDebito());
        response.setTotalVentas(cierre.getTotalVentas());
        response.setEfectivoReal(cierre.getEfectivoReal());
        response.setDiferencia(cierre.getDiferencia());
        response.setObservaciones(cierre.getObservaciones());
        return response;
    }

    /**
     * Lista los cierres de mes paginados
     * @param anio Año opcional para filtrar (null = todos los años)
     * @param page Número de página (base 0)
     * @param size Tamaño de página
     * @return Page con cierres de mes (sin detalles de cierres diarios)
     */
    @Transactional(readOnly = true)
    public Page<CierreMesResponse> listar(Integer anio, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CierreMesModel> cierresPage;
        
        if (anio != null) {
            cierresPage = cierreMesRepository.findByAnioOrderByMesDesc(anio, pageable);
        } else {
            cierresPage = cierreMesRepository.findAllByOrderByAnioDescMesDesc(pageable);
        }
        
        return cierresPage.map(this::toResponseSinDetalles);
    }

    private CierreMesResponse toResponseSinDetalles(CierreMesModel cierre) {
        CierreMesResponse response = new CierreMesResponse();
        response.setId(cierre.getId());
        response.setAnio(cierre.getAnio());
        response.setMes(cierre.getMes());
        response.setTotalEfectivo(cierre.getTotalEfectivo());
        response.setTotalMercadoPago(cierre.getTotalMercadoPago());
        response.setTotalCuentaDni(cierre.getTotalCuentaDni());
        response.setTotalTarjetaCredito(cierre.getTotalTarjetaCredito());
        response.setTotalTarjetaDebito(cierre.getTotalTarjetaDebito());
        response.setIngresosTotal(cierre.getIngresosTotal());
        response.setEgresosTotal(cierre.getEgresosTotal());
        response.setResultadoTotal(cierre.getResultadoTotal());
        // No incluye los cierres diarios para optimizar el listado
        return response;
    }
}
