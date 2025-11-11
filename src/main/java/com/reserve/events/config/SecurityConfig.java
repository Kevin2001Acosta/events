package com.reserve.events.config;

import com.reserve.events.application.CustomUserDetailsService;
import com.reserve.events.security.jwt.JwtAuthenticationFilter;
// IMPORTANTE: Importamos HttpMethod para reglas más específicas
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// clase contra la que registramos filtros
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Habilita la configuración de seguridad web de Spring
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    // Bean 1 Hasheador de contraseñas utilizando BCrypt
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // --- BEAN 2: El Proveedor de Autenticación ---
    /**
     * Define el "proveedor" que Spring usará para autenticar.
     * Le decimos que use nuestro CustomUserDetailsService (para buscar en BBDD)
     * y BCrypt (para comparar contraseñas).
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // --- BEAN 3: El Gestor de Autenticación ---
    /**
     * Expone el AuthenticationManager como un Bean para que podamos
     * usarlo en nuestros controladores (ej. para el endpoint de login).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // --- BEAN 4: La Cadena de Filtros de Seguridad (El Núcleo) ---
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                /*
                 * 1. Deshabilitar CSRF (Cross-Site Request Forgery).
                 * Es común deshabilitarlo para APIs REST stateless (que no usan sesiones).
                 */
                .csrf(AbstractHttpConfigurer::disable)

                /*
                 * 2. Configurar las reglas de autorización.
                 * Aquí defines qué rutas son públicas y cuáles son privadas.
                 */
                .authorizeHttpRequests(authz -> authz
                        // Tus endpoints públicos (ej. login, registro, swagger)
                        .requestMatchers(
                                "/User/login",
                                "/User/register",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // 2. Endpoints solo para POST ADMIN
                        // (Ej. borrar usuarios, crear eventos)
                        .requestMatchers(HttpMethod.POST,
                                "/entertainment",
                                "/catering",
                                "/decoration",
                                "/additional",
                                "/establishments",
                                "/events").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/events/{id}", "/establishments/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/events/{id}", "/establishments/**").hasRole("ADMIN")


                        // 3. Endpoints solo para CLIENTE
                        // (Ej. hacer una reserva, ver mi perfil)
                        // TODO: quitar la ruta de ejemplo cuando pongan una ruta real
                        .requestMatchers("/Ruta-ejemplo").hasRole("CLIENTE")

                        // 4. Endpoints para AMBOS (ADMIN o CLIENTE)
                        .requestMatchers("/reserve/{id}/cancelar").hasAnyRole("ADMIN", "CLIENTE")

                        // solo para los endpoint get con esta ruta
                        .requestMatchers(HttpMethod.GET, "/events", "/establishments/{id}/occupied-dates").hasAnyRole("ADMIN", "CLIENTE")

                        // 5. CUALQUIER OTRA PETICIÓN
                        .anyRequest().authenticated() // Requiere token (ADMIN o CLIENTE)
                )
                // --- FIN DE LA SECCIÓN DE AUTORIZACIÓN ---

                /*
                 * 3. Configurar la gestión de sesión.
                 * Para una API REST, se usa STATELESS (sin estado).
                 * Spring no creará ni usará sesiones HTTP.
                 */
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                /*
                 * 4. Le decimos a Spring Security que use el proveedor de
                 * autenticación que configuramos en el BEAN 2.
                 */
                .authenticationProvider(authenticationProvider())

        // (Aquí es donde se añadiría un filtro JWT si lo estuvieras usando)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
