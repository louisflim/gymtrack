package edu.cit.lim.gymtrack.feature.attendance.dto;

public class MemberGymScanResponse {
    private String action;
    private String message;
    private String gymName;
    private String membershipStatus;
    private String planName;
    private boolean firstCheckInCompleted;
    private String nextStep;

    public MemberGymScanResponse(String action, String message, String gymName, String membershipStatus,
                                 String planName, boolean firstCheckInCompleted, String nextStep) {
        this.action = action;
        this.message = message;
        this.gymName = gymName;
        this.membershipStatus = membershipStatus;
        this.planName = planName;
        this.firstCheckInCompleted = firstCheckInCompleted;
        this.nextStep = nextStep;
    }

    public String getAction() { return action; }
    public String getMessage() { return message; }
    public String getGymName() { return gymName; }
    public String getMembershipStatus() { return membershipStatus; }
    public String getPlanName() { return planName; }
    public boolean isFirstCheckInCompleted() { return firstCheckInCompleted; }
    public String getNextStep() { return nextStep; }
}

