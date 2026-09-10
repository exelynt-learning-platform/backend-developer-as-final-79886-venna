package com.example.booking.security;

import com.example.booking.entity.AppUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    @Value("${app.jwt.secret}") private String secret;
    @Value("${app.jwt.expiration-ms:3600000}") private long expirationMs;
    private SecretKey key;
    @PostConstruct
    void initialize() {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32)
            throw new IllegalStateException("app.jwt.secret must be at least 32 bytes");
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    public String generate(AppUser user) {
        Instant now = Instant.now();
        return Jwts.builder().subject(user.getUsername()).claim("role", user.getRole().name())
                .issuedAt(Date.from(now)).expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key).compact();
    }
    public String username(String token) { return parse(token).getPayload().getSubject(); }
    public boolean isValid(String token) {
        try { parse(token); return true; } catch (JwtException | IllegalArgumentException ex) { return false; }
    }
    public long expirationMs() { return expirationMs; }
    private Jws<Claims> parse(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token); }
}
