package br.com.agromonitor.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtService jwt;
    @Value("${DEMO_USERNAME:aluno}")
    String username;
    @Value("${DEMO_PASSWORD:Agro@123}")
    String password;
    public AuthController(JwtService jwt) {
        this.jwt = jwt;
    }
    @PostMapping("/token")
    public ResponseEntity<?> token(
        @RequestBody
    Map<String, String> body) {
        if (!Objects.equals(username, body.get("username")) || !Objects.equals(password, body.get("password"))) return ResponseEntity.status(401).body(Map.of("error", "Credenciais inválidas"));
        return ResponseEntity.ok(Map.of("token", jwt.generate(username), "tokenType", "Bearer", "expiresInMinutes", 120));
    }
}
