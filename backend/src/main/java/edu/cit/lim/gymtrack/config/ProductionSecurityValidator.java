package edu.cit.lim.gymtrack.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProductionSecurityValidator implements ApplicationRunner {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${paymongo.mock-enabled:false}")
    private boolean paymongoMockEnabled;

    @Value("${paymongo.webhook-secret:}")
    private String webhookSecret;

    @Value("${paymongo.secret-key:}")
    private String paymongoSecretKey;

    @Value("${spring.datasource.password:}")
    private String datasourcePassword;

    @Override
    public void run(ApplicationArguments args) {
        if (jwtSecret == null
                || jwtSecret.isBlank()
                || jwtSecret.contains("CHANGE_THIS")
                || jwtSecret.length() < 32) {
            throw new IllegalStateException(
                    "Production requires JWT_SECRET (at least 32 characters). Set it as an environment variable."
            );
        }

        if (datasourcePassword == null || datasourcePassword.isBlank()) {
            throw new IllegalStateException(
                    "Production requires SPRING_DATASOURCE_PASSWORD. Set it as an environment variable."
            );
        }

        if (paymongoMockEnabled) {
            throw new IllegalStateException(
                    "Production cannot run with PAYMONGO_MOCK_ENABLED=true. Disable mock payments."
            );
        }

        if (!paymongoMockEnabled && (paymongoSecretKey == null || paymongoSecretKey.isBlank())) {
            throw new IllegalStateException(
                    "Production requires PAYMONGO_SECRET_KEY when mock payments are disabled."
            );
        }

        if (webhookSecret == null || webhookSecret.isBlank()) {
            throw new IllegalStateException(
                    "Production requires PAYMONGO_WEBHOOK_SECRET for webhook verification."
            );
        }
    }
}
