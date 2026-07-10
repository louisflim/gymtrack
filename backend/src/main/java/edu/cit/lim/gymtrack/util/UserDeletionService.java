package edu.cit.lim.gymtrack.util;

import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.repository.AttendanceLogRepository;
import edu.cit.lim.gymtrack.repository.MembershipRepository;
import edu.cit.lim.gymtrack.repository.PaymentRepository;
import edu.cit.lim.gymtrack.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDeletionService {

    private final UserRepository userRepository;
    private final AttendanceLogRepository attendanceLogRepository;
    private final MembershipRepository membershipRepository;
    private final PaymentRepository paymentRepository;

    public UserDeletionService(UserRepository userRepository,
                               AttendanceLogRepository attendanceLogRepository,
                               MembershipRepository membershipRepository,
                               PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.attendanceLogRepository = attendanceLogRepository;
        this.membershipRepository = membershipRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void deleteUserAndRelatedData(User user) {
        Long userId = user.getId();
        attendanceLogRepository.deleteAll(attendanceLogRepository.findByUserIdOrderByCheckInTimeDesc(userId));
        membershipRepository.deleteAll(membershipRepository.findByUserIdOrderByEndDateDesc(userId));
        paymentRepository.deleteAll(paymentRepository.findByUserIdOrderByCreatedAtDesc(userId));
        userRepository.delete(user);
    }
}
