package com.ApiRestStock.CRUD.Finanzas.gasto;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ApiRestStock.CRUD.Finanzas.DTOs.GastoRequest;
import com.ApiRestStock.CRUD.Finanzas.enums.TipoGasto;


@RestController
@RequestMapping("/gastos")
public class GastoController {

    @Autowired
    private GastoService gastoService;

    @GetMapping
    public List<GastoModel> getAllGastos(
            @RequestParam(required = false) TipoGasto tipo
    ) {
        if (tipo == null) {
            return gastoService.getAllGastos();
        } else {
            return gastoService.findByTipo(tipo);
        }
    }

    @PostMapping
    public ResponseEntity<GastoModel> postGasto(@RequestBody GastoRequest gasto) {
        GastoModel savedGasto = gastoService.registrarGasto(gasto.getTotal(), gasto.getTipo(), gasto.getNombreProveedor()).orElse(null);

        if (savedGasto != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(savedGasto);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/total")
    public double getTotalIngresos(@RequestParam(name = "dias", required = false) Integer dias) {
        if (dias == null) {
            return gastoService.getTotalGastos();
        }
        
        
        return gastoService.getTotalGastosLastDays(dias);
        
        
    }

}
