package edu.cit.lim.gymtrack.feature.account;

import edu.cit.lim.gymtrack.feature.account.dto.DeleteAccountRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @DeleteMapping("/account")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteAccount(
            @RequestBody DeleteAccountRequest request,
            Authentication authentication) {
        try {
            accountService.deleteAccount(authentication.getName(), request);
            return ResponseEntity.ok(Map.of("message", "Account deleted successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
