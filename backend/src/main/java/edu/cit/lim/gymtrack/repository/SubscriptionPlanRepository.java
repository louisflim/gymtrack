package edu.cit.lim.gymtrack.repository;

import edu.cit.lim.gymtrack.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    List<SubscriptionPlan> findByActiveTrueOrderByNameAsc();
    List<SubscriptionPlan> findAllByOrderByNameAsc();
    List<SubscriptionPlan> findByGymIdAndActiveTrueOrderByNameAsc(Long gymId);
    List<SubscriptionPlan> findByGymIdOrderByNameAsc(Long gymId);
}
