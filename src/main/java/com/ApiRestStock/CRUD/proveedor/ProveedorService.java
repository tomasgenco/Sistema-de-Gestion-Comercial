package com.ApiRestStock.CRUD.proveedor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ApiRestStock.CRUD.proveedor.DTOs.ProveedorResponse;
import com.ApiRestStock.CRUD.proveedor.DTOs.ProveedorStatsResponse;

@Service
public class ProveedorService {

    @Autowired
    ProveedorRepository proveedorRepository;


    public ProveedorModel guardarProveedor(ProveedorModel proveedor) {
        if (proveedor.getActivo() == null) {
            proveedor.setActivo(true);
        }
        return proveedorRepository.save(proveedor);
    }

    public ProveedorModel getProveedorByNombreEmpresa(String nombreEmpresa) {
        ProveedorModel proveedor = proveedorRepository.findByNombreEmpresa(nombreEmpresa)
            .orElseThrow(() -> new RuntimeException("No se encontro el proveedor con el Nombre: " + nombreEmpresa));
        return proveedor;
    }

    public ProveedorModel getProveedorById(Long id) {
        return proveedorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontro el proveedor con el ID: " + id));
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

    public ProveedorResponse actualizarProveedor(Long id, ProveedorModel datosActualizados) {
        ProveedorModel proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro el proveedor con el ID: " + id));
        
        // Actualizar solo los campos proporcionados
        if (datosActualizados.getNombreEmpresa() != null) {
            proveedor.setNombreEmpresa(datosActualizados.getNombreEmpresa());
        }
        if (datosActualizados.getCuit() != null) {
            proveedor.setCuit(datosActualizados.getCuit());
        }
        if (datosActualizados.getPersonaContacto() != null) {
            proveedor.setPersonaContacto(datosActualizados.getPersonaContacto());
        }
        if (datosActualizados.getEmail() != null) {
            proveedor.setEmail(datosActualizados.getEmail());
        }
        if (datosActualizados.getTelefono() != null) {
            proveedor.setTelefono(datosActualizados.getTelefono());
        }
        if (datosActualizados.getDireccion() != null) {
            proveedor.setDireccion(datosActualizados.getDireccion());
        }
        if (datosActualizados.getActivo() != null) {
            proveedor.setActivo(datosActualizados.getActivo());
        }
        
        ProveedorModel proveedorActualizado = proveedorRepository.save(proveedor);
        return convertirADTO(proveedorActualizado);
    }

    public List<ProveedorResponse> getAllProveedores() {
        return proveedorRepository.findAll().stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene proveedores paginados ordenados por nombre de empresa
     * @param page Número de página (base 0)
     * @param size Tamaño de página
     * @return Page con proveedores e información de paginación
     */
    public Page<ProveedorResponse> getProveedoresPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombreEmpresa").ascending());
        return proveedorRepository.findAll(pageable).map(this::convertirADTO);
    }

    /**
     * Obtiene estadísticas de proveedores: total, activos y total gastado
     * @return ProveedorStatsResponse con las estadísticas
     */
    public ProveedorStatsResponse getProveedorStats() {
        Long totalProveedores = proveedorRepository.count();
        Long proveedoresActivos = proveedorRepository.countByActivo(true);
        java.math.BigDecimal totalGastado = proveedorRepository.calcularTotalGastado();
        
        return new ProveedorStatsResponse(
            totalProveedores,
            proveedoresActivos,
            totalGastado
        );
    }

    /**
     * Filtra proveedores por término de búsqueda y estado activo
     * @param searchTerm texto para buscar en nombre, contacto, email o CUIT
     * @param activo filtro de estado (null = todos, true = activos, false = inactivos)
     * @param page número de página (base 0)
     * @param size tamaño de página
     * @return Page con proveedores filtrados
     */
    public Page<ProveedorResponse> filtrarProveedores(String searchTerm, Boolean activo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombreEmpresa").ascending());
        return proveedorRepository.filtrarProveedores(searchTerm, activo, pageable)
            .map(this::convertirADTO);
    }

    private ProveedorResponse convertirADTO(ProveedorModel proveedor) {
        return new ProveedorResponse(
            proveedor.getId(),
            proveedor.getCuit(),
            proveedor.getActivo(),
            proveedor.getNombreEmpresa(),
            proveedor.getPersonaContacto(),
            proveedor.getEmail(),
            proveedor.getTelefono(),
            proveedor.getDireccion(),
            proveedor.getTotalCompras(),
            proveedor.getUltimaCompra()
        );
    }

}
