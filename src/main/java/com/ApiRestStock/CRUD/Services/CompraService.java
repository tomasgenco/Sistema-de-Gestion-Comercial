package com.ApiRestStock.CRUD.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ApiRestStock.CRUD.Repositories.CompraRepository;
import com.ApiRestStock.CRUD.Repositories.GastoRepository;
import com.ApiRestStock.CRUD.Repositories.ProveedorRepository;
import com.ApiRestStock.CRUD.stock.IProductRepository;

@Service
public class CompraService {

    @Autowired
    CompraRepository compraRepository;

    @Autowired
    IProductRepository productRepository;

    @Autowired
    ProveedorRepository proveedorRepository;

    @Autowired
    GastoRepository gastoRepository;

    




}
