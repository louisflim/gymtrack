package edu.cit.lim.gymtrack.feature.payments.dto;

public class CheckoutResponse {

    private Long paymentId;
    private String checkoutUrl;
    private String status;
    private String reference;
    private boolean mockCheckout;

    public CheckoutResponse(Long paymentId, String checkoutUrl, String status, String reference, boolean mockCheckout) {
        this.paymentId = paymentId;
        this.checkoutUrl = checkoutUrl;
        this.status = status;
        this.reference = reference;
        this.mockCheckout = mockCheckout;
    }

    public Long getPaymentId() { return paymentId; }
    public String getCheckoutUrl() { return checkoutUrl; }
    public String getStatus() { return status; }
    public String getReference() { return reference; }
    public boolean isMockCheckout() { return mockCheckout; }
}
