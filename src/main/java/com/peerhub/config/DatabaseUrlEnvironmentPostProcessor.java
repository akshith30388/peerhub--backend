package com.peerhub.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "peerhubDatabaseUrlOverrides";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String rawDatabaseUrl = trimToNull(environment.getProperty("DATABASE_URL"));
        if (rawDatabaseUrl == null) {
            return;
        }

        Map<String, Object> overrides = new LinkedHashMap<>();
        if (rawDatabaseUrl.startsWith("mysql://")) {
            applyMysqlUri(rawDatabaseUrl, overrides);
        } else if (rawDatabaseUrl.startsWith("jdbc:mysql://")) {
            overrides.put("spring.datasource.url", normalizeJdbcMysqlUrl(rawDatabaseUrl));
        }

        if (!overrides.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, overrides));
        }
    }

    private void applyMysqlUri(String rawDatabaseUrl, Map<String, Object> overrides) {
        URI uri = URI.create(rawDatabaseUrl);

        String host = trimToNull(uri.getHost());
        if (host == null) {
            return;
        }

        int port = uri.getPort();
        String path = trimToNull(uri.getRawPath());
        String databasePath = (path == null) ? "/defaultdb" : path;
        String normalizedQuery = normalizeQuery(uri.getRawQuery());

        StringBuilder jdbcUrl = new StringBuilder("jdbc:mysql://")
                .append(host);
        if (port > 0) {
            jdbcUrl.append(":").append(port);
        }
        jdbcUrl.append(databasePath);
        if (!normalizedQuery.isBlank()) {
            jdbcUrl.append("?").append(normalizedQuery);
        }

        overrides.put("spring.datasource.url", jdbcUrl.toString());

        String userInfo = trimToNull(uri.getRawUserInfo());
        if (userInfo != null) {
            int separator = userInfo.indexOf(':');
            if (separator >= 0) {
                String username = decode(userInfo.substring(0, separator));
                String password = decode(userInfo.substring(separator + 1));
                if (!username.isBlank()) {
                    overrides.put("spring.datasource.username", username);
                }
                overrides.put("spring.datasource.password", password);
            } else {
                String username = decode(userInfo);
                if (!username.isBlank()) {
                    overrides.put("spring.datasource.username", username);
                }
            }
        }
    }

    private String normalizeJdbcMysqlUrl(String jdbcUrl) {
        int queryIndex = jdbcUrl.indexOf('?');
        if (queryIndex < 0 || queryIndex >= jdbcUrl.length() - 1) {
            return jdbcUrl;
        }

        String base = jdbcUrl.substring(0, queryIndex);
        String query = jdbcUrl.substring(queryIndex + 1);
        String normalizedQuery = normalizeQuery(query);
        if (normalizedQuery.isBlank()) {
            return base;
        }
        return base + "?" + normalizedQuery;
    }

    private String normalizeQuery(String rawQuery) {
        if (rawQuery == null || rawQuery.isBlank()) {
            return "";
        }

        String[] parts = rawQuery.split("&");
        List<String> normalized = new ArrayList<>();
        for (String part : parts) {
            if (part == null || part.isBlank()) {
                continue;
            }
            int separator = part.indexOf('=');
            String key = separator >= 0 ? part.substring(0, separator) : part;
            String value = separator >= 0 ? part.substring(separator + 1) : "";

            String keyLower = decode(key).toLowerCase(Locale.ROOT);
            if ("ssl-mode".equals(keyLower)) {
                key = "sslMode";
            }
            normalized.add(value.isEmpty() ? key : key + "=" + value);
        }
        return String.join("&", normalized);
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
