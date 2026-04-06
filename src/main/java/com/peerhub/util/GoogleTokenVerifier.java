package com.peerhub.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@Component
public class GoogleTokenVerifier {

    private static final String GOOGLE_TOKENINFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";

    private final String googleClientId;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GoogleTokenVerifier(@Value("${google.oauth.client-id:}") String googleClientId,
                               ObjectMapper objectMapper) {
        this.googleClientId = googleClientId == null ? "" : googleClientId.trim();
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
    }

    public VerifiedGoogleUser verify(String idToken) {
        if (idToken == null || idToken.isBlank()) {
            throw new RuntimeException("Google ID token is required");
        }

        try {
            String encodedToken = URLEncoder.encode(idToken, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GOOGLE_TOKENINFO_URL + encodedToken))
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Invalid Google token");
            }

            Map<String, Object> payload = objectMapper.readValue(response.body(), Map.class);

            String aud = stringValue(payload.get("aud"));
            if (!googleClientId.isBlank() && !googleClientId.equals(aud)) {
                throw new RuntimeException("Google client mismatch");
            }

            String email = stringValue(payload.get("email"));
            String name = stringValue(payload.get("name"));
            String emailVerified = stringValue(payload.get("email_verified"));

            if (email.isBlank()) {
                throw new RuntimeException("Google account email is unavailable");
            }

            if (!"true".equalsIgnoreCase(emailVerified)) {
                throw new RuntimeException("Google email is not verified");
            }

            return new VerifiedGoogleUser(email.toLowerCase(), name);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify Google token");
        }
    }

    private String stringValue(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    public record VerifiedGoogleUser(String email, String name) {}
}