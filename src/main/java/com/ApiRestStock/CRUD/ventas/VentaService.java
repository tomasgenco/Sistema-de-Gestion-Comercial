package com.ApiRestStock.CRUD.ventas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ApiRestStock.CRUD.Finanzas.enums.TipoIngreso;
import com.ApiRestStock.CRUD.Finanzas.gasto.GastoRepository;
import com.ApiRestStock.CRUD.Finanzas.ingreso.IngresoRepository;
import com.ApiRestStock.CRUD.Finanzas.ingreso.IngresoService;
import com.ApiRestStock.CRUD.stock.ProductService;
import com.ApiRestStock.CRUD.ventas.DTOs.DetalleVentaResponse;
import com.ApiRestStock.CRUD.ventas.DTOs.ItemVentaRequest;
import com.ApiRestStock.CRUD.ventas.DTOs.VentaPorHoraDTO;
import com.ApiRestStock.CRUD.ventas.DTOs.VentaResponse;
import com.ApiRestStock.CRUD.ventas.DTOs.VentasStatsResponse;
import com.ApiRestStock.CRUD.ventas.enums.MetodoPago;

import jakarta.transaction.Transactional;

@Service
public class VentaService {

    @Autowired
    VentaRepository ventaRepository;

    @Autowired
    ProductService productService;

    @Autowired
    IngresoService ingresoService;

    @Autowired
    IngresoRepository ingresoRepository;

    @Autowired
    GastoRepository gastoRepository;
    

    public BigDecimal calcularTotal(VentaModel venta) {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleVentaModel detalle : venta.getDetalles()) {

            BigDecimal subtotal = detalle.getPrecioUnitario().multiply(detalle.getCantidad());
            total = total.add(subtotal);
        }
        
