package com.ApiRestStock.CRUD.stock;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ApiRestStock.CRUD.stock.DTOs.EditProductRequest;
import com.ApiRestStock.CRUD.stock.DTOs.InventarioStatsResponse;

@RestController
@RequestMapping("/producto")
public class ProductController {

    @Autowired
    private ProductService productService;
    

    @GetMapping
    public List<ProductModel> getProductos(){
        return this.productService.getProductos();
    }

    /**
     * Endpoint con paginación
     * @param page Número de página (empieza en 1 para el cliente, internamente se convierte a 0-based)
     * @param size Cantidad de productos por página (default: 10)
     * @return Page con productos, información de paginación
     */
    @GetMapping("/paginated")
    public Page<ProductModel> getProductosPaginados(
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
        return this.productService.getProductosPaginados(page - 1, size);
    }

    /**
     * Obtiene estadísticas del inventario
     * @return Cantidad de productos con stock bajo (<=5) y valor total del inventario
     */
    @GetMapping("/stats")
    public InventarioStatsResponse getInventarioStats() {
        return this.productService.getInventarioStats();
    }

    /**
     * Busca productos por nombre o SKU (para campos input/autocomplete)
     * @param q término de búsqueda
     * @return Lista de productos que coinciden
     */
    @GetMapping("/search")
    public List<ProductModel> buscarProductos(@RequestParam String q) {
        return this.productService.buscarProductos(q);
    }

    @GetMapping("/id/{id:[0-9]+}")
    public ProductModel getProductoById(@PathVariable("id") Long id){
        return this.productService.getProductById(id);
    }
    
    @GetMapping("/sku/{sku}")
    public ProductModel getProductBySku(@PathVariable String sku) {

        try {
            return productService.getProductBySku(sku);
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Producto no encontrado con SKU: " + sku
            );
        }
}




    @PostMapping
    public ResponseEntity<ProductModel> saveProduct(@RequestBody ProductModel producto){
        ProductModel savedProduct = this.productService.saveProduct(producto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }


    @PutMapping("/editar/{id}")
    public ProductModel updateProductById(@RequestBody EditProductRequest producto, @PathVariable Long id) {
        return this.productService.updateById(producto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        if (productService.deleteProducto(id)) {
            return ResponseEntity.noContent().build(); // 204
        }
        return ResponseEntity.notFound().build(); // 404
    }




    

    
}
