package com.peerhub.controller;

import com.peerhub.dto.GoogleAuthRequest;
import com.peerhub.dto.LoginRequest;
import com.peerhub.dto.LoginResponse;
import com.peerhub.dto.SignupRequest;
import com.peerhub.dto.UserDTO;
import com.peerhub.service.AuthService;
import com.peerhub.util.RecaptchaVerifier;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final RecaptchaVerifier recaptchaVerifier;

    public AuthController(AuthService authService, RecaptchaVerifier recaptchaVerifier) {
        this.authService = authService;
        this.recaptchaVerifier = recaptchaVerifier;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            recaptchaVerifier.verifyOrThrow(request.getCaptchaToken());
            LoginResponse response = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            recaptchaVerifier.verifyOrThrow(request.getCaptchaToken());
            LoginResponse response = authService.signup(
                    request.getName(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getRole()
            );
            return ResponseEntity.status(201).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleAuth(@RequestBody GoogleAuthRequest request) {
        try {
            recaptchaVerifier.verifyOrThrow(request.getCaptchaToken());
            boolean allowCreate = "signup".equalsIgnoreCase(request.getMode());
            LoginResponse response = authService.googleAuth(request.getIdToken(), request.getRole(), allowCreate);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Claims claims)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

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
