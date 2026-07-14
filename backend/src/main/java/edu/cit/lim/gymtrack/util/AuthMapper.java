package edu.cit.lim.gymtrack.util;

import edu.cit.lim.gymtrack.dto.AuthResponse;
import edu.cit.lim.gymtrack.entity.Gym;
import edu.cit.lim.gymtrack.entity.User;

public final class AuthMapper {

    private AuthMapper() {}

    public static AuthResponse toAuthResponse(User user, String token) {
        Gym gym = user.getGym();
        return new AuthResponse(
                token,
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name(),
                gym != null ? gym.getId() : null,
                gym != null ? gym.getName() : null,
                user.isMustChangePassword()
        );
    }

    public static AuthResponse issueToken(User user, JwtUtil jwtUtil) {
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return toAuthResponse(user, token);
    }
}
