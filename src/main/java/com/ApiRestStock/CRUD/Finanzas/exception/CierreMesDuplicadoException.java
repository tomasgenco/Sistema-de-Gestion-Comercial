package com.ApiRestStock.CRUD.Finanzas.exception;

public class CierreMesDuplicadoException extends RuntimeException {
    public CierreMesDuplicadoException(int mes, int anio) {
        super("Ya existe un cierre de mes para " + mes + "/" + anio);
    }
}
