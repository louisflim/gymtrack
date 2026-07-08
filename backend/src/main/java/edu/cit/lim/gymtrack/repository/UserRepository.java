package edu.cit.lim.gymtrack.repository;

import edu.cit.lim.gymtrack.entity.Role;
import edu.cit.lim.gymtrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByRoleAndGymId(Role role, Long gymId);

    @Query("""
        SELECT u
        FROM User u
        WHERE u.role = :role
        AND u.gym.id = :gymId
        AND (
            LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    List<User> searchMembersByGym(
            @Param("search") String search,
            @Param("role") Role role,
            @Param("gymId") Long gymId
    );
}