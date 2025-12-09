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
import org.springframework.security.config.Customizer;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://10.147.17.249:5173",
                "http://127.0.0.1:5173",
                "http://localhost:8081",
                "http://127.0.0.1:8081",
                "http://localhost:8080",
                "http://127.0.0.1:8080"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                /*
                 * 1. Deshabilitar CSRF (Cross-Site Request Forgery).
                 * Es común deshabilitarlo para APIs REST stateless (que no usan sesiones).
                 */
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                /*
                 * 2. Configurar las reglas de autorización.
                 * Aquí defines qué rutas son públicas y cuáles son privadas.
                 */
                .authorizeHttpRequests(authz -> authz

                        // ==================== RUTAS PÚBLICAS ====================
                        // No requieren autenticación (sin token JWT)
                        // Agregar aquí: login, registro, documentación, recursos públicos
                        .requestMatchers(
                                "/User/login",
                                "/User/register",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/*.jpg",
                                "/*.png"
                        ).permitAll()

                        .requestMatchers(
                                "/bodas.jpg",
                                "/baby.jpg",
                                "/cumpleanos.jpg",
                                "/default.jpg",
                                "/bautizos.jpg",
                                "/grados.jpg",
                                "/infantil.jpg",
                                "/fiesta15.jpg",
                                "/despedida.jpg",
                                "/conferencias.jpg",
                                "/corporativa.jpg"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/entertainment",
                                "/catering",
                                "/decoration",
                                "/additional",
                                "/events",
                                "/establishments"
                        ).permitAll()

                        // ==================== SOLO ADMIN ====================

                        // GET solo ADMIN: Agregar rutas que solo el admin puede consultar
                        .requestMatchers(HttpMethod.GET,
                                "/admin-example-get",
                                "/User",
                                "/User/type/{type}",
                                "/User/{id}"
                        ).hasRole("ADMIN")

                        // POST solo ADMIN: Agregar rutas donde solo el admin puede crear recursos
                        .requestMatchers(HttpMethod.POST,
                                "/entertainment",
                                "/catering",
                                "/decoration",
                                "/additional",
                                "/establishments",
                                "/events"
                        ).hasRole("ADMIN")

                        // PUT solo ADMIN: Agregar rutas donde solo el admin puede actualizar
                        .requestMatchers(HttpMethod.PUT,
                                "/events/{id}",
                                "/establishments/**",
                                "/decoration/{id}",
                                "/additional/{id}",
                                "/entertainment/{id}",
                                "/catering/{id}"
                        ).hasRole("ADMIN")

                        // PATCH solo ADMIN: Agregar rutas donde solo el admin puede actualizar parcialmente
                        .requestMatchers(HttpMethod.PATCH,
                                "/admin-example-patch"
                        ).hasRole("ADMIN")

                        // DELETE solo ADMIN: Agregar rutas donde solo el admin puede eliminar
                        .requestMatchers(HttpMethod.DELETE,
                                "/events/{id}",
                                "/establishments/**"
                        ).hasRole("ADMIN")

                        // ==================== SOLO CLIENTE ====================

                        // GET solo CLIENTE: Agregar rutas que solo el cliente puede consultar
                        .requestMatchers(HttpMethod.GET,
                                "/cliente-example-get"
                        ).hasRole("CLIENTE")

                        // POST solo CLIENTE: Agregar rutas donde solo el cliente puede crear
                        .requestMatchers(HttpMethod.POST,
                                "/reserve"
                        ).hasRole("CLIENTE")

                        // PUT solo CLIENTE: Agregar rutas donde solo el cliente puede actualizar
                        .requestMatchers(HttpMethod.PUT,
                                "/reserve/{id}"
                        ).hasRole("CLIENTE")

                        // PATCH solo CLIENTE: Agregar rutas donde solo el cliente puede actualizar parcialmente
                        .requestMatchers(HttpMethod.PATCH,
                                "/cliente-example-patch"
                        ).hasRole("CLIENTE")

                        // DELETE solo CLIENTE: Agregar rutas donde solo el cliente puede eliminar
                        .requestMatchers(HttpMethod.DELETE,
                                "/cliente-example-delete"
                        ).hasRole("CLIENTE")

                        // ==================== ADMIN Y CLIENTE (AMBOS) ====================

                        // GET para ambos: Agregar rutas que ambos roles pueden consultar
                        .requestMatchers(HttpMethod.GET,
                                "/establishments/{id}/occupied-dates",
                                "/reserve",
                                "/reserve/{id}"
                        ).hasAnyRole("ADMIN", "CLIENTE")

                        // POST para ambos: Agregar rutas donde ambos pueden crear
                        .requestMatchers(HttpMethod.POST,
                                "/ambos-example-post"
                        ).hasAnyRole("ADMIN", "CLIENTE")

                        // PUT para ambos: Agregar rutas donde ambos pueden actualizar
                        .requestMatchers(HttpMethod.PUT,
                                "/reserve/{id}/cancelar"
                        ).hasAnyRole("ADMIN", "CLIENTE")

                        // PATCH para ambos: Agregar rutas donde ambos pueden actualizar parcialmente
                        .requestMatchers(HttpMethod.PATCH,
                                "/ambos-example-patch"
                        ).hasAnyRole("ADMIN", "CLIENTE")

                        // DELETE para ambos: Agregar rutas donde ambos pueden eliminar
                        .requestMatchers(HttpMethod.DELETE,
                                "/ambos-example-delete"
                        ).hasAnyRole("ADMIN", "CLIENTE")

                        // ==================== CUALQUIER OTRA PETICIÓN ====================
                        // Requiere estar autenticado (con token JWT válido, sin importar el rol)
                        .anyRequest().authenticated()
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