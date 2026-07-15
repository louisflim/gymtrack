package edu.cit.lim.gymtrack.feature.account;

import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.feature.account.dto.DeleteAccountRequest;
import edu.cit.lim.gymtrack.repository.AttendanceLogRepository;
import edu.cit.lim.gymtrack.repository.MembershipRepository;
import edu.cit.lim.gymtrack.repository.PaymentRepository;
import edu.cit.lim.gymtrack.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final UserRepository userRepository;
    private final AttendanceLogRepository attendanceLogRepository;
    private final MembershipRepository membershipRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(UserRepository userRepository,
                          AttendanceLogRepository attendanceLogRepository,
                          MembershipRepository membershipRepository,
                          PaymentRepository paymentRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.attendanceLogRepository = attendanceLogRepository;
        this.membershipRepository = membershipRepository;
        this.paymentRepository = paymentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void deleteAccount(String email, DeleteAccountRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Please sign in again to continue."));

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Please enter your password to delete your account.");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Password is incorrect.");
        }

        Long userId = user.getId();
        attendanceLogRepository.deleteAll(attendanceLogRepository.findByUserIdOrderByCheckInTimeDesc(userId));
        membershipRepository.deleteAll(membershipRepository.findByUserIdOrderByEndDateDesc(userId));
        paymentRepository.deleteAll(paymentRepository.findByUserIdOrderByCreatedAtDesc(userId));
        userRepository.delete(user);
    }
}
