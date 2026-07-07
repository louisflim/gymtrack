package edu.cit.lim.gymtrack.feature.membership;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/membership")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> myMembership(Authentication authentication) {
        return ResponseEntity.ok(membershipService.getMembershipForEmail(authentication.getName()));
    }
}
