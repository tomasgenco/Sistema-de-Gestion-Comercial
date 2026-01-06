package com.ApiRestStock.CRUD.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ApiRestStock.CRUD.Finanzas.DTOs.ProductosFaltantesResponse;
import com.ApiRestStock.CRUD.Finanzas.exception.CierreCajaDuplicadoException;
import com.ApiRestStock.CRUD.Finanzas.exception.ProductosFaltantesException;
import com.ApiRestStock.CRUD.shared.dto.error.ApiError;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(CierreCajaDuplicadoException.class)
    public ResponseEntity<ApiError> handleCierreCajaDuplicado(CierreCajaDuplicadoException ex) {

        ApiError apiError = new ApiError(
            HttpStatus.CONFLICT.value(),
            "CIERRE_CAJA_DUPLICADO",
            ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }
    
    @ExceptionHandler(ProductosFaltantesException.class)
    public ResponseEntity<ProductosFaltantesResponse> handleProductosFaltantes(ProductosFaltantesException ex) {
        ProductosFaltantesResponse response = new ProductosFaltantesResponse(
            "PRODUCTOS_FALTANTES",
            "Los siguientes productos no existen en la base de datos y requieren que proporcione su SKU (código de barras) antes de continuar:",
            ex.getProductosFaltantes()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
