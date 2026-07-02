package edu.cit.lim.gymtrack.controller;

import edu.cit.lim.gymtrack.dto.AttendanceLogResponse;
import edu.cit.lim.gymtrack.dto.AttendanceScanResponse;
import edu.cit.lim.gymtrack.dto.ScanRequest;
import edu.cit.lim.gymtrack.service.QrAttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final QrAttendanceService qrAttendanceService;

    public AttendanceController(QrAttendanceService qrAttendanceService) {
        this.qrAttendanceService = qrAttendanceService;
    }

    @PostMapping("/scan")
    public ResponseEntity<?> scan(@RequestBody ScanRequest request) {
        try {
            AttendanceScanResponse response = qrAttendanceService.scanAttendance(request.getQrData());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<List<AttendanceLogResponse>> myLogs(Authentication authentication) {
        return ResponseEntity.ok(qrAttendanceService.getMyAttendanceLogs(authentication.getName()));
    }
}
