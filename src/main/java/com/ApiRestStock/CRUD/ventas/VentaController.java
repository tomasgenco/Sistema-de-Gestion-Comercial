package com.ApiRestStock.CRUD.ventas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ApiRestStock.CRUD.ventas.DTOs.VentaRequest;



@RestController
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping
    public List<VentaModel> getVentas() {
        return this.ventaService.getVentas();
        
    }
    


    @PostMapping
    public ResponseEntity<Void> subirVenta(@RequestBody VentaRequest request) {

        
        ventaService.registrarVenta(request.items(), request.metodoPago());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
}
