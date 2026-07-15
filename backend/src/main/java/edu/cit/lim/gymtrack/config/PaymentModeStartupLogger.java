package edu.cit.lim.gymtrack.config;

import edu.cit.lim.gymtrack.feature.payments.PayMongoService;
import edu.cit.lim.gymtrack.feature.payments.PaymentMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Logs the active PayMongo / mock payment mode at startup so school demos are unambiguous.
 */
@Component
public class PaymentModeStartupLogger implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PaymentModeStartupLogger.class);

    private final PayMongoService payMongoService;

    public PaymentModeStartupLogger(PayMongoService payMongoService) {
        this.payMongoService = payMongoService;
    }

    @Override
    public void run(ApplicationArguments args) {
        PaymentMode mode = payMongoService.currentMode();
        switch (mode) {
            case MOCK -> log.info(
                    "Payments: MOCK mode (school demo). Set paymongo.mock-enabled=false and PAYMONGO_SECRET_KEY for real checkout."
            );
            case TEST -> log.info(
                    "Payments: PayMongo TEST mode (sk_test). Use test wallets/cards; no live charges."
            );
            case LIVE -> log.warn(
                    "Payments: PayMongo LIVE mode (sk_live). Real money will be charged."
            );
        }
    }
}
