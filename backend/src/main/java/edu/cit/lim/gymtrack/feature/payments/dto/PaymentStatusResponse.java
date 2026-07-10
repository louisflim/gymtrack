package edu.cit.lim.gymtrack.feature.payments.dto;

public class PaymentStatusResponse {

    private String reference;
    private String status;
    private boolean paid;
    private boolean mockCheckout;

    public PaymentStatusResponse(String reference, String status, boolean paid, boolean mockCheckout) {
        this.reference = reference;
        this.status = status;
        this.paid = paid;
        this.mockCheckout = mockCheckout;
    }

    public String getReference() { return reference; }
    public String getStatus() { return status; }
    public boolean isPaid() { return paid; }
    public boolean isMockCheckout() { return mockCheckout; }
}
