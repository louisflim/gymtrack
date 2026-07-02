package edu.cit.lim.gymtrack.dto;

public class AttendanceScanResponse {
    private String action;
    private String memberName;
    private String role;
    private boolean active;
    private String timestamp;
    private String message;

    public AttendanceScanResponse(String action, String memberName, String role, boolean active, String timestamp, String message) {
        this.action = action;
        this.memberName = memberName;
        this.role = role;
        this.active = active;
        this.timestamp = timestamp;
        this.message = message;
    }

    public String getAction() { return action; }
    public String getMemberName() { return memberName; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }
    public String getTimestamp() { return timestamp; }
    public String getMessage() { return message; }
}
