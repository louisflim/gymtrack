package edu.cit.lim.gymtrack.repository;

import edu.cit.lim.gymtrack.entity.Gym;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GymRepository extends JpaRepository<Gym, Long> {
    Optional<Gym> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
