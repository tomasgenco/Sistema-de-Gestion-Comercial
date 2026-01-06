package com.ApiRestStock.CRUD.stock;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    @Autowired
    IProductRepository productRepository;

    public List<ProductModel> getProductos(){
        return  this.productRepository.findAll();
    }

    public ProductModel saveProduct(ProductModel producto){
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

    
    public ProductModel updateById(ProductModel producto, Long id){
        ProductModel findProduct = this.productRepository.findById(id).get();

        findProduct.setNombre( producto.getNombre());
        findProduct.setPrecio( producto.getPrecio());
        findProduct.setSku( producto.getSku());
        findProduct.setStock( producto.getStock());

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
    public ProductModel buscarOCrearProducto(String sku, String nombreProducto, BigDecimal precioUnitario, Integer cantidadInicial) {
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
        nuevoProducto.setPrecio(precioUnitario);
        nuevoProducto.setSku(sku);
        nuevoProducto.setStock(cantidadInicial != null ? cantidadInicial : 0);
        
        return productRepository.save(nuevoProducto);
    }

    
}
