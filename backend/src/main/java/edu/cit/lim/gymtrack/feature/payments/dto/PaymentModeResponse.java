package edu.cit.lim.gymtrack.feature.payments.dto;

import edu.cit.lim.gymtrack.feature.payments.PaymentMode;

public class PaymentModeResponse {

    private final String mode;
    private final boolean mockEnabled;
    private final String description;

    public PaymentModeResponse(PaymentMode mode, boolean mockEnabled, String description) {
        this.mode = mode.name();
        this.mockEnabled = mockEnabled;
        this.description = description;
    }

    public String getMode() {
        return mode;
    }

    public boolean isMockEnabled() {
        return mockEnabled;
    }

    public String getDescription() {
        return description;
    }
}
