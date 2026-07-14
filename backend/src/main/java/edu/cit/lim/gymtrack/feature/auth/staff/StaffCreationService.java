package edu.cit.lim.gymtrack.feature.auth.staff;

import edu.cit.lim.gymtrack.entity.Role;
import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.feature.auth.staff.dto.CreateStaffRequest;
import edu.cit.lim.gymtrack.feature.auth.staff.dto.StaffAccountResponse;
import edu.cit.lim.gymtrack.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StaffCreationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StaffCreationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public StaffAccountResponse createStaffByAdmin(CreateStaffRequest request, String requesterEmail) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Requesting user not found."));

        if (requester.getRole() != Role.ADMIN) {
            throw new SecurityException("You don't have permission to create staff accounts.");
        }

        if (requester.getGym() == null) {
            throw new IllegalArgumentException("Your account isn't connected to a gym yet.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("That email is already registered.");
        }

        User staff = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.STAFF
        );
        staff.setGym(requester.getGym());
        staff.setMustChangePassword(true);

        User saved = userRepository.save(staff);
        return new StaffAccountResponse(
                saved.getId(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getEmail(),
                saved.getRole().name()
        );
    }
}
