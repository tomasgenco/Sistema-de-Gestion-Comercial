package com.ApiRestStock.CRUD.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    public ProductModel saveProduct(@RequestBody ProductModel producto){
        return this.productService.saveProduct(producto);
    }


    @PutMapping("/{id}")
    public ProductModel updateProductById(@RequestBody ProductModel producto, @PathVariable("id") Long id){
        return this.productService.updateById(producto, id);
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable("id") Long id){
        boolean ok = this.productService.deleteProducto(id);

            if(ok){
                return ("El usuario con el id: " + id + " fue borrado correctamente!");
            } else {
                return "Error, el usuario con el id: " + id + "no se pudo borrar";
            }    }
    
}
