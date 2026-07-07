package edu.cit.lim.gymtrack.service;

import edu.cit.lim.gymtrack.dto.AuthResponse;
import edu.cit.lim.gymtrack.dto.LoginRequest;
import edu.cit.lim.gymtrack.dto.StaffAccountResponse;
import edu.cit.lim.gymtrack.feature.auth.registration.dto.RegisterRequest;
import edu.cit.lim.gymtrack.entity.Gym;
import edu.cit.lim.gymtrack.entity.Role;
import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.repository.UserRepository;
import edu.cit.lim.gymtrack.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!user.isActive()) {
            throw new DisabledException("This account has been deactivated");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());

        return toAuthResponse(user, token);
    }

    public StaffAccountResponse createStaffByAdmin(RegisterRequest request, String requesterEmail) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Requesting user not found."));

        if (requester.getRole() != Role.ADMIN) {
            throw new SecurityException("Only ADMIN can create STAFF accounts.");
        }

        if (requester.getGym() == null) {
            throw new IllegalArgumentException("Your admin account is not linked to a gym.");
        }

        if (request.getRole() != null && !request.getRole().isBlank() && !"STAFF".equalsIgnoreCase(request.getRole())) {
            throw new IllegalArgumentException("Role must be STAFF for this endpoint.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User staff = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.STAFF
        );
        staff.setGym(requester.getGym());

        User saved = userRepository.save(staff);
        return new StaffAccountResponse(
                saved.getId(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getEmail(),
                saved.getRole().name()
        );
    }

    private AuthResponse toAuthResponse(User user, String token) {
        Gym gym = user.getGym();
        Long gymId = gym != null ? gym.getId() : null;
        String gymName = gym != null ? gym.getName() : null;
        return new AuthResponse(
                token,
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name(),
                gymId,
                gymName
        );
    }
}
