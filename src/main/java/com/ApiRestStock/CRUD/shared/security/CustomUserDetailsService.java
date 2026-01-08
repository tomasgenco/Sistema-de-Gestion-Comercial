package com.ApiRestStock.CRUD.shared.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import com.ApiRestStock.CRUD.shared.enums.RolUsuario;
import com.ApiRestStock.CRUD.shared.model.UsuarioModel;
import com.ApiRestStock.CRUD.shared.repositories.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository ur){
        usuarioRepository = ur;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioModel usuario = usuarioRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        //IMPORTANTE: Spring espera "ROLE_X" para hasRole('X')
        RolUsuario rol = usuario.getRol();
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + rol));


        return new User(
            usuario.getUsername(),
            usuario.getPassword(), //Hash
            authorities
        );
    }


}
