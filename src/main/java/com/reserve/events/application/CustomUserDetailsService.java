package com.reserve.events.application;

import com.reserve.events.controllers.domain.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;


@Service
public class CustomUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Buscamos al usuario en la BBDD por su email
        com.reserve.events.controllers.domain.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado con el email: " + email));

        // 2. Creamos la lista de "autoridades" (roles) a partir de tu UserType
        // Spring Security requiere que los roles tengan el prefijo "ROLE_"
        String roleName = "ROLE_" + user.getType().name(); // Ej: "ROLE_CLIENTE" o "ROLE_ADMIN"
        GrantedAuthority authority = new SimpleGrantedAuthority(roleName);
        List<GrantedAuthority> authorities = Collections.singletonList(authority);

        // 3. Creamos el objeto UserDetails que Spring Security entiende
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),       // El "username" para Spring será el email
                user.getPassword(),    // La contraseña hasheada de la BBDD
                authorities            // La lista de roles
        );
    }



}
