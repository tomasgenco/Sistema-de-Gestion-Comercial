package com.ApiRestStock.CRUD.Finanzas.Compra;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ApiRestStock.CRUD.Finanzas.Compra.DTOs.CompraRequestPorId;
import com.ApiRestStock.CRUD.Finanzas.Compra.DTOs.CompraResponse;
import com.ApiRestStock.CRUD.Finanzas.ingreso.DTOs.CompraRequest;

@RestController
@RequestMapping("/compras")
public class CompraController {

    @Autowired
    CompraService compraService;

    /**
     * Endpoint con paginación ordenado por fecha (más recientes primero)
     * @param page número de página (empieza en 1 para el cliente, internamente se convierte a 0-based)
     * @param size cantidad de compras por página (default: 10)
     * @return Page con compras, información de paginación
     */
    @GetMapping
    public Page<CompraResponse> getAllCompras(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
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
        return compraService.getComprasPaginadas(page - 1, size);
    }

    /**
     * Filtra compras por término de búsqueda
     * @param q término de búsqueda (nombre del proveedor)
     * @param page número de página (empieza en 1)
     * @param size cantidad por página (default: 10)
     * @return Page con compras filtradas
     */
    @GetMapping("/filtrar")
    public Page<CompraResponse> filtrarCompras(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
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
        return compraService.filtrarCompras(q, page - 1, size);
    }

    @PostMapping
    public ResponseEntity<CompraModel> registrarCompra(@RequestBody CompraRequest compraRequest) {
        CompraModel compraModel = compraService.registrarCompra(
            compraRequest.items(),
            compraRequest.metodoPago(),
            compraRequest.nombreEmpresa()
        );
        return ResponseEntity.ok(compraModel);
    }

    @PostMapping("/proveedor/{proveedorId}")
    public ResponseEntity<CompraModel> registrarCompraPorProveedorId(
            @PathVariable Long proveedorId,
            @RequestBody CompraRequestPorId compraRequest) {
        CompraModel compraModel = compraService.registrarCompraPorProveedorId(
            compraRequest.items(),
            compraRequest.metodoPago(),
            proveedorId
        );
        return ResponseEntity.ok(compraModel);
    }

    @GetMapping("/empresa/{nombre}")
    public List<CompraResponse> getComprasByEmpresaNombre(@PathVariable String nombre) {
        return compraService.getComprasPorEmpresa(nombre.toLowerCase());
    }
}
