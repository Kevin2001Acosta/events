package com.reserve.events.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que se invoca cuando un usuario intenta acceder a un recurso protegido
 * sin proporcionar credenciales válidas (ej. token ausente o inválido).
 * Devuelve un error 401 Unauthorized y un JSON descriptivo.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        final Exception exception = (Exception) request.getAttribute("jwt_exception");
        String message;

        if (exception != null) {
            if (exception instanceof ExpiredJwtException) {
                message = "El token JWT ha expirado.";
            } else if (exception instanceof SignatureException) {
                message = "La firma del token JWT es inválida.";
            } else if (exception instanceof MalformedJwtException) {
                message = "El token JWT tiene un formato incorrecto.";
            } else if (exception instanceof UnsupportedJwtException) {
                message = "El token JWT no es soportado.";
            } else if (exception instanceof IllegalArgumentException) {
                message = "Las claims del token JWT están vacías.";
            } else {
                message = "Token JWT inválido.";
            }
        } else {
            message = authException.getMessage();
        }


        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("path", request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
