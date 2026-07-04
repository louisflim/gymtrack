package edu.cit.lim.gymtrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.cit.lim.gymtrack.entity.Role;
import edu.cit.lim.gymtrack.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    long countByRole(Role role);

    List<User> findByRoleAndGymId(Role role, Long gymId);

    @Query("""
        SELECT u FROM User u
        WHERE u.role = :memberRole
        AND u.gym.id = :gymId
        AND (:search IS NULL OR :search = '' OR
             LOWER(CONCAT(u.firstName, ' ', u.lastName, ' ', u.email)) LIKE LOWER(CONCAT('%', :search, '%')))
        ORDER BY u.lastName ASC, u.firstName ASC
        """)
    List<User> searchMembersByGym(@Param("search") String search,
                                  @Param("memberRole") Role memberRole,
                                  @Param("gymId") Long gymId);
}