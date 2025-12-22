package com.ApiRestStock.CRUD.DTOs;

public class LoginResponse {
    private String accesToken;
    private String tokenType = "Bearer";

    public LoginResponse(String accesToken) {
        this.accesToken = accesToken;
    }

    public String getAccesToken() {
        return accesToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}
