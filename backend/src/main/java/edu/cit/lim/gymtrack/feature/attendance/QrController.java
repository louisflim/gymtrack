package edu.cit.lim.gymtrack.feature.attendance;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qr")
public class QrController {

    private final AttendanceService attendanceService;

    public QrController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> myQr(Authentication authentication) {
        try {
            return ResponseEntity.ok(attendanceService.generateQrForUser(authentication.getName()));
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/gym")
    public ResponseEntity<?> gymQr(Authentication authentication) {
        try {
            return ResponseEntity.ok(attendanceService.generateGymQrForStaff(authentication.getName()));
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
