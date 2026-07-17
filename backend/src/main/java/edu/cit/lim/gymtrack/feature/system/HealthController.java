package edu.cit.lim.gymtrack.feature.system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public, unauthenticated root endpoint.
 * Exists purely so hitting the bare domain (e.g. Render health checks,
 * or someone opening the URL in a browser) returns a normal 200 instead
 * of a 403 from Spring Security's catch-all authenticated() rule.
 */
@RestController
public class HealthController {

    @GetMapping("/")
    public String health() {
        return "GymTrack API is running";
    }
}