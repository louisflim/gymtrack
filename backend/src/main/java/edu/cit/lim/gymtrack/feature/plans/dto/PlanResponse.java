package edu.cit.lim.gymtrack.feature.plans.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PlanResponse {

    private Long id;
    private String name;
    private int durationDays;
    private BigDecimal price;
    private boolean active;
    private LocalDateTime createdAt;

    public PlanResponse(Long id, String name, int durationDays, BigDecimal price, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.durationDays = durationDays;
        this.price = price;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getDurationDays() { return durationDays; }
    public BigDecimal getPrice() { return price; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
