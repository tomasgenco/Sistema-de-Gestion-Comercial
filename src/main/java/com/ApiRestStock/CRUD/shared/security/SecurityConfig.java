package com.ApiRestStock.CRUD.shared.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableMethodSecurity //Habilita @PreAuthorize / @PostAuthorize
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig (JwtAuthFilter jaf){
        jwtAuthFilter = jaf;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(Customizer.withDefaults())


            .authorizeHttpRequests(auth -> auth
                // Públicos
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // VENDEDOR + ADMIN: ventas (GET y POST)
                .requestMatchers(HttpMethod.GET, "/ventas/**").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(HttpMethod.POST, "/ventas/**").hasAnyRole("ADMIN", "VENDEDOR")
                
                // VENDEDOR + ADMIN: compras (POST)
                .requestMatchers(HttpMethod.POST, "/compras/**").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(HttpMethod.GET, "/compras/**").hasAnyRole("ADMIN", "VENDEDOR")

                // VENDEDOR + ADMIN: proveedores (GET y POST)
                .requestMatchers(HttpMethod.GET, "/proveedores/**").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(HttpMethod.POST, "/proveedores/**").hasAnyRole("ADMIN", "VENDEDOR")

                // VENDEDOR + ADMIN: búsqueda de productos
                .requestMatchers(HttpMethod.GET, "/producto/search").hasAnyRole("ADMIN", "VENDEDOR")

                // Todo lo demás: solo ADMIN
                .anyRequest().hasRole("ADMIN")
            )

            // JWT Filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }



}
