package com.ApiRestStock.CRUD.Finanzas.cierreCaja;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ApiRestStock.CRUD.Finanzas.DTOs.CerrarCajaRequest;
import com.ApiRestStock.CRUD.Finanzas.DTOs.CierreCajaResponse;


@RestController
@RequestMapping("/cierre-caja")
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


    //Lista todos los cierres de caja paginados
    @GetMapping
    public ResponseEntity<Page<CierreCajaResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(cierreCajaService.listar(pageable));
    }

    //Obtiene estadísticas del día actual (ventas + efectivo en sistema)
    @GetMapping("/hoy")
    public ResponseEntity<CierreCajaResponse> obtenerEstadisticasHoy() {
        return ResponseEntity.ok(cierreCajaService.obtenerEstadisticasDelDia(LocalDate.now()));
    }

}
