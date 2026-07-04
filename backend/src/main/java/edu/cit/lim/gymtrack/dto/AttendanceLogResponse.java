package edu.cit.lim.gymtrack.dto;

public class AttendanceLogResponse {
    private Long id;
    private Long memberId;
    private String memberName;
    private String checkInTime;
    private String checkOutTime;

    public AttendanceLogResponse(String checkInTime, String checkOutTime) {
        this(null, null, null, checkInTime, checkOutTime);
    }

    public AttendanceLogResponse(Long id, Long memberId, String memberName,
                                 String checkInTime, String checkOutTime) {
        this.id = id;
        this.memberId = memberId;
        this.memberName = memberName;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
    }

    public Long getId() { return id; }
    public Long getMemberId() { return memberId; }
    public String getMemberName() { return memberName; }
    public String getCheckInTime() { return checkInTime; }
    public String getCheckOutTime() { return checkOutTime; }
}
