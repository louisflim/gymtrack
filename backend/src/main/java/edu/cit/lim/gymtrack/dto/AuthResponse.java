package edu.cit.lim.gymtrack.dto;

public class AuthResponse {

    private final String token;
    private final Long userId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String role;
    private final Long gymId;
    private final String gymName;
    private final boolean mustChangePassword;

    public AuthResponse(
            String token,
            Long userId,
            String firstName,
            String lastName,
            String email,
            String role,
            Long gymId,
            String gymName,
            boolean mustChangePassword
    ) {
        this.token = token;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.gymId = gymId;
        this.gymName = gymName;
        this.mustChangePassword = mustChangePassword;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public Long getGymId() {
        return gymId;
    }

    public String getGymName() {
        return gymName;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }
}
