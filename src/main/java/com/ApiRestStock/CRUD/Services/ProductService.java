package com.ApiRestStock.CRUD.Services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ApiRestStock.CRUD.Models.ProductModel;
import com.ApiRestStock.CRUD.Repositories.IProductRepository;

@Service
public class ProductService {

    @Autowired
    IProductRepository productRepository;

    public ArrayList<ProductModel> getProductos(){
        return (ArrayList<ProductModel>) productRepository.findAll();
    }

    public ProductModel saveProduct(ProductModel producto){
        return productRepository.save(producto);
    }

    public ProductModel getProductById(Long id){
        return productRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("No se encontro el producto con el Id: " + id));
    }

    public ProductModel updateById(ProductModel producto, Long id){
        ProductModel findProduct = this.productRepository.findById(id).get();

        findProduct.setNombre( producto.getNombre());
        findProduct.setPrecio( producto.getPrecio());
        findProduct.setProvedor( producto.getProvedor());

        return productRepository.save(findProduct);
    }

    public Boolean deleteProducto(Long id){
        try {
            this.productRepository.deleteById(id);
            return true;
        } catch(Exception e){
            return false;
        }
    }
    
}
