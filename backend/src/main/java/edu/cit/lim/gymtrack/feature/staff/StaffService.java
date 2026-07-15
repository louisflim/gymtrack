package edu.cit.lim.gymtrack.feature.staff;

import edu.cit.lim.gymtrack.entity.Gym;
import edu.cit.lim.gymtrack.entity.Role;
import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.feature.staff.dto.StaffResponse;
import edu.cit.lim.gymtrack.feature.staff.dto.StaffUpdateRequest;
import edu.cit.lim.gymtrack.repository.UserRepository;
import edu.cit.lim.gymtrack.util.GymGuard;
import edu.cit.lim.gymtrack.util.GymUserLookup;
import edu.cit.lim.gymtrack.util.RoleGuard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StaffService {

    private final UserRepository userRepository;

    public StaffService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<StaffResponse> listStaff(String requesterEmail) {
        Gym gym = adminGym(requesterEmail);
        return userRepository.findByRoleAndGymId(Role.STAFF, gym.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public StaffResponse updateStaff(Long staffId, StaffUpdateRequest request, String requesterEmail) {
        Gym gym = adminGym(requesterEmail);
        User staff = requireStaff(staffId, gym);
        GymUserLookup.applyProfileUpdates(
                staff,
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getActive(),
                userRepository
        );
        return toResponse(userRepository.save(staff));
    }

    private Gym adminGym(String requesterEmail) {
        return GymGuard.requireAdminGym(RoleGuard.requireAdmin(userRepository, requesterEmail));
    }

    private User requireStaff(Long staffId, Gym gym) {
        return GymUserLookup.requireUserAtGym(
                userRepository,
                staffId,
                gym,
                Role.STAFF,
                "Staff account not found.",
                "User is not a staff account."
        );
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
