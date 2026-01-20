package com.ApiRestStock.CRUD.stock;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ApiRestStock.CRUD.stock.DTOs.CreateProductRequest;
import com.ApiRestStock.CRUD.stock.DTOs.EditProductRequest;
import com.ApiRestStock.CRUD.stock.DTOs.InventarioStatsResponse;

@Service
public class ProductService {

    // Constante para definir el límite de stock bajo (puedes cambiar este valor según necesites)
    private static  Integer LIMITE_STOCK_BAJO = 5;

    @Autowired
    IProductRepository productRepository;

    public List<ProductModel> getProductos(){
        return  this.productRepository.findAll();
    }

    /**
     * Obtiene productos paginados
     * @param page Número de página (base 0)
     * @param size Tamaño de página
     * @return Page con productos e información de paginación
     */
    public Page<ProductModel> getProductosPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        return this.productRepository.findAll(pageable);
    }

    /**
     * Obtiene estadísticas del inventario: productos con stock bajo y valor total
     * @return InventarioStatsResponse con cantidad de productos con stock bajo y valor total
     */
    public InventarioStatsResponse getInventarioStats() {
        Long totalProductos = productRepository.count();
        Long productosStockBajo = productRepository.countProductosConStockBajo(LIMITE_STOCK_BAJO);
        Long productosSinStock = productRepository.countProductosSinStock();
        BigDecimal valorTotal = productRepository.calcularValorTotalInventario();
        
        return new InventarioStatsResponse(totalProductos, productosStockBajo, productosSinStock, valorTotal);
    }

    /**
     * Busca productos por nombre o SKU (búsqueda parcial)
     * @param searchTerm término de búsqueda
     * @return Lista de productos que coinciden
     */
    public List<ProductModel> buscarProductos(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        return productRepository.buscarPorNombreOSku(searchTerm.trim());
    }

    public ProductModel saveProduct(ProductModel request){
        ProductModel producto = new ProductModel();
        producto.setNombre(request.getNombre());
        producto.setSku(request.getSku());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setPrecioCompra(request.getPrecioCompra());
        producto.setStock(request.getStock() != null && request.getStock() >= 0 ? request.getStock() : 0);
        
        return productRepository.save(producto);
    }

    public ProductModel getProductById(Long id){
        return productRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("No se encontro el producto con el Id: " + id));
    }

    public ProductModel getProductBySku(String sku){
        ProductModel product = productRepository.findBySku(sku).get();

        if (product == null) {
            throw new RuntimeException("No se encontro el producto con el SKU: " + sku);
        } else{
            return product;
        }
    }

    public ProductModel getProductByNombre(String nombre){
        ProductModel product = productRepository.findByNombre(nombre).get();

        if (product == null) {
            throw new RuntimeException("No se encontro el producto con el Nombre: " + nombre);
        } else {
            return product;
        }
    }

    public void actualizarStockPorNombre(String nombreProducto, Integer cantidadVendida, String operacion){
        ProductModel producto = getProductByNombre(nombreProducto);
        Integer stockActual = producto.getStock();

        if (operacion.toLowerCase().equals("sumar")) {
            producto.setStock(stockActual + cantidadVendida);
        } else if (operacion.toLowerCase().equals("restar")) {
            producto.setStock(stockActual - cantidadVendida);
        } else {
            throw new RuntimeException("Operacion no valida. Use 'sumar' o 'restar'.");
        }

        productRepository.save(producto);
    }

    
    public ProductModel updateById(EditProductRequest producto, Long id){
        ProductModel findProduct = this.productRepository.findById(id).get();

        findProduct.setNombre(producto.nombre());
        findProduct.setPrecioVenta(producto.precioVenta());
        findProduct.setPrecioCompra(producto.precioCompra());

        return productRepository.save(findProduct);
    }

    public Boolean deleteProducto(Long id) {
    try {
        if (!productRepository.existsById(id)) {
            return false; // No existe el producto
        }

        productRepository.deleteById(id);
        return true;

    } catch (Exception e) {
        return false;
    }
}

    /**
     * Verifica si un producto existe en la base de datos por nombre o SKU.
     * 
     * @param nombreProducto Nombre del producto
     * @param sku SKU del producto (puede ser null)
     * @return true si el producto existe, false en caso contrario
     */
    public boolean existeProducto(String nombreProducto, String sku) {
        // Si se proporciona SKU, buscar por SKU primero
        if (sku != null && !sku.trim().isEmpty()) {
            Optional<ProductModel> productoPorSku = productRepository.findBySku(sku);
            if (productoPorSku.isPresent()) {
                return true;
            }
        }
        
        // Buscar por nombre
        Optional<ProductModel> productoPorNombre = productRepository.findByNombre(nombreProducto);
        return productoPorNombre.isPresent();
    }

    /**
     * Busca un producto por SKU (código de barras) o por nombre. Si no existe, lo crea automáticamente.
     * Esto permite que las compras puedan registrar productos nuevos sin necesidad
     * de crearlos manualmente primero.
     * 
     * Prioriza la búsqueda por SKU ya que es más confiable y único.
     * 
     * @param sku Código de barras del producto (SKU)
     * @param nombreProducto Nombre del producto
     * @param precioUnitario Precio unitario (se usará como precio inicial del producto)
     * @param cantidadInicial Cantidad inicial de stock (normalmente 0, se sumará después)
     * @return ProductModel existente o recién creado
     */
    @Transactional
    public ProductModel buscarOCrearProducto(String sku, String nombreProducto, BigDecimal precioVenta, BigDecimal precioCompra, Integer cantidadInicial) {
        // Primero buscar por SKU (más confiable que por nombre)
        Optional<ProductModel> productoPorSku = productRepository.findBySku(sku);
        if (productoPorSku.isPresent()) {
            return productoPorSku.get();
        }
        
        // Si no existe por SKU, buscar por nombre (por si acaso el SKU cambió pero el nombre es el mismo)
        Optional<ProductModel> productoPorNombre = productRepository.findByNombre(nombreProducto);
        if (productoPorNombre.isPresent()) {
            return productoPorNombre.get();
        }
        
        // Si no existe ni por SKU ni por nombre, crear uno nuevo con el SKU proporcionado
        ProductModel nuevoProducto = new ProductModel();
        nuevoProducto.setNombre(nombreProducto);
        nuevoProducto.setPrecioVenta(precioVenta);
        nuevoProducto.setPrecioCompra(precioCompra);
        nuevoProducto.setSku(sku);
        nuevoProducto.setStock(cantidadInicial != null ? cantidadInicial : 0);
        
        return productRepository.save(nuevoProducto);
    }

    
}
