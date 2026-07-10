package edu.cit.lim.gymtrack.repository;

import edu.cit.lim.gymtrack.entity.Payment;
import edu.cit.lim.gymtrack.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Payment> findAllByOrderByCreatedAtDesc();
    Optional<Payment> findByPaymongoCheckoutId(String paymongoCheckoutId);
    Optional<Payment> findByReferenceNumber(String referenceNumber);
    long countByStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.plan.gym.id = :gymId ORDER BY p.createdAt DESC")
    List<Payment> findByPlanGymIdOrderByCreatedAtDesc(@Param("gymId") Long gymId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.plan.gym.id = :gymId AND p.status = :status")
    java.math.BigDecimal sumAmountByGymIdAndStatus(@Param("gymId") Long gymId, @Param("status") PaymentStatus status);
}
