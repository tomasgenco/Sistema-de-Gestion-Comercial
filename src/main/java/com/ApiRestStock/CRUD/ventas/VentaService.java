package com.ApiRestStock.CRUD.ventas;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ApiRestStock.CRUD.Finanzas.enums.TipoIngreso;
import com.ApiRestStock.CRUD.Finanzas.ingreso.IngresoService;
import com.ApiRestStock.CRUD.stock.ProductService;
import com.ApiRestStock.CRUD.ventas.DTOs.ItemVentaRequest;
import com.ApiRestStock.CRUD.ventas.DTOs.VentaResponse;
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
    

    public BigDecimal calcularTotal(VentaModel venta) {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleVentaModel detalle : venta.getDetalles()) {

            BigDecimal subtotal = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            total = total.add(subtotal);
        }
        
        venta.setTotal(total);
        return total;
    }

    public List<VentaModel> getVentas() {
        return ventaRepository.findAll();
    }

    public Long getCantidadVentasDelMes() {
        OffsetDateTime ahora = OffsetDateTime.now();
        int anio = ahora.getYear();
        int mes = ahora.getMonthValue();
        return ventaRepository.countVentasDelMes(anio, mes);
    }

    public List<VentaResponse> getUltimas5Ventas() {
        List<VentaModel> ventas = ventaRepository.findTop5ByOrderByFechaHoraDesc();
        return ventas.stream()
                .map(venta -> new VentaResponse(
                    venta.getId(),
                    venta.getFechaHora(),
                    venta.getTotal(),
                    venta.getMetodoPago()
                ))
                .toList();
    }

@Transactional
public VentaModel registrarVenta(List<ItemVentaRequest> items, MetodoPago metodoPago) {

    VentaModel venta = new VentaModel();
    venta.setMetodoPago(metodoPago);
    venta.setFechaHora(OffsetDateTime.now());

    List<DetalleVentaModel> detalles = new ArrayList<>();

    for (ItemVentaRequest item : items) {
        
        DetalleVentaModel detalle = new DetalleVentaModel();
        detalle.setCantidad(item.cantidad());
        detalle.setPrecioUnitario(item.precioUnitario());
        detalle.setNombreProducto(item.nombreProducto());
        detalle.setVenta(venta); // Establecer la referencia a la venta
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
