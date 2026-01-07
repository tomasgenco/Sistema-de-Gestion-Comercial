package com.ApiRestStock.CRUD.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ApiRestStock.CRUD.Finanzas.DTOs.ProductosFaltantesResponse;
import com.ApiRestStock.CRUD.Finanzas.exception.CierreCajaDuplicadoException;
import com.ApiRestStock.CRUD.Finanzas.exception.NoFoundComprasProveedorException;
import com.ApiRestStock.CRUD.Finanzas.exception.ProductosFaltantesException;
import com.ApiRestStock.CRUD.shared.dto.error.ApiError;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(CierreCajaDuplicadoException.class)
    public ResponseEntity<ApiError> handleCierreCajaDuplicado(CierreCajaDuplicadoException ex) {

        ApiError apiError = new ApiError(
            HttpStatus.CONFLICT.value(), // código HTTP 404
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

    @ExceptionHandler(NoFoundComprasProveedorException.class)
    public ResponseEntity<ApiError> handleNoFoundComprasProveedor(
            NoFoundComprasProveedorException ex,
            HttpServletRequest request
    ) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),                  // código HTTP 404
                "COMPRAS_PROVEEDOR_NOT_FOUND",                  // código de error para el frontend
                ex.getMessage()                       // "No se encontraron compras..."
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

}