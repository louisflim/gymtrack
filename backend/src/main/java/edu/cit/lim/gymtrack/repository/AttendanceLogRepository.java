package edu.cit.lim.gymtrack.repository;

import edu.cit.lim.gymtrack.entity.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {
    Optional<AttendanceLog> findTopByUserIdOrderByCheckInTimeDesc(Long userId);

    List<AttendanceLog> findByUserIdOrderByCheckInTimeDesc(Long userId);

    @Query("""
        SELECT al FROM AttendanceLog al
        JOIN FETCH al.user u
        WHERE u.gym.id = :gymId
        AND (:search IS NULL OR :search = '' OR
             LOWER(CONCAT(u.firstName, ' ', u.lastName, ' ', u.email)) LIKE LOWER(CONCAT('%', :search, '%')))
        ORDER BY al.checkInTime DESC
        """)
    List<AttendanceLog> findGymAttendance(@Param("gymId") Long gymId, @Param("search") String search);

    @Query("""
        SELECT al FROM AttendanceLog al
        JOIN FETCH al.user u
        WHERE u.gym.id = :gymId
        AND (:search IS NULL OR :search = '' OR
             LOWER(CONCAT(u.firstName, ' ', u.lastName, ' ', u.email)) LIKE LOWER(CONCAT('%', :search, '%')))
        AND al.checkInTime >= :start
        AND al.checkInTime < :end
        ORDER BY al.checkInTime DESC
        """)
    List<AttendanceLog> findGymAttendanceByDate(
            @Param("gymId") Long gymId,
            @Param("search") String search,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT COUNT(al) FROM AttendanceLog al
        JOIN al.user u
        WHERE u.gym.id = :gymId
        AND al.checkInTime >= :start
        AND al.checkInTime < :end
        """)
    long countGymCheckInsBetween(
            @Param("gymId") Long gymId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
