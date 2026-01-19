package com.ApiRestStock.CRUD.Finanzas.Compra;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CompraRepository extends JpaRepository<CompraModel, Long> {
    List<CompraModel> findByProveedorNombreEmpresa(String proveedor);

    /**
     * Busca compras por nombre de proveedor
     * @param searchTerm término de búsqueda para el nombre del proveedor
     * @param pageable información de paginación
     * @return Page de compras que coinciden
     */
    @Query(value = "SELECT c.* FROM compra c " +
           "JOIN proveedor p ON c.proveedor_id = p.proveedor_id " +
           "WHERE (:searchTerm IS NULL OR :searchTerm = '' OR LOWER(p.nombre_empresa) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY c.fecha_hora DESC",
           countQuery = "SELECT COUNT(*) FROM compra c " +
           "JOIN proveedor p ON c.proveedor_id = p.proveedor_id " +
           "WHERE (:searchTerm IS NULL OR :searchTerm = '' OR LOWER(p.nombre_empresa) LIKE LOWER(CONCAT('%', :searchTerm, '%')))",
           nativeQuery = true)
    Page<CompraModel> filtrarCompras(
        @Param("searchTerm") String searchTerm,
        Pageable pageable
    );
}
