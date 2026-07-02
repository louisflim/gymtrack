package edu.cit.lim.gymtrack.repository;

import edu.cit.lim.gymtrack.entity.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {
    Optional<AttendanceLog> findTopByUserIdOrderByCheckInTimeDesc(Long userId);

    List<AttendanceLog> findByUserIdOrderByCheckInTimeDesc(Long userId);
}
