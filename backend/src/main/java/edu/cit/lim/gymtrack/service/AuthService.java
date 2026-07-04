package edu.cit.lim.gymtrack.service;

import edu.cit.lim.gymtrack.dto.AuthResponse;
import edu.cit.lim.gymtrack.dto.LoginRequest;
import edu.cit.lim.gymtrack.dto.RegisterRequest;
import edu.cit.lim.gymtrack.dto.StaffAccountResponse;
import edu.cit.lim.gymtrack.entity.Gym;
import edu.cit.lim.gymtrack.entity.Role;
import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.repository.GymRepository;
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
    private GymRepository gymRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        String requestedRole = request.getRole() == null ? "" : request.getRole().toUpperCase();

        if (!requestedRole.equals("ADMIN") && !requestedRole.equals("MEMBER")) {
            throw new IllegalArgumentException("Role must be either ADMIN or MEMBER");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        Role role = Role.valueOf(requestedRole);

        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                role
        );

        if (role == Role.ADMIN) {
            String gymName = request.getGymName();
            if (gymName == null || gymName.isBlank()) {
                throw new IllegalArgumentException("Gym name is required when registering as gym owner.");
            }
            String trimmedName = gymName.trim();
            if (gymRepository.existsByNameIgnoreCase(trimmedName)) {
                throw new IllegalArgumentException("A gym with this name already exists.");
            }
            Gym gym = gymRepository.save(new Gym(trimmedName));
            user.setGym(gym);
        }

        User saved = userRepository.save(user);

        String token = jwtUtil.generateToken(saved.getEmail(), saved.getRole().name(), saved.getId());

        return toAuthResponse(saved, token);
    }

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
