package edu.cit.lim.gymtrack.feature.members;

import edu.cit.lim.gymtrack.feature.membership.dto.MembershipResponse;
import edu.cit.lim.gymtrack.entity.Gym;
import edu.cit.lim.gymtrack.entity.Membership;
import edu.cit.lim.gymtrack.entity.MembershipStatus;
import edu.cit.lim.gymtrack.entity.Role;
import edu.cit.lim.gymtrack.entity.SubscriptionPlan;
import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.feature.members.dto.AssignPlanRequest;
import edu.cit.lim.gymtrack.feature.members.dto.MemberResponse;
import edu.cit.lim.gymtrack.feature.members.dto.MemberUpdateRequest;
import edu.cit.lim.gymtrack.repository.SubscriptionPlanRepository;
import edu.cit.lim.gymtrack.repository.UserRepository;
import edu.cit.lim.gymtrack.feature.membership.MembershipService;
import edu.cit.lim.gymtrack.util.GymGuard;
import edu.cit.lim.gymtrack.util.UserDeletionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final UserRepository userRepository;
    private final SubscriptionPlanRepository planRepository;
    private final MembershipService membershipService;
    private final UserDeletionService userDeletionService;

    public MemberService(UserRepository userRepository,
                         SubscriptionPlanRepository planRepository,
                         MembershipService membershipService,
                         UserDeletionService userDeletionService) {
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.membershipService = membershipService;
        this.userDeletionService = userDeletionService;
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> listMembers(String requesterEmail, String search, String statusFilter) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Requesting user not found."));
        Gym gym = GymGuard.requireAdminGym(requester);

        List<User> users = (search == null || search.isBlank())
                ? userRepository.findByRoleAndGymId(Role.MEMBER, gym.getId())
                : userRepository.searchMembersByGym(search, Role.MEMBER, gym.getId());

        return users.stream()
                .map(this::toMemberResponse)
                .filter(member -> matchesStatus(member, statusFilter))
                .toList();
    }

    @Transactional
    public MemberResponse updateMember(Long memberId, MemberUpdateRequest request, String requesterEmail) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Requesting user not found."));
        Gym gym = GymGuard.requireAdminGym(requester);

        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));
        if (member.getRole() != Role.MEMBER) {
            throw new IllegalArgumentException("User is not a member.");
        }
        requireMemberAtGym(member, gym);

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            member.setFirstName(request.getFirstName().trim());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            member.setLastName(request.getLastName().trim());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String email = request.getEmail().trim();
            if (!email.equalsIgnoreCase(member.getEmail()) && userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email is already registered.");
            }
            member.setEmail(email);
        }
        if (request.getActive() != null) {
            member.setActive(request.getActive());
        }

        return toMemberResponse(userRepository.save(member));
    }

    @Transactional
    public void deleteMember(Long memberId, String requesterEmail) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Requesting user not found."));
        Gym gym = GymGuard.requireAdminGym(requester);

        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));
        if (member.getRole() != Role.MEMBER) {
            throw new IllegalArgumentException("User is not a member.");
        }
        requireMemberAtGym(member, gym);

        userDeletionService.deleteUserAndRelatedData(member);
    }

    public MembershipResponse assignPlan(AssignPlanRequest request, String requesterEmail) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Requesting user not found."));
        Gym gym = GymGuard.requireAdminGym(requester);

        if (request.getMemberId() == null || request.getPlanId() == null) {
            throw new IllegalArgumentException("Member ID and plan ID are required.");
        }

        User member = userRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));
        requireMemberAtGym(member, gym);

        SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found."));
        if (plan.getGym() == null || !plan.getGym().getId().equals(gym.getId())) {
            throw new IllegalArgumentException("Plan does not belong to your gym.");
        }

        return membershipService.activateMembership(request.getMemberId(), request.getPlanId());
    }

    private void requireMemberAtGym(User member, Gym gym) {
        if (member.getGym() == null || !member.getGym().getId().equals(gym.getId())) {
            throw new IllegalArgumentException("Member is not enrolled at your gym. Ask them to scan your gym QR code first.");
        }
    }

    private MemberResponse toMemberResponse(User member) {
        Optional<Membership> membership = membershipService.getLatestMembership(member.getId());
        String planName = membership.map(m -> m.getPlan().getName()).orElse(null);
        String status = membership.map(m -> m.getStatus().name()).orElse(MembershipStatus.NONE.name());
        String endDate = membership.map(m -> m.getEndDate().toString()).orElse(null);
        String gymName = member.getGym() != null ? member.getGym().getName() : null;

        return new MemberResponse(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.isActive(),
                planName,
                status,
                endDate,
                gymName,
                member.getGym() != null
        );
    }

    private boolean matchesStatus(MemberResponse member, String statusFilter) {
        if (statusFilter == null || statusFilter.isBlank() || "ALL".equalsIgnoreCase(statusFilter)) {
            return true;
        }
        return statusFilter.equalsIgnoreCase(member.getMembershipStatus());
    }
}
