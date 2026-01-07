package com.ApiRestStock.CRUD.proveedor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProveedorService {

    @Autowired
    ProveedorRepository proveedorRepository;


    public ProveedorModel guardarProveedor(String nombreProvedor, String cuitProveedor) {
        ProveedorModel proveedor = new ProveedorModel();
        proveedor.setNombre(nombreProvedor);
        proveedor.setCuit(cuitProveedor);
        proveedor.setActivo(true);
        return proveedorRepository.save(proveedor);
    }

    public ProveedorModel getProveedorByNombre(String nombre) {
        ProveedorModel proveedor = proveedorRepository.findByNombre(nombre).orElseThrow(() -> new RuntimeException("No se encontro el proveedor con el Nombre: " + nombre));
        return proveedor;
    }

    public List<ProveedorModel> getAllProveedoresActivos() {
        return proveedorRepository.findByActivo(true);
    }

    public void cambiarEstadoProveedor(Long id, Boolean estado) {
        ProveedorModel proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro el proveedor con el ID: " + id));
        proveedor.setActivo(estado);
        proveedorRepository.save(proveedor);
    }

    

}
