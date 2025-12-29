package com.ApiRestStock.CRUD.Services;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ApiRestStock.CRUD.Repositories.UsuarioRepository;
import com.ApiRestStock.CRUD.security.JwtService;
import com.ApiRestStock.CRUD.shared.model.UsuarioModel;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(String userName, String password) {
        UsuarioModel usuario = usuarioRepository.findByUsername(userName)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        return jwtService.generateToken(
            usuario.getUsername(),
            Map.of("rol", usuario.getRol())
        );
    }
}
