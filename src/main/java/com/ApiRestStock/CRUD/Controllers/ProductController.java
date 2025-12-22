package com.ApiRestStock.CRUD.Controllers;

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

import com.ApiRestStock.CRUD.Models.ProductModel;
import com.ApiRestStock.CRUD.Services.ProductService;

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

    @GetMapping("/{sku}")
    public ProductModel getProductBySku(@PathVariable String sku) {
        return this.productService.getProductBySku(sku);
    }
    

    
}
