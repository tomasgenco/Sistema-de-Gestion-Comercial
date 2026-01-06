package com.ApiRestStock.CRUD.Finanzas.ingreso;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ApiRestStock.CRUD.Finanzas.enums.TipoIngreso;
import com.ApiRestStock.CRUD.ventas.VentaModel;
import com.ApiRestStock.CRUD.ventas.fiado.FiadoModel;

@Service
public class IngresoService {

    @Autowired
    IngresoRepository ingresoRepository;

    public Optional<IngresoModel> registrarIngreso(BigDecimal total, TipoIngreso tipo, VentaModel venta, FiadoModel fiado) {
        IngresoModel nuevoIngreso = new IngresoModel();
        nuevoIngreso.setFecha(java.time.OffsetDateTime.now());
        nuevoIngreso.setTotal(total);
        nuevoIngreso.setTipo(tipo);
        nuevoIngreso.setVenta(venta);
        nuevoIngreso.setFiado(fiado);

        ingresoRepository.save(nuevoIngreso);
        return Optional.of(nuevoIngreso);
    }

    public Optional<IngresoModel> getIngresoById(Long id) {
        return ingresoRepository.findById(id);
    }

    public List<IngresoModel> getAllIngresos() {
        return ingresoRepository.findAll();
    }

    public List<IngresoModel> getIngresosByTipo(TipoIngreso tipo) {
        return ingresoRepository.findByTipo(tipo);
    }
}
