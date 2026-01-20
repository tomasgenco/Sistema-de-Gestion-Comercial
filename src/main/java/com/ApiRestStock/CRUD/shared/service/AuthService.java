package com.ApiRestStock.CRUD.shared.service;


import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ApiRestStock.CRUD.shared.dto.AuthResponse;
import com.ApiRestStock.CRUD.shared.dto.LoginRequest;
import com.ApiRestStock.CRUD.shared.enums.RolUsuario;
import com.ApiRestStock.CRUD.shared.model.UsuarioModel;
import com.ApiRestStock.CRUD.shared.repositories.UsuarioRepository;
import com.ApiRestStock.CRUD.shared.security.JwtService;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsuarioRepository userRepository;

    public AuthService(PasswordEncoder passwordEncoder, JwtService jwtService, UsuarioRepository userRepository, AuthenticationManager am) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authenticationManager = am;
    }

    public AuthResponse register(LoginRequest request) {
        String userNameNorm = request.getUsername().trim().toLowerCase();

        if (userRepository.existsByUsername(userNameNorm)) {
            throw new IllegalArgumentException("El nombre de usuario ya está registrado.");
        }


        UsuarioModel user = new UsuarioModel();
        user.setUsername(userNameNorm);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRol(RolUsuario.VENDEDOR);
        user.setFechaCreacion(OffsetDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")));
        user = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(userNameNorm, user.getRol().name());

        return new AuthResponse(accessToken, "Bearer", user.getId(), userNameNorm, user.getRol().name());
    }

    public AuthResponse login(LoginRequest request) {
        String userNameNorm = request.getUsername().trim().toLowerCase();

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                userNameNorm,
                request.getPassword()
            )
        );


        // Si authenticate() no lanza excepción, las credenciales son válidas
        UsuarioModel user = userRepository.findByUsername(userNameNorm)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado tras autenticar."));

        String accessToken = jwtService.generateAccessToken(userNameNorm, user.getRol().name());

        return new AuthResponse(accessToken, "Bearer", user.getId(), user.getUsername(), user.getRol().name());
    }

    public LoginWithRefreshResult loginWithRefresh(LoginRequest request) {
        String userNameNorm = request.getUsername().trim().toLowerCase();

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                userNameNorm,
                request.getPassword()
            )
        );

        UsuarioModel user = userRepository.findByUsername(userNameNorm)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado tras autenticar."));

        String accessToken = jwtService.generateAccessToken(userNameNorm, user.getRol().name());
        String refreshToken = jwtService.generateRefreshToken(userNameNorm, user.getRol().name());

        AuthResponse response = new AuthResponse(accessToken, "Bearer", user.getId(), user.getUsername(), user.getRol().name());
        return new LoginWithRefreshResult(response, refreshToken);
    }

    public LoginWithRefreshResult refreshAccessToken(String refreshToken) {
        String username = jwtService.extractSubject(refreshToken);

        UsuarioModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado."));

        String accessToken = jwtService.generateAccessToken(username, user.getRol().name());
        String newRefreshToken = jwtService.generateRefreshToken(username, user.getRol().name());

        AuthResponse response = new AuthResponse(accessToken, "Bearer", user.getId(), user.getUsername(), user.getRol().name());
        return new LoginWithRefreshResult(response, newRefreshToken);
    }

    public record LoginWithRefreshResult(AuthResponse response, String refreshToken) {}
}
