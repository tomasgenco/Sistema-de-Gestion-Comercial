package com.ApiRestStock.CRUD.ventas;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ApiRestStock.CRUD.stock.IProductRepository;
import com.ApiRestStock.CRUD.stock.ProductModel;
import com.ApiRestStock.CRUD.ventas.DTOs.ItemVentaRequest;

import jakarta.transaction.Transactional;

@Service
public class VentaService {

    @Autowired
    VentaRepository ventaRepository;

    @Autowired
    IProductRepository productRepository;
    

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
        
        
        // Actualizar el stock del producto
        Optional<ProductModel> prod = productRepository.findByNombre(item.nombreProducto());
        prod.ifPresent(product -> {
            product.setStock(product.getStock() - item.cantidad());
            productRepository.save(product);
        });


    }

    venta.setDetalles(detalles);
    calcularTotal(venta);

    return ventaRepository.save(venta);
}

    

    






}
