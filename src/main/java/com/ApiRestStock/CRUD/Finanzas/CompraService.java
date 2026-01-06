package com.ApiRestStock.CRUD.Finanzas;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ApiRestStock.CRUD.Finanzas.enums.TipoGasto;
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
        compraModel.setProveedor(proveedorService.getProveedorByNombre(nombreProveedor));
        compraModel.setFechaHora(OffsetDateTime.now());
        

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


        return compraRepository.save(compraModel);
    }

}
