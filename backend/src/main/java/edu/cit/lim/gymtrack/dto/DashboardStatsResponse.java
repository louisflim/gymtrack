package edu.cit.lim.gymtrack.dto;

import java.math.BigDecimal;

public class DashboardStatsResponse {
    private long totalMembers;
    private long activeSubscriptions;
    private long expiredMemberships;
    private long todayCheckIns;
    private BigDecimal totalPaymentsCollected;

    public DashboardStatsResponse(long totalMembers, long activeSubscriptions, long expiredMemberships,
                                  long todayCheckIns, BigDecimal totalPaymentsCollected) {
        this.totalMembers = totalMembers;
        this.activeSubscriptions = activeSubscriptions;
        this.expiredMemberships = expiredMemberships;
        this.todayCheckIns = todayCheckIns;
        this.totalPaymentsCollected = totalPaymentsCollected;
    }

    public long getTotalMembers() { return totalMembers; }
    public long getActiveSubscriptions() { return activeSubscriptions; }
    public long getExpiredMemberships() { return expiredMemberships; }
    public long getTodayCheckIns() { return todayCheckIns; }
    public BigDecimal getTotalPaymentsCollected() { return totalPaymentsCollected; }
}
