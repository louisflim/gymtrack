package edu.cit.lim.gymtrack.feature.auth.staff;

import edu.cit.lim.gymtrack.feature.auth.staff.dto.CreateStaffRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class StaffCreationController {

    private final StaffCreationService staffCreationService;

    public StaffCreationController(StaffCreationService staffCreationService) {
        this.staffCreationService = staffCreationService;
    }

    @PostMapping("/staff")
    public ResponseEntity<?> createStaff(@RequestBody CreateStaffRequest request, Authentication authentication) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(staffCreationService.createStaffByAdmin(request, authentication.getName()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
