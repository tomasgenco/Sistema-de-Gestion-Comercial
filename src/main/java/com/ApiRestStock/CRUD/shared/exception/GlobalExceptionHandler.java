package com.ApiRestStock.CRUD.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ApiRestStock.CRUD.Finanzas.DTOs.ProductosFaltantesResponse;
import com.ApiRestStock.CRUD.Finanzas.exception.CierreCajaDuplicadoException;
import com.ApiRestStock.CRUD.Finanzas.exception.CierreMesDuplicadoException;
import com.ApiRestStock.CRUD.Finanzas.exception.NoFoundComprasProveedorException;
import com.ApiRestStock.CRUD.Finanzas.exception.ProductosFaltantesException;
import com.ApiRestStock.CRUD.shared.dto.error.ApiError;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(CierreCajaDuplicadoException.class)
    public ResponseEntity<ApiError> handleCierreCajaDuplicado(CierreCajaDuplicadoException ex) {

        ApiError apiError = new ApiError(
            HttpStatus.CONFLICT.value(), // código HTTP 409
            "CIERRE_CAJA_DUPLICADO",
            ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(CierreMesDuplicadoException.class)
    public ResponseEntity<ApiError> handleCierreMesDuplicado(CierreMesDuplicadoException ex) {

        ApiError apiError = new ApiError(
            HttpStatus.CONFLICT.value(), // código HTTP 409
            "CIERRE_MES_DUPLICADO",
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

    // Handler genérico para RuntimeException (para debug)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException ex) {
        ApiError error = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_ERROR",
            ex.getMessage()
        );
        
        // Log para debug
        System.err.println("RuntimeException no manejada: " + ex.getClass().getName() + " - " + ex.getMessage());
        ex.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}