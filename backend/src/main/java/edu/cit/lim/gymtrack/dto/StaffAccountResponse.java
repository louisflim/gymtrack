package edu.cit.lim.gymtrack.dto;

public class StaffAccountResponse {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;

    public StaffAccountResponse(Long userId, String firstName, String lastName, String email, String role) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }

    public Long getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
