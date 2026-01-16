package com.ApiRestStock.CRUD.shared.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//Valida la firma y extrae el usuario autorizado
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Value("${security.jwt.cookie-name:AUTH_TOKEN}")
    private String accessCookieName;
    
    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

                // 1) Tomar token de Authorization o cookie
                final String authHeader = request.getHeader("Authorization");
                String token;

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                } else {
                    token = readCookie(request, accessCookieName);
                }

                // Si no hay token, seguir
                if (token == null || token.isBlank()) {
                    filterChain.doFilter(request, response);
                    return;
                }

                //3) Validar token (firma + expreación)
                if (!jwtService.isTokenValid(token) || !jwtService.isAccessToken(token)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // 4) Extraer subject (username)
                final String username = jwtService.extractSubject(token); // o extractUsername

                //5) Si ya esta autenticado, no tocar
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    //6) Cargar user + roles reales
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    //7) Crear autenticacion con authorities
                    UsernamePasswordAuthenticationToken authentication = new 
                    UsernamePasswordAuthenticationToken(userDetails, 
                        null,
                        userDetails.getAuthorities()
                    );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 8) Setear en SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);


                }

                filterChain.doFilter(request, response);
    }

    private String readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie != null && name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/");
    }


    

    
}
