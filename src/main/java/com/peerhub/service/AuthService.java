package com.peerhub.service;

import com.peerhub.dto.LoginResponse;
import com.peerhub.dto.UserDTO;
import com.peerhub.model.User;
import com.peerhub.repository.UserRepository;
import com.peerhub.util.GoogleTokenVerifier;
import com.peerhub.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final GoogleTokenVerifier googleTokenVerifier;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       GoogleTokenVerifier googleTokenVerifier) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.googleTokenVerifier = googleTokenVerifier;
    }

    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return issueToken(user);
    }

    public LoginResponse signup(String name, String email, String password, String role) {
        String normalizedEmail = normalizeEmail(email);
        String normalizedName = normalizeName(name, normalizedEmail);
        String normalizedRole = normalizeRole(role);

        if (password == null || password.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new RuntimeException("An account with this email already exists");
        }

        User user = new User(
                normalizedEmail,
                passwordEncoder.encode(password),
                normalizedRole,
                normalizedName,
                buildInitials(normalizedName, normalizedEmail)
        );

        return issueToken(userRepository.save(user));
    }

    public LoginResponse googleAuth(String idToken, String role, boolean allowCreate) {
        GoogleTokenVerifier.VerifiedGoogleUser googleUser = googleTokenVerifier.verify(idToken);
        User existing = userRepository.findByEmail(googleUser.email()).orElse(null);

        if (existing != null) {
            return issueToken(existing);
        }

        if (!allowCreate) {
            throw new RuntimeException("No account found for this Google user. Please sign up first.");
        }

        String normalizedName = normalizeName(googleUser.name(), googleUser.email());
        String normalizedRole = normalizeRole(role);

        User user = new User(
                googleUser.email(),
                passwordEncoder.encode(UUID.randomUUID().toString()),
                normalizedRole,
                normalizedName,
                buildInitials(normalizedName, googleUser.email())
        );

        return issueToken(userRepository.save(user));
    }

    private LoginResponse issueToken(User user) {
        String token = jwtUtil.generateToken(
                user.getId(), user.getEmail(), user.getRole(), user.getName(), user.getInitials()
        );

        return new LoginResponse(token, new UserDTO(user));
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email is required");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeRole(String role) {
        String normalized = role == null ? "student" : role.trim().toLowerCase(Locale.ROOT);
        if (!normalized.equals("student") && !normalized.equals("instructor")) {
            throw new RuntimeException("Role must be student or instructor");
        }
        return normalized;
    }

    private String normalizeName(String name, String email) {
        if (name != null && !name.trim().isBlank()) {
            return name.trim();
        }
        String[] parts = email.split("@");
        return parts.length > 0 ? parts[0] : "PeerHub User";
    }

    private String buildInitials(String name, String email) {
        if (name != null && !name.isBlank()) {
            String[] tokens = name.trim().split("\\s+");
            if (tokens.length >= 2) {
                return (firstChar(tokens[0]) + firstChar(tokens[1])).toUpperCase(Locale.ROOT);
            }
            if (tokens.length == 1 && !tokens[0].isBlank()) {
                String token = tokens[0];
                String first = firstChar(token);
                String second = token.length() > 1 ? String.valueOf(token.charAt(1)) : "X";
                return (first + second).toUpperCase(Locale.ROOT);
            }
        }

        String local = email.split("@")[0];
        String first = local.isBlank() ? "P" : String.valueOf(local.charAt(0));
        String second = local.length() > 1 ? String.valueOf(local.charAt(1)) : "H";
        return (first + second).toUpperCase(Locale.ROOT);
    }

    private String firstChar(String value) {
        return value == null || value.isBlank() ? "X" : String.valueOf(value.charAt(0));
    }
}
