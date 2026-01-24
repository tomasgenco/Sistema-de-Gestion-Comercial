package com.ApiRestStock.CRUD.Finanzas.cierreMes;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ApiRestStock.CRUD.Finanzas.DTOs.CerrarMesRequest;
import com.ApiRestStock.CRUD.Finanzas.DTOs.CierreMesResponse;

@RestController
@RequestMapping("/cierre-mes")
public class CierreMesController {

    private final CierreMesService cierreMesService;

    public CierreMesController(CierreMesService cierreMesService) {
        this.cierreMesService = cierreMesService;
    }

    /**
     * Obtiene un preview del cierre de mes sin guardarlo
     * Muestra los totales calculados por método de pago y todos los cierres diarios
     */
    @GetMapping("/preview")
    public ResponseEntity<CierreMesResponse> obtenerPreview(
            @RequestParam int mes,
            @RequestParam int anio) {
        
        if (mes < 1 || mes > 12) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El mes debe estar entre 1 y 12"
            );
        }
        
        CierreMesResponse preview = cierreMesService.obtenerPreview(mes, anio);
        return ResponseEntity.ok(preview);
    }

    /**
     * Confirma y guarda el cierre de mes
     * Automáticamente vincula todos los cierres de caja diarios del mes
     */
    @PostMapping
    public ResponseEntity<CierreMesResponse> cerrarMes(@Validated @RequestBody CerrarMesRequest request) {
        CierreMesResponse response = cierreMesService.cerrarMes(request.getMes(), request.getAnio());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista los cierres de mes paginados
     * @param anio Año opcional para filtrar
     * @param page Número de página (empieza en 1 para el cliente)
     * @param size Cantidad por página (default: 12 meses)
     * @return Page con cierres de mes ordenados por año y mes descendente
     */
    @GetMapping("/lista")
    public Page<CierreMesResponse> listar(
            @RequestParam(required = false) Integer anio,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        if (page < 1) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El número de página debe ser mayor o igual a 1"
            );
        }
        if (size < 1 || size > 100) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El tamaño de página debe estar entre 1 y 100"
            );
        }
        
        return cierreMesService.listar(anio, page - 1, size);
    }
}
