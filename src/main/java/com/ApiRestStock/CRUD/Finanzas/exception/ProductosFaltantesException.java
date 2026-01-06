package com.ApiRestStock.CRUD.Finanzas.exception;

import java.util.List;

/**
 * Excepción lanzada cuando se intenta registrar una compra pero algunos productos
 * no existen en la base de datos y requieren que se proporcione su SKU.
 */
public class ProductosFaltantesException extends RuntimeException {
    
    private final List<String> productosFaltantes;
    
    public ProductosFaltantesException(List<String> productosFaltantes) {
        super("Los siguientes productos no existen en la base de datos y requieren SKU: " + productosFaltantes);
        this.productosFaltantes = productosFaltantes;
    }
    
    public List<String> getProductosFaltantes() {
        return productosFaltantes;
    }
}
