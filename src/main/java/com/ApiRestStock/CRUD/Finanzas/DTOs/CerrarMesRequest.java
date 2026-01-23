package com.ApiRestStock.CRUD.Finanzas.DTOs;



public class CerrarMesRequest {

    
    private Integer mes;


    private Integer anio;

    public CerrarMesRequest() {}

    public CerrarMesRequest(Integer mes, Integer anio) {
        this.mes = mes;
        this.anio = anio;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }
}
