package com.ApiRestStock.CRUD.proveedor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ApiRestStock.CRUD.proveedor.DTOs.ProveedorRequest;
import com.ApiRestStock.CRUD.proveedor.DTOs.ProveedorResponse;
import com.ApiRestStock.CRUD.proveedor.DTOs.ProveedorStatsResponse;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

    @Autowired
    ProveedorService proveedorService;
    
    /**
     * Endpoint con paginación ordenado por nombre de empresa
     * @param page Número de página (empieza en 1 para el cliente, internamente se convierte a 0-based)
     * @param size Cantidad de proveedores por página (default: 10)
     * @return Page con proveedores, información de paginación
     */
    @GetMapping
    public Page<ProveedorResponse> obtenerTodosLosProveedores(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Validar que page sea al menos 1
        if (page < 1) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El número de página debe ser mayor o igual a 1"
            );
        }
        // Validar que size sea positivo
        if (size < 1 || size > 100) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El tamaño de página debe estar entre 1 y 100"
            );
        }
        // Convertir de 1-based (cliente) a 0-based (Spring Data)
        return proveedorService.getProveedoresPaginados(page - 1, size);
    }

    /**
     * Obtiene estadísticas de proveedores
     * @return Total de proveedores, cantidad activos y total gastado
     */
    @GetMapping("/stats")
    public ProveedorStatsResponse getProveedorStats() {
        return proveedorService.getProveedorStats();
    }

    /**
     * Filtra proveedores por término de búsqueda y estado
     * @param q término de búsqueda (nombre, contacto, email o CUIT)
     * @param activo filtro de estado (true = activos, false = inactivos, null = todos)
     * @param page número de página (empieza en 1)
     * @param size cantidad por página (default: 10)
     * @return Page con proveedores filtrados
     */
    @GetMapping("/filtrar")
    public Page<ProveedorResponse> filtrarProveedores(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Validar que page sea al menos 1
        if (page < 1) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El número de página debe ser mayor o igual a 1"
            );
        }
        // Validar que size sea positivo
        if (size < 1 || size > 100) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El tamaño de página debe estar entre 1 y 100"
            );
        }
        // Convertir de 1-based (cliente) a 0-based (Spring Data)
        return proveedorService.filtrarProveedores(q, activo, page - 1, size);
    }
    
    @PostMapping
    public ProveedorModel crearProveedor(@RequestBody ProveedorRequest proveedorRequest) {
        ProveedorModel proveedor = new ProveedorModel();
        proveedor.setNombreEmpresa(proveedorRequest.nombreEmpresa());
        proveedor.setCuit(proveedorRequest.cuit());
        proveedor.setPersonaContacto(proveedorRequest.personaContacto());
        proveedor.setEmail(proveedorRequest.email());
        proveedor.setTelefono(proveedorRequest.telefono());
        proveedor.setDireccion(proveedorRequest.direccion());
        proveedor.setActivo(proveedorRequest.activo() != null ? proveedorRequest.activo() : true);
        return proveedorService.guardarProveedor(proveedor);
    }

    @GetMapping("/nombre/{nombre}")
    public ProveedorModel obtenerProveedorPorNombre(@PathVariable String nombre) {
        return proveedorService.getProveedorByNombreEmpresa(nombre);
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

    @PutMapping("/{id}")
    public ProveedorResponse actualizarProveedor(@PathVariable Long id, @RequestBody ProveedorRequest proveedorRequest) {
        ProveedorModel datosActualizados = new ProveedorModel();
        datosActualizados.setNombreEmpresa(proveedorRequest.nombreEmpresa());
        datosActualizados.setCuit(proveedorRequest.cuit());
        datosActualizados.setPersonaContacto(proveedorRequest.personaContacto());
        datosActualizados.setEmail(proveedorRequest.email());
        datosActualizados.setTelefono(proveedorRequest.telefono());
        datosActualizados.setDireccion(proveedorRequest.direccion());
        datosActualizados.setActivo(proveedorRequest.activo());
        return proveedorService.actualizarProveedor(id, datosActualizados);
    }

}
