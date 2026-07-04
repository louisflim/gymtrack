package edu.cit.lim.gymtrack.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {
    private Long id;
    private String memberName;
    private String planName;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public PaymentResponse(Long id, String memberName, String planName, BigDecimal amount, String currency,
                           String status, String paymentMethod, LocalDateTime createdAt, LocalDateTime paidAt) {
        this.id = id;
        this.memberName = memberName;
        this.planName = planName;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
    }

    public Long getId() { return id; }
    public String getMemberName() { return memberName; }
    public String getPlanName() { return planName; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
}
