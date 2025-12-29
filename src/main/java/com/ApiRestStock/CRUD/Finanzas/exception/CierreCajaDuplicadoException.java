package com.ApiRestStock.CRUD.Finanzas.exception;

import java.time.LocalDate;

public class CierreCajaDuplicadoException extends RuntimeException {
    public CierreCajaDuplicadoException(LocalDate fecha) {
        super("Ya existe un cierre de caja para la fecha: " + fecha);
    }
    
}
