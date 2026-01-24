package com.ApiRestStock.CRUD.Finanzas.Compra;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ApiRestStock.CRUD.Finanzas.Compra.DTOs.CompraResponse;
import com.ApiRestStock.CRUD.Finanzas.Compra.DTOs.DetalleCompraResponse;
import com.ApiRestStock.CRUD.Finanzas.exception.NoFoundComprasProveedorException;
import com.ApiRestStock.CRUD.Finanzas.gasto.GastoService;
import com.ApiRestStock.CRUD.Finanzas.ingreso.DTOs.ItemCompraRequest;
import com.ApiRestStock.CRUD.proveedor.ProveedorService;
import com.ApiRestStock.CRUD.stock.ProductModel;
import com.ApiRestStock.CRUD.stock.ProductService;
import com.ApiRestStock.CRUD.ventas.enums.MetodoPago;



@Service
public class CompraService {

    @Autowired
    CompraRepository compraRepository;

    @Autowired
    ProveedorService proveedorService;

    @Autowired
    ProductService productService;

    @Autowired
    GastoService gastoService;

    public List<CompraModel> getAllCompras() {
        return compraRepository.findAll();
    }

    /**
     * Obtiene compras paginadas ordenadas por fecha (más recientes primero)
     * @param page número de página (base 0)
     * @param size tamaño de página
     * @return Page con compras e información de paginación
     */
    public Page<CompraResponse> getComprasPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaHora").descending());
        Page<CompraModel> comprasPage = compraRepository.findAll(pageable);
        
