package edu.cit.lim.gymtrack.feature.attendance;

import edu.cit.lim.gymtrack.feature.attendance.dto.AttendanceLogResponse;
import edu.cit.lim.gymtrack.feature.attendance.dto.AttendanceScanResponse;
import edu.cit.lim.gymtrack.feature.attendance.dto.ScanRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/scan-gym")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<?> scanGym(@RequestBody ScanRequest request, Authentication authentication) {
        try {
            return ResponseEntity.ok(attendanceService.scanGymQrAsMember(
                    request.getQrData(),
                    authentication.getName()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (SecurityException ex) {
            return ResponseEntity.status(403).body(ex.getMessage());
        }
    }

    @PostMapping("/scan")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> scan(@RequestBody ScanRequest request, Authentication authentication) {
        try {
            AttendanceScanResponse response = attendanceService.scanAttendance(
                    request.getQrData(),
                    authentication.getName()
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (SecurityException ex) {
            return ResponseEntity.status(403).body(ex.getMessage());
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<AttendanceLogResponse>> myLogs(Authentication authentication) {
        return ResponseEntity.ok(attendanceService.getMyAttendanceLogs(authentication.getName()));
    }

    @GetMapping("/gym")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> gymLogs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String date,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(attendanceService.getGymAttendanceLogs(
                    authentication.getName(), search, date));
        } catch (SecurityException ex) {
            return ResponseEntity.status(403).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
