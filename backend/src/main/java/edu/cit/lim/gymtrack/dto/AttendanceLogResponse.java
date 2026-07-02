package edu.cit.lim.gymtrack.dto;

public class AttendanceLogResponse {
    private String checkInTime;
    private String checkOutTime;

    public AttendanceLogResponse(String checkInTime, String checkOutTime) {
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
    }

    public String getCheckInTime() { return checkInTime; }

    public String getCheckOutTime() { return checkOutTime; }
}
