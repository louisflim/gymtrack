package edu.cit.lim.gymtrack.controller;

import edu.cit.lim.gymtrack.dto.PlanRequest;
import edu.cit.lim.gymtrack.dto.PlanResponse;
import edu.cit.lim.gymtrack.service.PlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping("/active")
    public ResponseEntity<?> listActivePlans(Authentication authentication) {
        try {
            return ResponseEntity.ok(planService.listActivePlans(authentication.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listAllPlans(Authentication authentication) {
        try {
            return ResponseEntity.ok(planService.listAllPlans(authentication.getName()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createPlan(@RequestBody PlanRequest request, Authentication authentication) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(planService.createPlan(request, authentication.getName()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlan(@PathVariable Long id, @RequestBody PlanRequest request, Authentication authentication) {
        try {
            return ResponseEntity.ok(planService.updatePlan(id, request, authentication.getName()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
