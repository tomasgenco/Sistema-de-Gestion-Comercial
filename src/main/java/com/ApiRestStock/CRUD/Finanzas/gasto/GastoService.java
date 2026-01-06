package com.ApiRestStock.CRUD.Finanzas.gasto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ApiRestStock.CRUD.Finanzas.DTOs.GastoRequest;
import com.ApiRestStock.CRUD.Finanzas.enums.TipoGasto;

@Service
public class GastoService {

    @Autowired
    private GastoRepository gastoRepository;

    @Transactional
    public Optional<GastoModel> registrarGasto(BigDecimal total, TipoGasto tipo, String nombreProveedor) {
        GastoModel gastoModel = new GastoModel();
        gastoModel.setFecha(java.time.OffsetDateTime.now());
        gastoModel.setTotal(total);
        gastoModel.setTipo(tipo);
        gastoModel.setNombreProveedor(nombreProveedor);

        
        
        GastoModel savedGasto = gastoRepository.save(gastoModel);
        return Optional.of(savedGasto);
    }

    public Optional<GastoModel> getGastoById(Long id) {
        return gastoRepository.findById(id);
    }

    public List<GastoModel> getAllGastos() {
        return gastoRepository.findAll();
    }

    public List<GastoModel> findByTipo(TipoGasto tipo) {
        return gastoRepository.findByTipo(tipo);
    }
}
