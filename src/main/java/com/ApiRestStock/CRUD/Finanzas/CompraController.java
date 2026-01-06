package com.ApiRestStock.CRUD.Finanzas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ApiRestStock.CRUD.Finanzas.ingreso.DTOs.CompraRequest;

@RestController
@RequestMapping("/compras")
public class CompraController {

    @Autowired
    CompraService compraService;

    @GetMapping
    public List<CompraModel> getAllCompras() {
        return compraService.getAllCompras();
    }

    @PostMapping
    public ResponseEntity<CompraModel> registrarCompra(@RequestBody CompraRequest compraRequest) {
        CompraModel compraModel = compraService.registrarCompra(
            compraRequest.items(),
            compraRequest.metodoPago(),
            compraRequest.nombreProveedor()
        );
        return ResponseEntity.ok(compraModel);
    }
}
