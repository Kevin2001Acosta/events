package com.reserve.events.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    // Se recomienda usar una clave secreta fuerte de al menos 256 bits (32 caracteres)
    // Se lee desde application.properties (ej. jwt.secret=TU_CLAVE_SECRETA_DE_32_CHARS)
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Tiempo de expiración del token en milisegundos (ej. 24 horas)
    @Value("${jwt.expiration-ms}")
    private long jwtExpirationInMs;

    private Key key;

    // Inicializa la clave de forma segura al inicio
    private Key getSigningKey() {
        if (this.key == null) {
            // Decodifica la clave secreta y la convierte a una clave segura
            this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        }
        return this.key;
    }

    /**
     * Genera un token JWT para un usuario autenticado.
     * @param authentication Objeto de autenticación de Spring Security.
     * @return Token JWT como String.
     */
    public String generateToken(Authentication authentication) {

        // Obtiene el email (username) del usuario autenticado
        String email = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // Extrae los roles (authorities) y los convierte en una cadena separada por comas
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(email) // El identificador principal (Payload 'sub')
                .claim("roles", roles) // Agrega los roles como un claim custom
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Obtiene el email (subject) del token.
     */
    public String getEmailFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Valida el token JWT.
     */
    public void validateToken(String authToken) {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
    }
}
