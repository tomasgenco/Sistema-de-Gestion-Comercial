package com.ApiRestStock.CRUD.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ApiRestStock.CRUD.Finanzas.exception.CierreCajaDuplicadoException;
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
}
