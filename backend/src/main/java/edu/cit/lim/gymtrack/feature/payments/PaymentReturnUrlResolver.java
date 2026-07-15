package edu.cit.lim.gymtrack.feature.payments;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Resolves PayMongo / mock checkout return URLs from config and optional client overrides.
 */
@Component
public class PaymentReturnUrlResolver {

    private final String webBaseUrl;
    private final String configuredSuccessUrl;
    private final String configuredCancelUrl;
    private final Set<String> allowedOrigins;

    public PaymentReturnUrlResolver(
            @Value("${app.web-base-url:http://localhost:5173}") String webBaseUrl,
            @Value("${paymongo.success-url:}") String configuredSuccessUrl,
            @Value("${paymongo.cancel-url:}") String configuredCancelUrl,
            @Value("${app.cors.allowed-origins:http://localhost:5173,http://localhost:3000}") String corsOrigins) {
        this.webBaseUrl = trimTrailingSlash(webBaseUrl);
        this.configuredSuccessUrl = blankToNull(configuredSuccessUrl);
        this.configuredCancelUrl = blankToNull(configuredCancelUrl);
        this.allowedOrigins = buildAllowedOrigins(corsOrigins);
    }

    public String resolveSuccessUrl(String requested) {
        return resolve(requested, defaultSuccessUrl());
    }

    public String resolveCancelUrl(String requested) {
        return resolve(requested, defaultCancelUrl());
    }

    public String defaultSuccessUrl() {
        if (configuredSuccessUrl != null) {
            return configuredSuccessUrl;
        }
        return webBaseUrl + "/payment/success";
    }

    public String defaultCancelUrl() {
        if (configuredCancelUrl != null) {
            return configuredCancelUrl;
        }
        return webBaseUrl + "/payment/cancel";
    }

    private String resolve(String requested, String fallback) {
        if (requested == null || requested.isBlank()) {
            return fallback;
        }
        String trimmed = requested.trim();
        if (!isAllowedReturnUrl(trimmed)) {
            throw new IllegalArgumentException(
                    "Return URL is not allowed. Use your GymTrack web origin or the gymtrack:// app scheme."
            );
        }
        return trimmed;
    }

    boolean isAllowedReturnUrl(String url) {
        try {
            URI uri = URI.create(url);
            String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
            if ("gymtrack".equals(scheme)) {
                return true;
            }
            if (!"http".equals(scheme) && !"https".equals(scheme)) {
                return false;
            }
            String origin = originOf(uri);
            return allowedOrigins.contains(origin);
        } catch (Exception e) {
            return false;
        }
    }

    private Set<String> buildAllowedOrigins(String corsOrigins) {
        Set<String> origins = new LinkedHashSet<>();
        origins.add(originOf(URI.create(webBaseUrl)));
        if (configuredSuccessUrl != null) {
            origins.add(originOf(URI.create(configuredSuccessUrl)));
        }
        if (configuredCancelUrl != null) {
            origins.add(originOf(URI.create(configuredCancelUrl)));
        }
        if (corsOrigins != null && !corsOrigins.isBlank()) {
            origins.addAll(
                    Arrays.stream(corsOrigins.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(s -> originOf(URI.create(s)))
                            .collect(Collectors.toSet())
            );
        }
        return origins;
    }

    private static String originOf(URI uri) {
        String scheme = uri.getScheme();
        String host = uri.getHost();
        if (scheme == null || host == null) {
            throw new IllegalArgumentException("Invalid URL: " + uri);
        }
        int port = uri.getPort();
        if (port > 0) {
            return scheme.toLowerCase(Locale.ROOT) + "://" + host.toLowerCase(Locale.ROOT) + ":" + port;
        }
        return scheme.toLowerCase(Locale.ROOT) + "://" + host.toLowerCase(Locale.ROOT);
    }

    private static String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "http://localhost:5173";
        }
        String trimmed = value.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
