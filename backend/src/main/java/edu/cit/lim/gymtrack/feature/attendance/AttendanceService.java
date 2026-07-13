package edu.cit.lim.gymtrack.feature.attendance;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import edu.cit.lim.gymtrack.feature.attendance.dto.AttendanceLogResponse;
import edu.cit.lim.gymtrack.feature.attendance.dto.AttendanceScanResponse;
import edu.cit.lim.gymtrack.feature.attendance.dto.MemberGymScanResponse;
import edu.cit.lim.gymtrack.feature.attendance.dto.QrCodeResponse;
import edu.cit.lim.gymtrack.entity.AttendanceLog;
import edu.cit.lim.gymtrack.entity.Gym;
import edu.cit.lim.gymtrack.entity.Membership;
import edu.cit.lim.gymtrack.entity.MembershipStatus;
import edu.cit.lim.gymtrack.entity.Role;
import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.feature.membership.MembershipService;
import edu.cit.lim.gymtrack.repository.AttendanceLogRepository;
import edu.cit.lim.gymtrack.repository.GymRepository;
import edu.cit.lim.gymtrack.repository.UserRepository;
import edu.cit.lim.gymtrack.util.GymGuard;
import edu.cit.lim.gymtrack.util.MembershipStatusUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class AttendanceService {

    public static final String NEXT_ENROLL_AT_GYM = "ENROLL_AT_GYM";
    public static final String NEXT_PURCHASE_PLAN = "PURCHASE_PLAN";
    public static final String NEXT_FIRST_CHECK_IN = "FIRST_CHECK_IN";
    public static final String NEXT_ACTIVE = "ACTIVE";

    private final UserRepository userRepository;
    private final GymRepository gymRepository;
    private final AttendanceLogRepository attendanceLogRepository;
    private final MembershipService membershipService;

    public AttendanceService(UserRepository userRepository,
                               GymRepository gymRepository,
                               AttendanceLogRepository attendanceLogRepository,
                               MembershipService membershipService) {
        this.userRepository = userRepository;
        this.gymRepository = gymRepository;
        this.attendanceLogRepository = attendanceLogRepository;
        this.membershipService = membershipService;
    }

    public QrCodeResponse generateQrForUser(String email) {
        User user = findUserByEmail(email);
        if (user.getRole() != Role.MEMBER) {
            throw new SecurityException("Personal QR codes are only available on member accounts.");
        }
        if (user.getGym() == null) {
            throw new IllegalArgumentException("Join a gym first before your member QR code is available.");
        }
        Membership membership = membershipService.getLatestMembership(user.getId()).orElse(null);
        MembershipStatus status = membership != null ? membership.getStatus() : MembershipStatus.NONE;
        if (!MembershipStatusUtil.allowsAttendance(status)) {
            throw new IllegalArgumentException("Get an active membership plan before your QR code is available.");
        }
        String payload = buildMemberQrPayload(user.getId());
        return new QrCodeResponse(payload, generateQrBase64(payload));
    }

    public QrCodeResponse generateGymQrForStaff(String email) {
        User staff = findUserByEmail(email);
        Gym gym = GymGuard.requireStaffGym(staff);
        String payload = buildGymQrPayload(gym.getId());
        return new QrCodeResponse(payload, generateQrBase64(payload));
    }

    @Transactional
    public MemberGymScanResponse scanGymQrAsMember(String qrData, String memberEmail) {
        User member = findUserByEmail(memberEmail);
        if (member.getRole() != Role.MEMBER) {
            throw new SecurityException("Only members can scan gym QR codes.");
        }
        if (!member.isActive()) {
            throw new IllegalArgumentException("Your account has been deactivated. Please contact your gym.");
        }

        Long gymId = parseGymIdFromQr(qrData);
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new IllegalArgumentException("Gym not found."));

        Membership membership = membershipService.getLatestMembership(member.getId()).orElse(null);
        String planName = membership != null ? membership.getPlan().getName() : null;
        MembershipStatus membershipStatus = membership != null
                ? membership.getStatus()
                : MembershipStatus.NONE;

        if (member.getGym() == null) {
            member.setGym(gym);
            userRepository.save(member);
            return new MemberGymScanResponse(
                    "ENROLLED",
                    "Welcome to " + gym.getName() + "! Choose a subscription plan to continue.",
                    gym.getName(),
                    membershipStatus.name(),
                    planName,
                    false,
                    NEXT_PURCHASE_PLAN
            );
        }

        if (!member.getGym().getId().equals(gym.getId())) {
            throw new IllegalArgumentException(
                    "You are enrolled at " + member.getGym().getName()
                            + ". Scan the QR code at that gym instead."
            );
        }

        if (!MembershipStatusUtil.allowsAttendance(membershipStatus)) {
            throw new IllegalArgumentException(
                    "Purchase an active subscription, then show your member QR to staff for check-in."
            );
        }

        throw new IllegalArgumentException(
                "Show your member QR code to staff for check-in. Only staff scan member QR codes."
        );
    }

    @Transactional
    public AttendanceScanResponse scanAttendance(String qrData, String scannerEmail) {
        User scanner = findUserByEmail(scannerEmail);
        Gym scannerGym = GymGuard.requireStaffGym(scanner);

        Long userId = parseMemberIdFromQr(qrData);
        User member = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Scanned member does not exist."));

        if (member.getRole() != Role.MEMBER) {
            throw new IllegalArgumentException("Only member QR codes can be scanned for attendance.");
        }

        if (!member.isActive()) {
            throw new IllegalArgumentException("Scanned account is deactivated.");
        }

        if (member.getGym() == null) {
            throw new IllegalArgumentException(
                    "Member has not enrolled yet. Ask them to scan the gym QR code first."
            );
        }

        if (!member.getGym().getId().equals(scannerGym.getId())) {
            throw new IllegalArgumentException(
                    "This member is not registered at " + scannerGym.getName()
                            + ". They belong to " + member.getGym().getName() + "."
            );
        }

        Membership membership = membershipService.getLatestMembership(member.getId()).orElse(null);
        String planName = membership != null ? membership.getPlan().getName() : null;
        MembershipStatus membershipStatus = membership != null
                ? membership.getStatus()
                : MembershipStatus.NONE;

        LocalDateTime now = LocalDateTime.now();
        AttendanceLog latest = attendanceLogRepository
                .findTopByUserIdOrderByCheckInTimeDesc(member.getId())
                .orElse(null);

        boolean isCheckOut = latest != null && latest.getCheckOutTime() == null;
        if (!isCheckOut && !MembershipStatusUtil.allowsAttendance(membershipStatus)) {
            throw new IllegalArgumentException("Membership is expired or unpaid. Check-in denied.");
        }

        boolean completedFirstCheckIn = false;
        String action;
        String timestamp;
        if (isCheckOut) {
            latest.setCheckOutTime(now);
            attendanceLogRepository.save(latest);
            action = "CHECK_OUT";
            timestamp = latest.getCheckOutTime().toString();
        } else {
            AttendanceLog entry = new AttendanceLog();
            entry.setUser(member);
            entry.setCheckInTime(now);
            attendanceLogRepository.save(entry);
            action = "CHECK_IN";
            timestamp = entry.getCheckInTime().toString();

            if (!member.isFirstCheckInCompleted()) {
                member.setFirstCheckInCompleted(true);
                userRepository.save(member);
                completedFirstCheckIn = true;
            }
        }

        String message;
        if (completedFirstCheckIn) {
            message = "First check-in complete for " + member.getFullName() + ".";
        } else if (action.equals("CHECK_IN")) {
            message = "Member checked in successfully.";
        } else {
            message = "Member checked out successfully.";
        }

        return new AttendanceScanResponse(
                action,
                member.getFullName(),
                member.getRole().name(),
                member.isActive(),
                planName,
                membershipStatus.name(),
                timestamp,
                message,
                scannerGym.getName(),
                true
        );
    }

    public List<AttendanceLogResponse> getMyAttendanceLogs(String email) {
        User user = findUserByEmail(email);
        return attendanceLogRepository.findByUserIdOrderByCheckInTimeDesc(user.getId())
                .stream()
                .map(this::toMemberLogResponse)
                .toList();
    }

    public List<AttendanceLogResponse> getGymAttendanceLogs(String requesterEmail, String search, String date) {
        User requester = findUserByEmail(requesterEmail);
        if (requester.getRole() != Role.ADMIN) {
            throw new SecurityException("Only gym owners can view attendance records.");
        }
        Gym gym = GymGuard.requireAdminGym(requester);

        List<AttendanceLog> logs;
        if (date != null && !date.isBlank()) {
            LocalDate filterDate = LocalDate.parse(date);
            logs = attendanceLogRepository.findGymAttendanceByDate(
                    gym.getId(),
                    search,
                    filterDate.atStartOfDay(),
                    filterDate.plusDays(1).atStartOfDay()
            );
        } else {
            logs = attendanceLogRepository.findGymAttendance(gym.getId(), search);
        }

        return logs.stream().map(this::toAdminLogResponse).toList();
    }

    private AttendanceLogResponse toMemberLogResponse(AttendanceLog log) {
        return new AttendanceLogResponse(
                log.getCheckInTime().toString(),
                log.getCheckOutTime() == null ? null : log.getCheckOutTime().toString()
        );
    }

    private AttendanceLogResponse toAdminLogResponse(AttendanceLog log) {
        User member = log.getUser();
        return new AttendanceLogResponse(
                log.getId(),
                member.getId(),
                member.getFullName(),
                log.getCheckInTime().toString(),
                log.getCheckOutTime() == null ? null : log.getCheckOutTime().toString()
        );
    }

    public static String computeNextStep(User member, MembershipStatus membershipStatus) {
        if (member.getGym() == null) {
            return NEXT_ENROLL_AT_GYM;
        }
        if (!MembershipStatusUtil.allowsAttendance(membershipStatus)) {
            return NEXT_PURCHASE_PLAN;
        }
        if (!member.isFirstCheckInCompleted()) {
            return NEXT_FIRST_CHECK_IN;
        }
        return NEXT_ACTIVE;
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private String buildMemberQrPayload(Long userId) {
        return "GYMTRACK|USER|" + userId;
    }

    private String buildGymQrPayload(Long gymId) {
        return "GYMTRACK|GYM|" + gymId;
    }

    private Long parseMemberIdFromQr(String qrData) {
        if (qrData == null || qrData.isBlank()) {
            throw new IllegalArgumentException("QR data is required.");
        }
        String[] parts = qrData.split("\\|");
        if (parts.length != 3 || !"GYMTRACK".equals(parts[0]) || !"USER".equals(parts[1])) {
            throw new IllegalArgumentException("Invalid member QR format.");
        }
        try {
            return Long.parseLong(parts[2]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid member QR code.");
        }
    }

    private Long parseGymIdFromQr(String qrData) {
        if (qrData == null || qrData.isBlank()) {
            throw new IllegalArgumentException("QR data is required.");
        }
        String[] parts = qrData.split("\\|");
        if (parts.length != 3 || !"GYMTRACK".equals(parts[0]) || !"GYM".equals(parts[1])) {
            throw new IllegalArgumentException("Invalid gym QR format. Scan the QR displayed by staff or admin.");
        }
        try {
            return Long.parseLong(parts[2]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid gym QR code.");
        }
    }

    private String generateQrBase64(String payload) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(payload, BarcodeFormat.QR_CODE, 320, 320);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (WriterException | IOException e) {
            throw new IllegalStateException("Failed to generate QR code image.", e);
        }
    }
}

