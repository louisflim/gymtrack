package edu.cit.lim.gymtrack.controller;

import edu.cit.lim.gymtrack.dto.QrCodeResponse;
import edu.cit.lim.gymtrack.service.QrAttendanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qr")
public class QrController {

    private final QrAttendanceService qrAttendanceService;

    public QrController(QrAttendanceService qrAttendanceService) {
        this.qrAttendanceService = qrAttendanceService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> myQr(Authentication authentication) {
        try {
            return ResponseEntity.ok(qrAttendanceService.generateQrForUser(authentication.getName()));
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/gym")
    public ResponseEntity<?> gymQr(Authentication authentication) {
        try {
            return ResponseEntity.ok(qrAttendanceService.generateGymQrForStaff(authentication.getName()));
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
