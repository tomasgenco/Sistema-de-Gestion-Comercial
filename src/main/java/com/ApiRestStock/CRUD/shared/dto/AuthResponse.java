package com.ApiRestStock.CRUD.shared.dto;

public record AuthResponse(
    String accessToken,
    String tokenType,
    Long usuarioId,
    String username
) {}
