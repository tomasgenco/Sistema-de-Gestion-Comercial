package com.ApiRestStock.CRUD.shared.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//Valida la firma y extrae el usuario autorizado
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                
                // 1) Tomar header authorization
                final String authHeader = request.getHeader("Authorization");

                //Si no hay token, seguir
                if (authHeader == null || !authHeader.startsWith("Bearer")) {
                    filterChain.doFilter(request, response);
                    return;
                }

                //2) Extraer token
                final String token = authHeader.substring(7);

                //3) Validar token (firma + expreación)
                if (!jwtService.isTokenValid(token)) {
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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/");
    }


    

    
}
