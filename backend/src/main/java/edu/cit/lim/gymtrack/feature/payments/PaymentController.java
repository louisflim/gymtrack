package edu.cit.lim.gymtrack.feature.payments;

import edu.cit.lim.gymtrack.feature.payments.dto.CheckoutRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest request, Authentication authentication) {
        try {
            return ResponseEntity.ok(paymentService.createCheckout(request.getPlanId(), authentication.getName()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody String payload) {
        paymentService.handleWebhook(payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm-mock")
    public ResponseEntity<?> confirmMock(@RequestParam String reference) {
        try {
            paymentService.confirmMockPayment(reference);
            return ResponseEntity.ok(Map.of("message", "Payment confirmed and membership activated."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> myPayments(Authentication authentication) {
        try {
            return ResponseEntity.ok(paymentService.myPayments(authentication.getName()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> allPayments(Authentication authentication) {
        try {
            return ResponseEntity.ok(paymentService.allPayments(authentication.getName()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
