package com.ApiRestStock.CRUD.ventas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ApiRestStock.CRUD.ventas.DTOs.VentaPorHoraDTO;
import com.ApiRestStock.CRUD.ventas.DTOs.VentaRequest;
import com.ApiRestStock.CRUD.ventas.DTOs.VentaResponse;
import com.ApiRestStock.CRUD.ventas.DTOs.VentasStatsResponse;



@RestController
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping
    public List<VentaModel> getVentas() {
        return this.ventaService.getVentas();
        
    }

    /**
     * Endpoint con paginación
     * @param page Número de página (empieza en 1)
     * @param size Cantidad de ventas por página (default: 10)
     * @return Page con ventas ordenadas por fecha descendente
     */
    @GetMapping("/paginated")
    public Page<VentaResponse> getVentasPaginadas(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
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
        // Convertir de 1-based (cliente) a 0-based (Spring Data)
        return this.ventaService.getVentasPaginadas(page - 1, size);
    }

    /**
     * Busca ventas por método de pago y/o fecha
     * @param q término de búsqueda para método de pago (opcional)
     * @param fecha fecha en formato YYYY-MM-DD (opcional)
     * @return Lista de ventas que coinciden
     * 
     * Ejemplos:
     * - /ventas/search?q=efectivo
     * - /ventas/search?fecha=2026-01-17
     * - /ventas/search?q=tarjeta&fecha=2026-01-17
     */
    @GetMapping("/search")
    public List<VentaModel> buscarVentas(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) java.time.LocalDate fecha) {
        return this.ventaService.buscarVentas(q, fecha);
    }

    @GetMapping("/mes/cantidad")
    public ResponseEntity<Long> getCantidadVentasDelMes() {
        Long cantidad = ventaService.getCantidadVentasDelMes();
        return ResponseEntity.ok(cantidad);
    }

    /**
     * Obtiene estadísticas de ventas: cantidad del mes, ingresos del día y egresos del día
     * @return VentasStatsResponse con las estadísticas
     */
    @GetMapping("/stats")
    public ResponseEntity<VentasStatsResponse> getVentasStats() {
        VentasStatsResponse stats = ventaService.getVentasStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/ultimas")
    public ResponseEntity<List<VentaResponse>> getUltimas5Ventas() {
        List<VentaResponse> ventas = ventaService.getUltimas5Ventas();
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/hoy/por-hora")
    public ResponseEntity<List<VentaPorHoraDTO>> getVentasPorHoraDelDia() {
        List<VentaPorHoraDTO> ventas = ventaService.getVentasPorHoraDelDia();
        return ResponseEntity.ok(ventas);
    }

    @PostMapping
    public ResponseEntity<Void> subirVenta(@RequestBody VentaRequest request) {

        
        ventaService.registrarVenta(request.items(), request.metodoPago());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
}
