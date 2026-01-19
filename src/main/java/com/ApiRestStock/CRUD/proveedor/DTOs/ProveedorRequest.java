package com.ApiRestStock.CRUD.proveedor.DTOs;


public record ProveedorRequest(
    String nombreEmpresa,
    
    String cuit,
    
    String personaContacto,
    
    String email,
    
    String telefono,
    
    String direccion,
    
    Boolean activo
) {
}
