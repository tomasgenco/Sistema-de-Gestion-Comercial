package com.ApiRestStock.CRUD.Finanzas.exception;

public class NoFoundComprasProveedorException extends RuntimeException {
    private final String nombreProveedor;
    private final String codeError = "NO_FOUND_COMPRAS_PROVEEDOR";

    public NoFoundComprasProveedorException(String nombreProveedor) {
        super("No se encontraron compras para el proveedor: " + nombreProveedor);
        this.nombreProveedor = nombreProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public String getCodeError() {
        return codeError;
    }

}


