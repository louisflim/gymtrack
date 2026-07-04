package edu.cit.lim.gymtrack.dto;

public class MembershipResponse {
    private Long id;
    private Long planId;
    private String planName;
    private String startDate;
    private String endDate;
    private String status;
    private Long gymId;
    private String gymName;
    private boolean firstCheckInCompleted;
    private String nextStep;

    public MembershipResponse(Long id, Long planId, String planName, String startDate, String endDate, String status,
                            Long gymId, String gymName, boolean firstCheckInCompleted, String nextStep) {
        this.id = id;
        this.planId = planId;
        this.planName = planName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.gymId = gymId;
        this.gymName = gymName;
        this.firstCheckInCompleted = firstCheckInCompleted;
        this.nextStep = nextStep;
    }

    public Long getId() { return id; }
    public Long getPlanId() { return planId; }
    public String getPlanName() { return planName; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public Long getGymId() { return gymId; }
    public String getGymName() { return gymName; }
    public boolean isFirstCheckInCompleted() { return firstCheckInCompleted; }
    public String getNextStep() { return nextStep; }
}
