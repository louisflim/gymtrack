package edu.cit.lim.gymtrack.feature.members.dto;

public class MemberResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean active;
    private String planName;
    private String membershipStatus;
    private String membershipEndDate;
    private String gymName;
    private boolean enrolled;

    public MemberResponse(Long id, String firstName, String lastName, String email, boolean active,
                          String planName, String membershipStatus, String membershipEndDate,
                          String gymName, boolean enrolled) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
        this.planName = planName;
        this.membershipStatus = membershipStatus;
        this.membershipEndDate = membershipEndDate;
        this.gymName = gymName;
        this.enrolled = enrolled;
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public boolean isActive() { return active; }
    public String getPlanName() { return planName; }
    public String getMembershipStatus() { return membershipStatus; }
    public String getMembershipEndDate() { return membershipEndDate; }
    public String getGymName() { return gymName; }
    public boolean isEnrolled() { return enrolled; }
}
