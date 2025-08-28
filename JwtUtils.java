package com.gestaodeh.gestaodeh.security.jwt;
import com.gestaodeh.gestaodeh.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Value("${gestaodeh.app.jwtSecret}") private String jwtSecret;
    @Value("${gestaodeh.app.jwtExpirationMs}") private int jwtExpirationMs;
    private SecretKey key() { return Keys.hmacShaKeyFor(jwtSecret.getBytes()); }
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256).compact();
    }
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
    }
    public boolean validateJwtToken(String authToken) {
        try { Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken); return true;
        } catch (Exception e) { logger.error("Token JWT inválido: {}", e.getMessage()); }
        return false;
    }
}