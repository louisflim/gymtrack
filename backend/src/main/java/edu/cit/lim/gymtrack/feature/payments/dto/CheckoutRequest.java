package edu.cit.lim.gymtrack.feature.payments.dto;

public class CheckoutRequest {

    private Long planId;
    /** Optional. Must match APP_WEB_BASE_URL / CORS origins or gymtrack:// */
    private String successUrl;
    /** Optional. Must match APP_WEB_BASE_URL / CORS origins or gymtrack:// */
    private String cancelUrl;

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public String getSuccessUrl() { return successUrl; }
    public void setSuccessUrl(String successUrl) { this.successUrl = successUrl; }

    public String getCancelUrl() { return cancelUrl; }
    public void setCancelUrl(String cancelUrl) { this.cancelUrl = cancelUrl; }
}
