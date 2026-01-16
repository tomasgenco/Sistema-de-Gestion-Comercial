package com.ApiRestStock.CRUD.stock;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/producto")
public class ProductController {

    @Autowired
    private ProductService productService;
    

    @GetMapping
    public List<ProductModel> getProductos(){
        return this.productService.getProductos();
    }

    @GetMapping("/{id}")
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


    @PutMapping("/{id}")
    public ProductModel updateProductById(@RequestBody ProductModel producto, @PathVariable Long id) {
        return this.productService.updateById(producto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        if (productService.deleteProducto(id)) {
            return ResponseEntity.noContent().build(); // 204
        }
        return ResponseEntity.notFound().build(); // 404
    }


    @GetMapping("{nombre}/bajo-stock")
    public ResponseEntity<Boolean> estaBajoStock(@PathVariable String nombre) {
        return ResponseEntity.ok(productService.productoEstaBajoStockPorNombre(nombre));
    }


    

    
}
