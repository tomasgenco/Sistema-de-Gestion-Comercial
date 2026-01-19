package com.ApiRestStock.CRUD.proveedor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<ProveedorModel, Long> {

    // Buscar proveedor por nombre de empresa
    Optional<ProveedorModel> findByNombreEmpresa(String nombreEmpresa);

    // Verificar si existe un proveedor por nombre de empresa
    boolean existsByNombreEmpresa(String nombreEmpresa);
    
    // Buscar proveedor por CUIT
    Optional<ProveedorModel> findByCuit(String cuit);

    // Listar proveedores activos
    List<ProveedorModel> findByActivo(Boolean activo);

    // Contar proveedores activos
    Long countByActivo(Boolean activo);

    // Calcular el total gastado sumando totalCompras de todos los proveedores
    @Query("SELECT COALESCE(SUM(p.totalCompras), 0) FROM ProveedorModel p")
    BigDecimal calcularTotalGastado();

    /**
     * Busca proveedores por nombre de empresa, persona de contacto, email o CUIT
     * y opcionalmente filtra por estado activo/inactivo
     * @param searchTerm término de búsqueda
     * @param activo filtro de estado (null = todos, true = activos, false = inactivos)
     * @param pageable información de paginación
     * @return Page de proveedores que coinciden
     */
    @Query("SELECT p FROM ProveedorModel p WHERE " +
           "(:searchTerm IS NULL OR :searchTerm = '' OR " +
           "LOWER(p.nombreEmpresa) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.personaContacto) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.cuit) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:activo IS NULL OR p.activo = :activo)")
    Page<ProveedorModel> filtrarProveedores(
        @Param("searchTerm") String searchTerm,
        @Param("activo") Boolean activo,
        Pageable pageable
    );
}
