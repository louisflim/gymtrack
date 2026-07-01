package edu.cit.lim.gymtrack.dto;

public class AuthResponse {

    private String token;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;

    public AuthResponse(String token, Long userId, String firstName, String lastName, String email, String role) {
        this.token = token;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}