package edu.cit.lim.gymtrack.service;

import edu.cit.lim.gymtrack.dto.DashboardStatsResponse;
import edu.cit.lim.gymtrack.entity.Gym;
import edu.cit.lim.gymtrack.entity.MembershipStatus;
import edu.cit.lim.gymtrack.entity.PaymentStatus;
import edu.cit.lim.gymtrack.entity.Role;
import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.repository.AttendanceLogRepository;
import edu.cit.lim.gymtrack.repository.PaymentRepository;
import edu.cit.lim.gymtrack.repository.UserRepository;
import edu.cit.lim.gymtrack.util.GymGuard;
import edu.cit.lim.gymtrack.util.MembershipStatusUtil;
import edu.cit.lim.gymtrack.util.RoleGuard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final MembershipService membershipService;
    private final AttendanceLogRepository attendanceLogRepository;
    private final PaymentRepository paymentRepository;

    public DashboardService(UserRepository userRepository,
                            MembershipService membershipService,
                            AttendanceLogRepository attendanceLogRepository,
                            PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.membershipService = membershipService;
        this.attendanceLogRepository = attendanceLogRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats(String requesterEmail) {
        User requester = RoleGuard.requireAdmin(userRepository, requesterEmail);
        Gym gym = GymGuard.requireAdminGym(requester);

        List<User> members = userRepository.findByRoleAndGymId(Role.MEMBER, gym.getId());
        long active = 0;
        long expired = 0;
        for (User member : members) {
            MembershipStatus status = membershipService.getLatestMembership(member.getId())
                    .map(m -> {
                        MembershipStatusUtil.refreshStatus(m);
                        return m.getStatus();
                    })
                    .orElse(MembershipStatus.NONE);

            if (status == MembershipStatus.ACTIVE || status == MembershipStatus.EXPIRING_SOON) {
                active++;
            } else if (status == MembershipStatus.EXPIRED) {
                expired++;
            }
        }

        LocalDate today = LocalDate.now();
        long todayCheckIns = attendanceLogRepository.countGymCheckInsBetween(
                gym.getId(),
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );

        BigDecimal totalCollected = paymentRepository.sumAmountByGymIdAndStatus(gym.getId(), PaymentStatus.PAID);
        if (totalCollected == null) {
            totalCollected = BigDecimal.ZERO;
        }

        return new DashboardStatsResponse(
                members.size(),
                active,
                expired,
                todayCheckIns,
                totalCollected
        );
    }
}
