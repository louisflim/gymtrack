package edu.cit.lim.gymtrack.feature.members;

import edu.cit.lim.gymtrack.feature.members.dto.AssignPlanRequest;
import edu.cit.lim.gymtrack.feature.members.dto.MemberUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<?> listMembers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(memberService.listMembers(authentication.getName(), search, status));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(
            @PathVariable Long id,
            @RequestBody MemberUpdateRequest request,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(memberService.updateMember(id, request, authentication.getName()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/assign-plan")
    public ResponseEntity<?> assignPlan(@RequestBody AssignPlanRequest request, Authentication authentication) {
        try {
            return ResponseEntity.ok(memberService.assignPlan(request, authentication.getName()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
