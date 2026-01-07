package com.ApiRestStock.CRUD.proveedor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ApiRestStock.CRUD.proveedor.DTOs.ProveedorRequest;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

    @Autowired
    ProveedorService proveedorService;
    
    @PostMapping
    public ProveedorModel crearProveedor(@RequestBody ProveedorRequest proveedorRequest) {
        return proveedorService.guardarProveedor(proveedorRequest.nombre(), proveedorRequest.cuit());
    }

    @GetMapping("/nombre/{nombre}")
    public ProveedorModel obtenerProveedorPorNombre(@PathVariable String nombre) {
        return proveedorService.getProveedorByNombre(nombre);
    }

    @GetMapping("/activo")
    public List<ProveedorModel> obtenerTodosLosProveedores(@RequestParam(required = false) String filtro) {
        return proveedorService.getAllProveedoresActivos();
    }

    @PutMapping("/cambiar-estado/{id}")
    public ResponseEntity<Void> cambiarEstadoProveedor(@PathVariable Long id, @RequestParam Boolean estado) {
        proveedorService.cambiarEstadoProveedor(id, estado);
        return ResponseEntity.ok().build();
    }

}
