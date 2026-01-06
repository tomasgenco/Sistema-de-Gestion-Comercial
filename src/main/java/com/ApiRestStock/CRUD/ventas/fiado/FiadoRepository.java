package com.ApiRestStock.CRUD.ventas.fiado;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ApiRestStock.CRUD.ventas.enums.EstadoFiado;

@Repository
public interface FiadoRepository extends JpaRepository<FiadoModel, Long> {

    // Obtener todos los fiados por estado (PAGADO / SIN_PAGAR)
    List<FiadoModel> findByEstado(EstadoFiado estado);

    List<FiadoModel> findByPersona(String persona);

    List<FiadoModel> findByPersonaAndEstado(String persona, EstadoFiado estado);

     
}
