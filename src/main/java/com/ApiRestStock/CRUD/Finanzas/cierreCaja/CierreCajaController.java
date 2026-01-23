package com.ApiRestStock.CRUD.Finanzas.cierreCaja;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ApiRestStock.CRUD.Finanzas.DTOs.CerrarCajaRequest;
import com.ApiRestStock.CRUD.Finanzas.DTOs.CierreCajaResponse;


@RestController
@RequestMapping("/cierre-caja")
@PreAuthorize("hasRole('ADMIN')")
public class CierreCajaController {

    @Autowired
    private CierreCajaService cierreCajaService;



    //Registra un cierre de caja en DB
    @PostMapping
    public ResponseEntity<CierreCajaModel> cerrarCaja(@Validated @RequestBody CerrarCajaRequest request) {

        
        CierreCajaModel  response = cierreCajaService.cerrarCaja(
                request.getFecha(),
                request.getEfectivoReal(),
                request.getObservaciones()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Obtiene un cierre de caja con una fecha en particular
    @GetMapping("/{fecha}")
    public ResponseEntity<CierreCajaModel> obtenerPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return cierreCajaService.obtenerPorFecha(fecha)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    /**
     * Lista los cierres de caja de un mes específico, paginados y ordenados por fecha descendente
     * @param mes Mes (1-12)
     * @param año Año (ej: 2026)
     * @param page Número de página (empieza en 1 para el cliente, internamente se convierte a 0-based)
     * @param size Cantidad de cierres por página (default: 10)
     * @return Page con cierres de caja e información de paginación
     */
    @GetMapping
    public Page<CierreCajaResponse> listar(
            @RequestParam int mes,
            @RequestParam int año,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Validar mes
        if (mes < 1 || mes > 12) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El mes debe estar entre 1 y 12"
            );
        }
        // Validar que page sea al menos 1
        if (page < 1) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El número de página debe ser mayor o igual a 1"
            );
        }
        // Validar que size sea positivo
        if (size < 1 || size > 100) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El tamaño de página debe estar entre 1 y 100"
            );
        }
        
        // Convertir de 1-based (cliente) a 0-based (Spring Data)
        return cierreCajaService.listarPorMes(mes, año, page - 1, size);
    }


    //Obtiene estadísticas del día actual (ventas + efectivo en sistema)
    @GetMapping("/hoy")
    public ResponseEntity<CierreCajaResponse> obtenerEstadisticasHoy() {
        return ResponseEntity.ok(cierreCajaService.obtenerEstadisticasDelDia(LocalDate.now()));
    }

}
