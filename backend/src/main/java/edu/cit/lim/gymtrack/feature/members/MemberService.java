package edu.cit.lim.gymtrack.feature.members;

import edu.cit.lim.gymtrack.entity.Gym;
import edu.cit.lim.gymtrack.entity.Membership;
import edu.cit.lim.gymtrack.entity.MembershipStatus;
import edu.cit.lim.gymtrack.entity.Role;
import edu.cit.lim.gymtrack.entity.SubscriptionPlan;
import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.feature.members.dto.AssignPlanRequest;
import edu.cit.lim.gymtrack.feature.members.dto.MemberResponse;
import edu.cit.lim.gymtrack.feature.members.dto.MemberUpdateRequest;
import edu.cit.lim.gymtrack.feature.membership.MembershipService;
import edu.cit.lim.gymtrack.feature.membership.dto.MembershipResponse;
import edu.cit.lim.gymtrack.repository.MembershipRepository;
import edu.cit.lim.gymtrack.repository.SubscriptionPlanRepository;
import edu.cit.lim.gymtrack.repository.UserRepository;
import edu.cit.lim.gymtrack.util.GymGuard;
import edu.cit.lim.gymtrack.util.GymUserLookup;
import edu.cit.lim.gymtrack.util.RoleGuard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final UserRepository userRepository;
    private final SubscriptionPlanRepository planRepository;
    private final MembershipService membershipService;
    private final MembershipRepository membershipRepository;

    public MemberService(UserRepository userRepository,
                         SubscriptionPlanRepository planRepository,
                         MembershipService membershipService,
                         MembershipRepository membershipRepository) {
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.membershipService = membershipService;
        this.membershipRepository = membershipRepository;
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> listMembers(String requesterEmail, String search, String statusFilter) {
        Gym gym = adminGym(requesterEmail);
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
        Gym gym = adminGym(requesterEmail);
        User member = requireMember(memberId, gym);
        GymUserLookup.applyProfileUpdates(
                member,
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getActive(),
                userRepository
        );
        return toMemberResponse(userRepository.save(member));
    }

    /**
     * Removes a member from this gym only. The user account stays in the database
     * so they can sign in and join a gym again later.
     */
    @Transactional
    public void deleteMember(Long memberId, String requesterEmail) {
        Gym gym = adminGym(requesterEmail);
        User member = requireMember(memberId, gym);

        List<Membership> memberships = membershipRepository.findByUserIdOrderByEndDateDesc(member.getId());
        for (Membership membership : memberships) {
            if (membership.getStatus() != MembershipStatus.EXPIRED
                    && membership.getStatus() != MembershipStatus.NONE) {
                membership.setStatus(MembershipStatus.EXPIRED);
                membership.setEndDate(LocalDate.now());
                membershipRepository.save(membership);
            }
        }

        member.setGym(null);
        member.setFirstCheckInCompleted(false);
        userRepository.save(member);
    }

    public MembershipResponse assignPlan(AssignPlanRequest request, String requesterEmail) {
        Gym gym = adminGym(requesterEmail);
        if (request.getMemberId() == null || request.getPlanId() == null) {
            throw new IllegalArgumentException("Member ID and plan ID are required.");
        }

        requireMember(request.getMemberId(), gym);

        SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found."));
        if (plan.getGym() == null || !plan.getGym().getId().equals(gym.getId())) {
            throw new IllegalArgumentException("Plan does not belong to your gym.");
        }

        return membershipService.activateMembership(request.getMemberId(), request.getPlanId());
    }

    private Gym adminGym(String requesterEmail) {
        return GymGuard.requireAdminGym(RoleGuard.requireAdmin(userRepository, requesterEmail));
    }

    private User requireMember(Long memberId, Gym gym) {
        return GymUserLookup.requireUserAtGym(
                userRepository,
                memberId,
                gym,
                Role.MEMBER,
                "Member not found.",
                "User is not a member."
        );
    }

    private MemberResponse toMemberResponse(User member) {
        Optional<Membership> membership = membershipService.getLatestMembership(member.getId());
        return new MemberResponse(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.isActive(),
                membership.map(m -> m.getPlan().getName()).orElse(null),
                membership.map(m -> m.getStatus().name()).orElse(MembershipStatus.NONE.name()),
                membership.map(m -> m.getEndDate().toString()).orElse(null),
                member.getGym() != null ? member.getGym().getName() : null,
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
