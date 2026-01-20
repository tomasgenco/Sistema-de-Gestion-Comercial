package com.ApiRestStock.CRUD.stock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductRepository extends JpaRepository<ProductModel, Long>{

    Optional<ProductModel> findBySku(String sku);

    Optional<ProductModel> findByNombre(String nombre);

    /**
     * Cuenta la cantidad de productos con stock bajo (1 <= stock <= limiteStockBajo, excluye stock 0)
     * @param limiteStockBajo El valor máximo para considerar stock bajo
     */
    @Query("SELECT COUNT(p) FROM ProductModel p WHERE p.stock BETWEEN 1 AND :limite")
    Long countProductosConStockBajo(@Param("limite") Integer limite);

    /**
     * Cuenta la cantidad de productos sin stock (stock = 0)
     */
    @Query("SELECT COUNT(p) FROM ProductModel p WHERE p.stock = 0")
    Long countProductosSinStock();

    /**
     * Calcula el valor total del inventario (suma de precio_venta * stock)
     */
    @Query("SELECT COALESCE(SUM(p.precioVenta * p.stock), 0) FROM ProductModel p")
    BigDecimal calcularValorTotalInventario();

    /**
     * Busca productos por nombre o SKU (búsqueda parcial case-insensitive)
     * @param searchTerm término de búsqueda
     * @return Lista de productos que coinciden
     */
    @Query("SELECT p FROM ProductModel p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<ProductModel> buscarPorNombreOSku(@Param("searchTerm") String searchTerm);
    
}
