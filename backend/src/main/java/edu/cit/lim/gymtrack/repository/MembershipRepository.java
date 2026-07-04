package edu.cit.lim.gymtrack.repository;

import edu.cit.lim.gymtrack.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Optional<Membership> findTopByUserIdOrderByEndDateDesc(Long userId);
    List<Membership> findByUserIdOrderByEndDateDesc(Long userId);
}
