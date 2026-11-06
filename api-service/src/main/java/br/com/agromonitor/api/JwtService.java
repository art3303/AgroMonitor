package br.com.agromonitor.api;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expiration;
    public JwtService(
    @Value("${JWT_SECRET}")
    String secret,
    @Value("${JWT_EXPIRATION_MINUTES:120}")
    long minutes) {
        if (secret.length() < 32) throw new IllegalArgumentException("JWT_SECRET precisa ter ao menos 32 caracteres");
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        expiration = minutes * 60_000;
    }
    public String generate(String username) {
        long now = System.currentTimeMillis();
        return Jwts.builder().subject(username).issuedAt(new Date(now)).expiration(new Date(now + expiration)).signWith(key).compact();
    }
    public String subject(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }
}
