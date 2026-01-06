package com.ApiRestStock.CRUD.Finanzas.DTOs;

import java.util.List;

/**
 * Respuesta cuando se intenta registrar una compra pero faltan productos con SKU.
 * Indica al cliente qué productos necesita cargar con su SKU antes de continuar.
 */
public class ProductosFaltantesResponse {
    
    private String errorCode;
    private String mensaje;
    private List<String> productosFaltantes;
    
    public ProductosFaltantesResponse() {
    }
    
    public ProductosFaltantesResponse(String errorCode, String mensaje, List<String> productosFaltantes) {
        this.errorCode = errorCode;
        this.mensaje = mensaje;
        this.productosFaltantes = productosFaltantes;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public List<String> getProductosFaltantes() {
        return productosFaltantes;
    }
    
    public void setProductosFaltantes(List<String> productosFaltantes) {
        this.productosFaltantes = productosFaltantes;
    }
}
