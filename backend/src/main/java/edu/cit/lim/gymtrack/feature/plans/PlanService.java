package edu.cit.lim.gymtrack.feature.plans;

import edu.cit.lim.gymtrack.entity.Gym;
import edu.cit.lim.gymtrack.entity.Role;
import edu.cit.lim.gymtrack.entity.SubscriptionPlan;
import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.feature.plans.dto.PlanRequest;
import edu.cit.lim.gymtrack.feature.plans.dto.PlanResponse;
import edu.cit.lim.gymtrack.repository.SubscriptionPlanRepository;
import edu.cit.lim.gymtrack.repository.UserRepository;
import edu.cit.lim.gymtrack.util.GymGuard;
import edu.cit.lim.gymtrack.util.RoleGuard;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class PlanService {

    private final SubscriptionPlanRepository planRepository;
    private final UserRepository userRepository;

    public PlanService(SubscriptionPlanRepository planRepository, UserRepository userRepository) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    public List<PlanResponse> listActivePlans(String requesterEmail) {
        User user = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (user.getRole() == Role.MEMBER) {
            if (user.getGym() == null) {
                return Collections.emptyList();
            }
            return planRepository.findByGymIdAndActiveTrueOrderByNameAsc(user.getGym().getId()).stream()
                    .map(this::toResponse)
                    .toList();
        }

        if (user.getRole() == Role.ADMIN || user.getRole() == Role.STAFF) {
            Gym gym = GymGuard.requireStaffGym(user);
            return planRepository.findByGymIdAndActiveTrueOrderByNameAsc(gym.getId()).stream()
                    .map(this::toResponse)
                    .toList();
        }

        return Collections.emptyList();
    }

    public List<PlanResponse> listAllPlans(String requesterEmail) {
        User requester = RoleGuard.requireAdmin(userRepository, requesterEmail);
        Gym gym = GymGuard.requireAdminGym(requester);

        return planRepository.findByGymIdOrderByNameAsc(gym.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    public PlanResponse createPlan(PlanRequest request, String requesterEmail) {
        User requester = RoleGuard.requireAdmin(userRepository, requesterEmail);
        Gym gym = GymGuard.requireAdminGym(requester);
        validateRequest(request);

        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(request.getName().trim());
        plan.setDurationDays(request.getDurationDays());
        plan.setPrice(request.getPrice());
        plan.setActive(request.getActive() == null || request.getActive());
        plan.setGym(gym);
        return toResponse(planRepository.save(plan));
    }

    public PlanResponse updatePlan(Long id, PlanRequest request, String requesterEmail) {
        User requester = RoleGuard.requireAdmin(userRepository, requesterEmail);
        Gym gym = GymGuard.requireAdminGym(requester);
        validateRequest(request);

        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found."));
        requirePlanAtGym(plan, gym);

        plan.setName(request.getName().trim());
        plan.setDurationDays(request.getDurationDays());
        plan.setPrice(request.getPrice());
        if (request.getActive() != null) {
            plan.setActive(request.getActive());
        }
        return toResponse(planRepository.save(plan));
    }

    public SubscriptionPlan getActivePlan(Long planId) {
        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found."));
        if (!plan.isActive()) {
            throw new IllegalArgumentException("Plan is not active.");
        }
        return plan;
    }

    private void requirePlanAtGym(SubscriptionPlan plan, Gym gym) {
        if (plan.getGym() == null || !plan.getGym().getId().equals(gym.getId())) {
            throw new IllegalArgumentException("Plan does not belong to your gym.");
        }
    }

    private void validateRequest(PlanRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Plan name is required.");
        }
        if (request.getDurationDays() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than zero.");
        }
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
    }

    private PlanResponse toResponse(SubscriptionPlan plan) {
        return new PlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getDurationDays(),
                plan.getPrice(),
                plan.isActive(),
                plan.getCreatedAt()
        );
    }
}
