package com.reserve.events.security.jwt;

import com.reserve.events.application.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    // Inyectamos las dependencias necesarias
    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. Extraer el token de la petición
        String jwt = getJwtFromRequest(request);

        // 2. Validar el token
        if (StringUtils.hasText(jwt)) {

            try{// 3. Obtener el email del token
                String email = tokenProvider.getEmailFromJWT(jwt);

                // 4. Cargar el usuario (UserDetails) desde la BBDD
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                // 5. Crear el token de autenticación
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Establecer el usuario en el Contexto de Seguridad
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }catch (ExpiredJwtException ex){
                logger.warn("El token JWT ha expirado: " + ex.getMessage());
                request.setAttribute("jwt_exception", ex);
            } catch (MalformedJwtException ex) {
                logger.error("Token JWT con formato inválido: " + ex.getMessage());
                request.setAttribute("jwt_exception", ex);
            } catch (SignatureException ex) {
                logger.error("Firma JWT inválida: " + ex.getMessage());
                request.setAttribute("jwt_exception", ex);
            } catch (UnsupportedJwtException ex) {
                logger.error("Token JWT no soportado: " + ex.getMessage());
                request.setAttribute("jwt_exception", ex);
            } catch (IllegalArgumentException ex) {
                logger.error("Las claims del JWT están vacías: " + ex.getMessage());
                request.setAttribute("jwt_exception", ex);
            } catch (Exception ex) {
                logger.error("Error al establecer la autenticación: " + ex.getMessage());
                request.setAttribute("jwt_exception", ex);
            }
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Helper para extraer el token del encabezado "Authorization: Bearer <token>"
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remueve "Bearer "
        }
        return null;
    }
}
