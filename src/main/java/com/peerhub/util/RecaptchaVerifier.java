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
public class RecaptchaVerifier {

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    private final String secretKey;
    private final boolean enabled;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public RecaptchaVerifier(@Value("${recaptcha.secret-key:}") String secretKey,
                             @Value("${recaptcha.enabled:true}") boolean enabled,
                             ObjectMapper objectMapper) {
        this.secretKey = secretKey == null ? "" : secretKey.trim();
        this.enabled = enabled;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
    }

    public void verifyOrThrow(String token) {
        if (!enabled) {
            return;
        }

        if (secretKey.isBlank()) {
            throw new RuntimeException("reCAPTCHA secret key is not configured");
        }

        if (token == null || token.isBlank()) {
            throw new RuntimeException("Please complete reCAPTCHA");
        }

        try {
            String body = "secret=" + URLEncoder.encode(secretKey, StandardCharsets.UTF_8)
                    + "&response=" + URLEncoder.encode(token, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VERIFY_URL))
                    .timeout(Duration.ofSeconds(8))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("reCAPTCHA verification failed");
            }

            Map<String, Object> payload = objectMapper.readValue(response.body(), Map.class);
            Object success = payload.get("success");
            boolean passed = Boolean.parseBoolean(String.valueOf(success));

            if (!passed) {
                throw new RuntimeException("reCAPTCHA validation failed");
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unable to verify reCAPTCHA");
        }
    }
}
