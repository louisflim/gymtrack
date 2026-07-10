package edu.cit.lim.gymtrack.feature.staff;

import edu.cit.lim.gymtrack.entity.Gym;
import edu.cit.lim.gymtrack.entity.Role;
import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.feature.staff.dto.StaffResponse;
import edu.cit.lim.gymtrack.feature.staff.dto.StaffUpdateRequest;
import edu.cit.lim.gymtrack.repository.UserRepository;
import edu.cit.lim.gymtrack.util.GymGuard;
import edu.cit.lim.gymtrack.util.RoleGuard;
import edu.cit.lim.gymtrack.util.UserDeletionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StaffService {

    private final UserRepository userRepository;
    private final UserDeletionService userDeletionService;

    public StaffService(UserRepository userRepository, UserDeletionService userDeletionService) {
        this.userRepository = userRepository;
        this.userDeletionService = userDeletionService;
    }

    @Transactional(readOnly = true)
    public List<StaffResponse> listStaff(String requesterEmail) {
        User requester = RoleGuard.requireAdmin(userRepository, requesterEmail);
        Gym gym = GymGuard.requireAdminGym(requester);

        return userRepository.findByRoleAndGymId(Role.STAFF, gym.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public StaffResponse updateStaff(Long staffId, StaffUpdateRequest request, String requesterEmail) {
        User requester = RoleGuard.requireAdmin(userRepository, requesterEmail);
        Gym gym = GymGuard.requireAdminGym(requester);

        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff account not found."));

        if (staff.getRole() != Role.STAFF) {
            throw new IllegalArgumentException("User is not a staff account.");
        }
        if (staff.getGym() == null || !staff.getGym().getId().equals(gym.getId())) {
            throw new IllegalArgumentException("Staff account does not belong to your gym.");
        }

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            staff.setFirstName(request.getFirstName().trim());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            staff.setLastName(request.getLastName().trim());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String email = request.getEmail().trim();
            if (!email.equalsIgnoreCase(staff.getEmail()) && userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email is already registered.");
            }
            staff.setEmail(email);
        }
        if (request.getActive() != null) {
            staff.setActive(request.getActive());
        }

        return toResponse(userRepository.save(staff));
    }

    @Transactional
    public void deleteStaff(Long staffId, String requesterEmail) {
        User requester = RoleGuard.requireAdmin(userRepository, requesterEmail);
        Gym gym = GymGuard.requireAdminGym(requester);

        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff account not found."));

        if (staff.getRole() != Role.STAFF) {
            throw new IllegalArgumentException("User is not a staff account.");
        }
        if (staff.getGym() == null || !staff.getGym().getId().equals(gym.getId())) {
            throw new IllegalArgumentException("Staff account does not belong to your gym.");
        }

        userDeletionService.deleteUserAndRelatedData(staff);
    }

    private StaffResponse toResponse(User staff) {
        return new StaffResponse(
                staff.getId(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getEmail(),
                staff.isActive()
        );
    }
}
