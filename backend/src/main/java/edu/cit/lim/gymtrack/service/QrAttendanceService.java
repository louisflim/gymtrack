package edu.cit.lim.gymtrack.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import edu.cit.lim.gymtrack.dto.AttendanceLogResponse;
import edu.cit.lim.gymtrack.dto.AttendanceScanResponse;
import edu.cit.lim.gymtrack.dto.QrCodeResponse;
import edu.cit.lim.gymtrack.entity.AttendanceLog;
import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.repository.AttendanceLogRepository;
import edu.cit.lim.gymtrack.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class QrAttendanceService {

    private final UserRepository userRepository;
    private final AttendanceLogRepository attendanceLogRepository;

    public QrAttendanceService(UserRepository userRepository, AttendanceLogRepository attendanceLogRepository) {
        this.userRepository = userRepository;
        this.attendanceLogRepository = attendanceLogRepository;
    }

    public QrCodeResponse generateQrForUser(String email) {
        User user = findUserByEmail(email);
        if (user.getRole() != edu.cit.lim.gymtrack.entity.Role.MEMBER) {
            throw new SecurityException("Only MEMBER accounts have personal QR codes.");
        }
        String payload = buildQrPayload(user.getId());
        return new QrCodeResponse(payload, generateQrBase64(payload));
    }

    public AttendanceScanResponse scanAttendance(String qrData) {
        Long userId = parseUserIdFromQr(qrData);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Scanned member does not exist."));

        if (!user.isActive()) {
            throw new IllegalArgumentException("Scanned account is deactivated.");
        }

        LocalDateTime now = LocalDateTime.now();
        AttendanceLog latest = attendanceLogRepository
                .findTopByUserIdOrderByCheckInTimeDesc(user.getId())
                .orElse(null);

        String action;
        String timestamp;
        if (latest != null && latest.getCheckOutTime() == null) {
            latest.setCheckOutTime(now);
            attendanceLogRepository.save(latest);
            action = "CHECK_OUT";
            timestamp = latest.getCheckOutTime().toString();
        } else {
            AttendanceLog entry = new AttendanceLog();
            entry.setUser(user);
            entry.setCheckInTime(now);
            attendanceLogRepository.save(entry);
            action = "CHECK_IN";
            timestamp = entry.getCheckInTime().toString();
        }

        return new AttendanceScanResponse(
                action,
                user.getFullName(),
                user.getRole().name(),
                user.isActive(),
                timestamp,
                action.equals("CHECK_IN") ? "Member checked in successfully." : "Member checked out successfully."
        );
    }

    public List<AttendanceLogResponse> getMyAttendanceLogs(String email) {
        User user = findUserByEmail(email);
        return attendanceLogRepository.findByUserIdOrderByCheckInTimeDesc(user.getId())
                .stream()
                .map(log -> new AttendanceLogResponse(
                        log.getCheckInTime().toString(),
                        log.getCheckOutTime() == null ? null : log.getCheckOutTime().toString()
                ))
                .toList();
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private String buildQrPayload(Long userId) {
        return "GYMTRACK|USER|" + userId;
    }

    private Long parseUserIdFromQr(String qrData) {
        if (qrData == null || qrData.isBlank()) {
            throw new IllegalArgumentException("QR data is required.");
        }

        String[] parts = qrData.split("\\|");
        if (parts.length != 3 || !"GYMTRACK".equals(parts[0]) || !"USER".equals(parts[1])) {
            throw new IllegalArgumentException("Invalid GymTrack QR format.");
        }

        try {
            return Long.parseLong(parts[2]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid member QR code.");
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
