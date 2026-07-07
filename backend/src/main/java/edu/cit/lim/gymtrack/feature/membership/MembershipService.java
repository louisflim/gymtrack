package edu.cit.lim.gymtrack.feature.membership;

import edu.cit.lim.gymtrack.entity.*;
import edu.cit.lim.gymtrack.feature.membership.dto.MembershipResponse;
import edu.cit.lim.gymtrack.repository.MembershipRepository;
import edu.cit.lim.gymtrack.repository.SubscriptionPlanRepository;
import edu.cit.lim.gymtrack.repository.UserRepository;
import edu.cit.lim.gymtrack.service.QrAttendanceService;
import edu.cit.lim.gymtrack.util.MembershipStatusUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final SubscriptionPlanRepository planRepository;
    private final UserRepository userRepository;

    public MembershipService(MembershipRepository membershipRepository,
                             SubscriptionPlanRepository planRepository,
                             UserRepository userRepository) {
        this.membershipRepository = membershipRepository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    public Optional<Membership> getLatestMembership(Long userId) {
        return membershipRepository.findTopByUserIdOrderByEndDateDesc(userId)
                .map(this::refreshIfNeeded);
    }

    public MembershipResponse getMembershipForEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return buildResponse(user);
    }

    public MembershipResponse getMembershipForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return buildResponse(user);
    }

    @Transactional
    public MembershipResponse activateMembership(Long userId, Long planId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));
        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found."));

        if (user.getRole() == Role.MEMBER) {
            if (user.getGym() == null) {
                throw new IllegalArgumentException("Scan the gym QR code to enroll before purchasing a plan.");
            }
            if (plan.getGym() == null || !plan.getGym().getId().equals(user.getGym().getId())) {
                throw new IllegalArgumentException("Plan does not belong to the member's gym.");
            }
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(plan.getDurationDays());

        Membership membership = membershipRepository.findTopByUserIdOrderByEndDateDesc(userId)
                .orElseGet(Membership::new);
        membership.setUser(user);
        membership.setPlan(plan);
        membership.setStartDate(startDate);
        membership.setEndDate(endDate);
        MembershipStatusUtil.refreshStatus(membership);

        membershipRepository.save(membership);
        return buildResponse(userRepository.findById(userId).orElseThrow());
    }

    public boolean memberCanAttend(Long userId) {
        return getLatestMembership(userId)
                .map(Membership::getStatus)
                .map(MembershipStatusUtil::allowsAttendance)
                .orElse(false);
    }

    private Membership refreshIfNeeded(Membership membership) {
        MembershipStatus computed = MembershipStatusUtil.computeStatus(membership.getEndDate());
        if (membership.getStatus() != computed) {
            membership.setStatus(computed);
            return membershipRepository.save(membership);
        }
        return membership;
    }

    private MembershipResponse buildResponse(User user) {
        Optional<Membership> membership = getLatestMembership(user.getId());
        MembershipStatus status = membership.map(Membership::getStatus).orElse(MembershipStatus.NONE);
        Gym gym = user.getGym();

        return new MembershipResponse(
                membership.map(Membership::getId).orElse(null),
                membership.map(m -> m.getPlan().getId()).orElse(null),
                membership.map(m -> m.getPlan().getName()).orElse(null),
                membership.map(m -> m.getStartDate().toString()).orElse(null),
                membership.map(m -> m.getEndDate().toString()).orElse(null),
                status.name(),
                gym != null ? gym.getId() : null,
                gym != null ? gym.getName() : null,
                user.isFirstCheckInCompleted(),
                QrAttendanceService.computeNextStep(user, status)
        );
    }
}