        venta.setTotal(total);
        return total;
    }

    public List<VentaModel> getVentas() {
        return ventaRepository.findAll();
    }

    /**
     * Obtiene ventas paginadas ordenadas por fecha descendente
     * @param page Número de página (base 0)
     * @param size Tamaño de página
     * @return Page con ventas
     */
    public Page<VentaResponse> getVentasPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaHora").descending());
        Page<VentaModel> ventasPage = ventaRepository.findAll(pageable);
        
        // Obtener IDs de las ventas de la página actual
        List<Long> ventaIds = ventasPage.getContent().stream()
            .map(VentaModel::getId)
            .toList();
        
        // Cargar ventas con detalles y productos usando JOIN FETCH
        List<VentaModel> ventasConProductos = ventaIds.isEmpty() ? 
            List.of() : 
            ventaRepository.findByIdInWithDetallesAndProductos(ventaIds);
        
        // Convertir a DTO
        List<VentaResponse> ventasResponse = ventasConProductos.stream()
            .map(venta -> new VentaResponse(
                venta.getId(),
                venta.getFechaHora(),
                venta.getTotal(),
                venta.getMetodoPago(),
                venta.getDetalles().stream()
                    .map(detalle -> new DetalleVentaResponse(
                        detalle.getId(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getNombreProducto(),
                        detalle.getProductoId()
                    ))
                    .toList()
            ))
            .toList();
        
        // Crear Page con los resultados convertidos
        return new org.springframework.data.domain.PageImpl<>(
            ventasResponse,
            pageable,
            ventasPage.getTotalElements()
        );
    }

    /**
     * Busca ventas por método de pago y/o fecha
     * @param searchTerm término de búsqueda para método de pago (opcional)
     * @param fecha fecha en formato LocalDate (opcional)
     * @return Lista de ventas que coinciden
     */
    public List<VentaModel> buscarVentas(String searchTerm, LocalDate fecha) {
        // Si ambos parámetros son nulos o vacíos, retornar vacío
        if ((searchTerm == null || searchTerm.trim().isEmpty()) && fecha == null) {
            return List.of();
        }
        
        // Normalizar el término de búsqueda: reemplazar espacios por guiones bajos
        String normalizedTerm = searchTerm != null ? searchTerm.trim().replace(" ", "_") : null;
        
        // Buscar por ambos criterios
        if (normalizedTerm != null && !normalizedTerm.isEmpty() && fecha != null) {
            return ventaRepository.buscarPorMetodoPagoYFecha(normalizedTerm, fecha);
        }
        
        // Buscar solo por fecha
        if (fecha != null) {
            return ventaRepository.buscarPorFecha(fecha);
        }
        
        // Buscar solo por método de pago
        return ventaRepository.buscarPorMetodoPago(normalizedTerm);
    }

    public Long getCantidadVentasDelMes() {
        OffsetDateTime ahora = OffsetDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));
        int anio = ahora.getYear();
        int mes = ahora.getMonthValue();
        return ventaRepository.countVentasDelMes(anio, mes);
    }

    /**
     * Obtiene estadísticas de ventas: cantidad del mes, ingresos del día y egresos del día
     */
    public VentasStatsResponse getVentasStats() {
        // Ventas del mes
        Long ventasDelMes = getCantidadVentasDelMes();
        
        // Ventas del día usando la zona horaria de Argentina
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Argentina/Buenos_Aires"));
        Long ventasDelDia = ventaRepository.countVentasDelDia(hoy);
        
        // Ingresos y gastos del día actual
        BigDecimal ingresosDelDia = ingresoRepository.sumIngresosDelDia(hoy);
        BigDecimal gastosDelDia = gastoRepository.sumGastosDelDia(hoy);
        
        return new VentasStatsResponse(ventasDelMes, ventasDelDia, ingresosDelDia, gastosDelDia);
    }


    public List<VentaResponse> getUltimas5Ventas() {
        List<VentaModel> ventas = ventaRepository.findTop5ByOrderByFechaHoraDesc();
        return ventas.stream()
                .map(venta -> new VentaResponse(
                    venta.getId(),
                    venta.getFechaHora(),
                    venta.getTotal(),
                    venta.getMetodoPago(),
                    venta.getDetalles().stream()
                        .map(detalle -> new DetalleVentaResponse(
                            detalle.getId(),
                            detalle.getCantidad(),
                            detalle.getPrecioUnitario(),
                            detalle.getNombreProducto(),
                            detalle.getProductoId()
                        ))
                        .toList()
                ))
                .toList();
    }

    /**
     * Obtiene las ventas del día actual agrupadas por hora.
     * Diseñado para ser consumido por gráficos Rechart.
     * @return Lista de VentaPorHoraDTO con hora, cantidad y total de ventas
     */
    public List<VentaPorHoraDTO> getVentasPorHoraDelDia() {
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Argentina/Buenos_Aires"));
        List<Object[]> resultados = ventaRepository.findVentasAgrupadasPorHora(hoy);
        
        return resultados.stream()
                .map(row -> new VentaPorHoraDTO(
                    ((Number) row[0]).intValue(),  // hora
                    ((Number) row[1]).longValue(), // cantidadVentas
                    (BigDecimal) row[2]            // totalVentas
                ))
                .toList();
    }

@Transactional
public VentaModel registrarVenta(List<ItemVentaRequest> items, MetodoPago metodoPago) {

    VentaModel venta = new VentaModel();
    venta.setMetodoPago(metodoPago);
    venta.setFechaHora(OffsetDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")));

    List<DetalleVentaModel> detalles = new ArrayList<>();

    for (ItemVentaRequest item : items) {
        
        // Obtener el producto para establecer la FK y capturar snapshots
        var producto = productService.getProductByNombre(item.nombreProducto());
        
        DetalleVentaModel detalle = new DetalleVentaModel();
        detalle.setProducto(producto); // FK al producto
        detalle.setCantidad(item.cantidad());
        detalle.setPrecioUnitario(item.precioUnitario());
        detalle.setNombreProducto(producto.getNombre()); // Snapshot
        detalle.setVenta(venta); // FK a la venta
        detalles.add(detalle);
        
        
        //Actualiza el stock del producto vendido
        productService.actualizarStockPorNombre(item.nombreProducto(), item.cantidad(), "restar");


    }

    venta.setDetalles(detalles);
    BigDecimal total = calcularTotal(venta);
    
    // Registra la venta como un ingreso en el sistema financiero
    ingresoService.registrarIngreso(total, TipoIngreso.VENTA, venta, null);
    
    return ventaRepository.save(venta);
}


    

    






}