        // Convertir a DTO
        return comprasPage.map(compra -> new CompraResponse(
            compra.getId(),
            compra.getFechaHora(),
            compra.getMetodoPago().name(),
            compra.getProveedor().getNombreEmpresa(),
            compra.getTotal(),
            compra.getDetalles().stream()
                .map(det -> new DetalleCompraResponse(
                    det.getId(),
                    det.getCantidad(),
                    det.getPrecioUnitario(),
                    det.getNombreProducto(),
                    det.getProducto().getId(),
                    det.getProducto().getTipoVenta()
                ))
                .toList()
        ));
    }

    /**
     * Filtra compras por término de búsqueda
     * @param searchTerm texto para buscar en nombre del proveedor
     * @param page número de página (base 0)
     * @param size tamaño de página
     * @return Page con compras filtradas
     */
    public Page<CompraResponse> filtrarCompras(String searchTerm, int page, int size) {
        // Como la query ya tiene ORDER BY, usamos Pageable sin Sort
        Pageable pageable = PageRequest.of(page, size);
        Page<CompraModel> comprasPage = compraRepository.filtrarCompras(searchTerm, pageable);
        
        // Convertir a DTO
        return comprasPage.map(compra -> new CompraResponse(
            compra.getId(),
            compra.getFechaHora(),
            compra.getMetodoPago().name(),
            compra.getProveedor().getNombreEmpresa(),
            compra.getTotal(),
            compra.getDetalles().stream()
                .map(det -> new DetalleCompraResponse(
                    det.getId(),
                    det.getCantidad(),
                    det.getPrecioUnitario(),
                    det.getNombreProducto(),
                    det.getProducto() != null ? det.getProducto().getId() : null,
                    det.getProducto() != null ? det.getProducto().getTipoVenta() : det.getTipoVenta()
                ))
                .toList()
        ));
    }



    public BigDecimal sumarTotal(List<DetalleCompraModel> detalles) {

        BigDecimal total = BigDecimal.ZERO;

        for (DetalleCompraModel detalle : detalles) {
            BigDecimal subtotal = detalle.getPrecioUnitario()
                    .multiply(detalle.getCantidad());

            total = total.add(subtotal);
        }

        return total;
    }




    @Transactional
    public CompraModel registrarCompra(List<ItemCompraRequest> items, MetodoPago metodoPago, String nombreProveedor) {

        BigDecimal total;
        
        CompraModel compraModel = new CompraModel();
        compraModel.setMetodoPago(metodoPago);
        compraModel.setProveedor(proveedorService.getProveedorByNombreEmpresa(nombreProveedor));
        compraModel.setFechaHora(OffsetDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")));
        

        for (ItemCompraRequest item : items) {
            // Obtener el producto existente
            ProductModel producto = productService.getProductByNombre(item.nombreProducto());
            
            // Actualizar el stock sumando la cantidad comprada
            productService.actualizarStockPorNombre(item.nombreProducto(), item.cantidad(), "sumar");
            
            // Crear el detalle de compra
            DetalleCompraModel detalle = new DetalleCompraModel();
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(item.precioUnitario());
            detalle.setNombreProducto(item.nombreProducto());
            detalle.setTipoVenta(producto.getTipoVenta()); // Snapshot
            detalle.setProducto(producto);
            detalle.setCompra(compraModel);
            compraModel.getDetalles().add(detalle);
        }

        
        total = sumarTotal(compraModel.getDetalles());
        compraModel.setTotal(total);

        // Actualizar totalCompras y ultimaCompra del proveedor
        var proveedor = compraModel.getProveedor();
        proveedor.setTotalCompras(proveedor.getTotalCompras().add(total));
        proveedor.setUltimaCompra(compraModel.getFechaHora().toLocalDate());
        proveedorService.guardarProveedor(proveedor);

        // Primero guardar la compra para obtener el ID
        CompraModel savedCompra = compraRepository.save(compraModel);

        // Luego registrar la compra como un gasto vinculado
        gastoService.registrarGastoDeCompra(savedCompra);

        return savedCompra;
    }

    @Transactional
    public CompraModel registrarCompraPorProveedorId(List<ItemCompraRequest> items, MetodoPago metodoPago, Long proveedorId) {
        BigDecimal total;
        
        // Obtener el proveedor por ID
        var proveedor = proveedorService.getProveedorById(proveedorId);
        
        CompraModel compraModel = new CompraModel();
        compraModel.setMetodoPago(metodoPago);
        compraModel.setProveedor(proveedor);
        compraModel.setFechaHora(OffsetDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")));
        

        for (ItemCompraRequest item : items) {
            // Obtener el producto existente
            ProductModel producto = productService.getProductByNombre(item.nombreProducto());
            
            // Actualizar el stock sumando la cantidad comprada
            productService.actualizarStockPorNombre(item.nombreProducto(), item.cantidad(), "sumar");
            
            // Crear el detalle de compra
            DetalleCompraModel detalle = new DetalleCompraModel();
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(item.precioUnitario());
            detalle.setNombreProducto(item.nombreProducto());
            detalle.setTipoVenta(producto.getTipoVenta()); // Snapshot
            detalle.setProducto(producto); // Vincular el producto al detalle
            detalle.setCompra(compraModel);
            compraModel.getDetalles().add(detalle);
        }

        
        total = sumarTotal(compraModel.getDetalles());
        compraModel.setTotal(total);

        // Actualizar totalCompras y ultimaCompra del proveedor
        proveedor.setTotalCompras(proveedor.getTotalCompras().add(total));
        proveedor.setUltimaCompra(compraModel.getFechaHora().toLocalDate());
        proveedorService.guardarProveedor(proveedor);

        // Primero guardar la compra para obtener el ID
        CompraModel savedCompra = compraRepository.save(compraModel);

        // Luego registrar la compra como un gasto vinculado
        gastoService.registrarGastoDeCompra(savedCompra);

        return savedCompra;
    }

    public List<CompraResponse> getComprasPorEmpresa(String nombreEmpresa) {

        List<CompraModel> compras = compraRepository.findByProveedorNombreEmpresa(nombreEmpresa);

        if (compras.isEmpty()) {
            throw new NoFoundComprasProveedorException(nombreEmpresa);
        }

        return compras.stream()
                .map(compra -> new CompraResponse(
                        compra.getId(),
                        compra.getFechaHora(),
                        compra.getMetodoPago().name(),
                        compra.getProveedor().getNombreEmpresa(),
                        compra.getTotal(),
                        compra.getDetalles().stream()
                                .map(det -> new DetalleCompraResponse(
                                        det.getId(),
                                        det.getCantidad(),
                                        det.getPrecioUnitario(),
                                        det.getNombreProducto(),
                                        det.getProducto() != null ? det.getProducto().getId() : null,
                                        det.getProducto() != null ? det.getProducto().getTipoVenta() : det.getTipoVenta()
                                ))
                                .toList()
                ))
                .toList();
    }
}
