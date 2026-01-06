package com.ApiRestStock.CRUD.shared.dto;

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
