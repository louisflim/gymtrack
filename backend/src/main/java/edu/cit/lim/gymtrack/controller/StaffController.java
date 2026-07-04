package edu.cit.lim.gymtrack.controller;

import edu.cit.lim.gymtrack.dto.StaffResponse;
import edu.cit.lim.gymtrack.dto.StaffUpdateRequest;
import edu.cit.lim.gymtrack.service.StaffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public ResponseEntity<?> listStaff(Authentication authentication) {
        try {
            List<StaffResponse> staff = staffService.listStaff(authentication.getName());
            return ResponseEntity.ok(staff);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(
            @PathVariable Long id,
            @RequestBody StaffUpdateRequest request,
            Authentication authentication) {
        try {
            StaffResponse updated = staffService.updateStaff(id, request, authentication.getName());
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
