package edu.cit.lim.gymtrack.feature.payments.dto;

public class CheckoutResponse {

    private Long paymentId;
    private String checkoutUrl;
    private String status;

    public CheckoutResponse(Long paymentId, String checkoutUrl, String status) {
        this.paymentId = paymentId;
        this.checkoutUrl = checkoutUrl;
        this.status = status;
    }

    public Long getPaymentId() { return paymentId; }
    public String getCheckoutUrl() { return checkoutUrl; }
    public String getStatus() { return status; }
}
