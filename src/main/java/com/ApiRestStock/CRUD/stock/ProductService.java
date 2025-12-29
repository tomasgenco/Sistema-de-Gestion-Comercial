package com.ApiRestStock.CRUD.stock;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    //Pensar bien pasa con lo que no se quiere actualizar del producto
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

    
}
