package com.ApiRestStock.CRUD.proveedor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ApiRestStock.CRUD.proveedor.DTOs.ProveedorRequest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/proveedores")
public class ProveedorController {


    @Autowired
    ProveedorService proveedorService;
    
    @PostMapping
    public ProveedorModel crearProveedor(@RequestBody ProveedorRequest proveedorRequest) {
        return proveedorService.guardarProveedor(proveedorRequest.nombre(), proveedorRequest.cuit());
    }

    @GetMapping("/nombre")
    public ProveedorModel obtenerProveedorPorNombre(@PathVariable String nombre) {
        return proveedorService.getProveedorByNombre(nombre);
    }

    @GetMapping
    public List<ProveedorModel> obtenerTodosLosProveedores() {
        return proveedorService.getAllProveedores();
    }
}

    

