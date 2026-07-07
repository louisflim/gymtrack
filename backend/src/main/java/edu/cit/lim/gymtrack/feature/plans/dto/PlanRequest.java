package edu.cit.lim.gymtrack.feature.plans.dto;

import java.math.BigDecimal;

public class PlanRequest {

    private String name;
    private int durationDays;
    private BigDecimal price;
    private Boolean active;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
