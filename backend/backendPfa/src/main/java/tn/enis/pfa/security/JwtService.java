package tn.enis.pfa.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret:elearning-secret-key-min-256-bits-for-hs256-algorithm-please-change-in-production}")
    private String secret;

    @Value("${app.jwt.expirationMs:86400000}")
    private long expirationMs;

    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
