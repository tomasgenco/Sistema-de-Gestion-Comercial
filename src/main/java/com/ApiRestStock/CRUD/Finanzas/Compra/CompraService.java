package com.ApiRestStock.CRUD.Finanzas.Compra;

import java.math.BigDecimal;
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
import org.springframework.transaction.annotation.Transactional;

import com.ApiRestStock.CRUD.Finanzas.Compra.DTOs.CompraResponse;
import com.ApiRestStock.CRUD.Finanzas.Compra.DTOs.DetalleCompraResponse;
import com.ApiRestStock.CRUD.Finanzas.enums.TipoGasto;
import com.ApiRestStock.CRUD.Finanzas.exception.NoFoundComprasProveedorException;
import com.ApiRestStock.CRUD.Finanzas.exception.ProductosFaltantesException;
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
    public Page<CompraModel> getComprasPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaHora").descending());
        return compraRepository.findAll(pageable);
    }

    /**
     * Filtra compras por término de búsqueda
     * @param searchTerm texto para buscar en nombre del proveedor
     * @param page número de página (base 0)
     * @param size tamaño de página
     * @return Page con compras filtradas
     */
    public Page<CompraModel> filtrarCompras(String searchTerm, int page, int size) {
        // Como la query ya tiene ORDER BY, usamos Pageable sin Sort
        Pageable pageable = PageRequest.of(page, size);
        return compraRepository.filtrarCompras(searchTerm, pageable);
    }



    public BigDecimal sumarTotal(List<DetalleCompraModel> detalles) {

        BigDecimal total = BigDecimal.ZERO;

        for (DetalleCompraModel detalle : detalles) {
            BigDecimal subtotal = detalle.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad()));

            total = total.add(subtotal);
        }

        return total;
    }




    /**
     * Valida que todos los productos de la compra existan en la base de datos.
     * Si algún producto no existe y no tiene SKU proporcionado, lanza una excepción
     * indicando qué productos necesitan SKU.
     * 
     * @param items Lista de items de la compra
     * @throws ProductosFaltantesException Si hay productos que no existen y no tienen SKU
     */
    private void validarProductos(List<ItemCompraRequest> items) {
        List<String> productosFaltantes = new ArrayList<>();
        
        for (ItemCompraRequest item : items) {
            // Verificar si el producto existe
            boolean existe = productService.existeProducto(item.nombreProducto(), item.sku());
            
            // Si no existe y no tiene SKU, agregarlo a la lista de productos faltantes
            if (!existe && (item.sku() == null || item.sku().trim().isEmpty())) {
                productosFaltantes.add(item.nombreProducto());
            }
        }
        
        // Si hay productos faltantes sin SKU, lanzar excepción
        if (!productosFaltantes.isEmpty()) {
            throw new ProductosFaltantesException(productosFaltantes);
        }
    }

    @Transactional
    public CompraModel registrarCompra(List<ItemCompraRequest> items, MetodoPago metodoPago, String nombreProveedor) {
        // Validar primero que todos los productos existan o tengan SKU proporcionado
        validarProductos(items);

        BigDecimal total;
        
        CompraModel compraModel = new CompraModel();
        compraModel.setMetodoPago(metodoPago);
        compraModel.setProveedor(proveedorService.getProveedorByNombreEmpresa(nombreProveedor));
        compraModel.setFechaHora(OffsetDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")));
        

        for (ItemCompraRequest item : items) {
            ProductModel producto;
            
            // Si el producto no existe, crearlo con el SKU proporcionado
            // Si existe, obtenerlo
            if (!productService.existeProducto(item.nombreProducto(), item.sku())) {
                // El producto no existe, crearlo con el SKU proporcionado
                // (ya validamos que tenga SKU en validarProductos)
                producto = productService.buscarOCrearProducto(
                    item.sku(),           // SKU (código de barras) proporcionado por el cliente
                    item.nombreProducto(),
                    item.precioUnitario(),
                    0 // Stock inicial 0, se sumará después
                );
            } else {
                // El producto existe, obtenerlo
                // Buscar primero por SKU si está disponible, sino por nombre
                if (item.sku() != null && !item.sku().trim().isEmpty()) {
                    producto = productService.getProductBySku(item.sku());
                } else {
                    producto = productService.getProductByNombre(item.nombreProducto());
                }
            }
            
            // Actualizar el stock sumando la cantidad comprada
            Integer stockActual = producto.getStock();
            producto.setStock(stockActual + item.cantidad());
            productService.saveProduct(producto);
            
            // Crear el detalle de compra
            DetalleCompraModel detalle = new DetalleCompraModel();
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(item.precioUnitario());
            detalle.setNombreProducto(item.nombreProducto());
            detalle.setProducto(producto); // Vincular el producto al detalle
            detalle.setCompra(compraModel);
            compraModel.getDetalles().add(detalle);
        }

        
        total = sumarTotal(compraModel.getDetalles());
        compraModel.setTotal(total);

        //Registra la compra como un gasto en el sistema financiero
        gastoService.registrarGasto(total, TipoGasto.PROVEEDOR, nombreProveedor);

        // Actualizar totalCompras y ultimaCompra del proveedor
        var proveedor = compraModel.getProveedor();
        proveedor.setTotalCompras(proveedor.getTotalCompras().add(total));
        proveedor.setUltimaCompra(compraModel.getFechaHora().toLocalDate());
        proveedorService.guardarProveedor(proveedor);

        return compraRepository.save(compraModel);
    }

    @Transactional
    public CompraModel registrarCompraPorProveedorId(List<ItemCompraRequest> items, MetodoPago metodoPago, Long proveedorId) {
        // Validar primero que todos los productos existan o tengan SKU proporcionado
        validarProductos(items);

        BigDecimal total;
        
        // Obtener el proveedor por ID
        var proveedor = proveedorService.getProveedorById(proveedorId);
        
        CompraModel compraModel = new CompraModel();
        compraModel.setMetodoPago(metodoPago);
        compraModel.setProveedor(proveedor);
        compraModel.setFechaHora(OffsetDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")));
        

        for (ItemCompraRequest item : items) {
            ProductModel producto;
            
            // Si el producto no existe, crearlo con el SKU proporcionado
            // Si existe, obtenerlo
            if (!productService.existeProducto(item.nombreProducto(), item.sku())) {
                // El producto no existe, crearlo con el SKU proporcionado
                // (ya validamos que tenga SKU en validarProductos)
                producto = productService.buscarOCrearProducto(
                    item.sku(),           // SKU (código de barras) proporcionado por el cliente
                    item.nombreProducto(),
                    item.precioUnitario(),
                    0 // Stock inicial 0, se sumará después
                );
            } else {
                // El producto existe, obtenerlo
                // Buscar primero por SKU si está disponible, sino por nombre
                if (item.sku() != null && !item.sku().trim().isEmpty()) {
                    producto = productService.getProductBySku(item.sku());
                } else {
                    producto = productService.getProductByNombre(item.nombreProducto());
                }
            }
            
            // Actualizar el stock sumando la cantidad comprada
            Integer stockActual = producto.getStock();
            producto.setStock(stockActual + item.cantidad());
            productService.saveProduct(producto);
            
            // Crear el detalle de compra
            DetalleCompraModel detalle = new DetalleCompraModel();
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(item.precioUnitario());
            detalle.setNombreProducto(item.nombreProducto());
            detalle.setProducto(producto); // Vincular el producto al detalle
            detalle.setCompra(compraModel);
            compraModel.getDetalles().add(detalle);
        }

        
        total = sumarTotal(compraModel.getDetalles());
        compraModel.setTotal(total);

        //Registra la compra como un gasto en el sistema financiero
        gastoService.registrarGasto(total, TipoGasto.PROVEEDOR, proveedor.getNombreEmpresa());

        // Actualizar totalCompras y ultimaCompra del proveedor
        proveedor.setTotalCompras(proveedor.getTotalCompras().add(total));
        proveedor.setUltimaCompra(compraModel.getFechaHora().toLocalDate());
        proveedorService.guardarProveedor(proveedor);

        return compraRepository.save(compraModel);
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
                                        det.getProducto().getId()
                                ))
                                .toList()
                ))
                .toList();
    }
}
