package com.peerhub.controller;

import com.peerhub.dto.LoginRequest;
import com.peerhub.dto.LoginResponse;
import com.peerhub.dto.UserDTO;
import com.peerhub.service.AuthService;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        Claims claims = (Claims) authentication.getPrincipal();
        Number idNum = (Number) claims.get("id");
        UserDTO user = new UserDTO(
                idNum.longValue(),
                claims.get("email", String.class),
                claims.get("role", String.class),
                claims.get("name", String.class),
                claims.get("initials", String.class)
        );
        return ResponseEntity.ok(Map.of("user", user));
    }
}
