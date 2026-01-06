package com.ApiRestStock.CRUD.Finanzas.ingreso;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ApiRestStock.CRUD.Finanzas.enums.TipoIngreso;


@RestController
@RequestMapping("/ingresos")
public class IngresoController {
    //Falta implementar el endpoint GET para obtener los ingresos

    @Autowired
    IngresoService ingresoService;

    @GetMapping
    public List<IngresoModel> getIngresos(@RequestParam(required = false) TipoIngreso tipo) {
        // Lógica para manejar la solicitud GET y filtrar por tipo si se proporciona
        if (tipo != null) {
            return ingresoService.getIngresosByTipo(tipo);
        } else {
            return ingresoService.getAllIngresos();
        }
    }
    
}
