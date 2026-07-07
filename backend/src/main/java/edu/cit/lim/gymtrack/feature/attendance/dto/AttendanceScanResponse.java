package edu.cit.lim.gymtrack.feature.attendance.dto;

public class AttendanceScanResponse {
    private String action;
    private String memberName;
    private String role;
    private boolean active;
    private String planName;
    private String membershipStatus;
    private String timestamp;
    private String message;
    private String gymName;
    private boolean enrolled;

    public AttendanceScanResponse(String action, String memberName, String role, boolean active,
                                  String planName, String membershipStatus, String timestamp, String message,
                                  String gymName, boolean enrolled) {
        this.action = action;
        this.memberName = memberName;
        this.role = role;
        this.active = active;
        this.planName = planName;
        this.membershipStatus = membershipStatus;
        this.timestamp = timestamp;
        this.message = message;
        this.gymName = gymName;
        this.enrolled = enrolled;
    }

    public String getAction() { return action; }
    public String getMemberName() { return memberName; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }
    public String getPlanName() { return planName; }
    public String getMembershipStatus() { return membershipStatus; }
    public String getTimestamp() { return timestamp; }
    public String getMessage() { return message; }
    public String getGymName() { return gymName; }
    public boolean isEnrolled() { return enrolled; }
}

