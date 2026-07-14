package edu.cit.lim.gymtrack.feature.auth.registration;

import edu.cit.lim.gymtrack.dto.AuthResponse;
import edu.cit.lim.gymtrack.entity.Gym;
import edu.cit.lim.gymtrack.entity.Role;
import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.feature.auth.registration.dto.RegisterRequest;
import edu.cit.lim.gymtrack.repository.GymRepository;
import edu.cit.lim.gymtrack.repository.UserRepository;
import edu.cit.lim.gymtrack.util.AuthMapper;
import edu.cit.lim.gymtrack.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final GymRepository gymRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public RegistrationService(UserRepository userRepository,
                               GymRepository gymRepository,
                               PasswordEncoder passwordEncoder,
                               JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.gymRepository = gymRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        String requestedRole = request.getRole() == null ? "" : request.getRole().toUpperCase();

        if (!requestedRole.equals("ADMIN") && !requestedRole.equals("MEMBER")) {
            throw new IllegalArgumentException("Please choose Member or Gym Owner.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("That email is already registered.");
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
                throw new IllegalArgumentException("Please enter a gym name when registering as a gym owner.");
            }
            String trimmedName = gymName.trim();
            if (gymRepository.existsByNameIgnoreCase(trimmedName)) {
                throw new IllegalArgumentException("A gym with that name already exists. Please choose another name.");
            }
            Gym gym = gymRepository.save(new Gym(trimmedName));
            user.setGym(gym);
        }

        User saved = userRepository.save(user);
        return AuthMapper.issueToken(saved, jwtUtil);
    }
}
