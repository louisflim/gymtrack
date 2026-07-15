package edu.cit.lim.gymtrack.feature.payments;

import edu.cit.lim.gymtrack.entity.Gym;
import edu.cit.lim.gymtrack.entity.Payment;
import edu.cit.lim.gymtrack.entity.PaymentStatus;
import edu.cit.lim.gymtrack.entity.SubscriptionPlan;
import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.feature.membership.MembershipService;
import edu.cit.lim.gymtrack.feature.payments.dto.CheckoutRequest;
import edu.cit.lim.gymtrack.feature.payments.dto.CheckoutResponse;
import edu.cit.lim.gymtrack.feature.payments.dto.PaymentModeResponse;
import edu.cit.lim.gymtrack.feature.payments.dto.PaymentResponse;
import edu.cit.lim.gymtrack.feature.payments.dto.PaymentStatusResponse;
import edu.cit.lim.gymtrack.feature.plans.PlanService;
import edu.cit.lim.gymtrack.repository.PaymentRepository;
import edu.cit.lim.gymtrack.repository.UserRepository;
import edu.cit.lim.gymtrack.util.GymGuard;
import edu.cit.lim.gymtrack.util.RoleGuard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PlanService planService;
    private final PayMongoService payMongoService;
    private final MembershipService membershipService;
    private final PaymentReturnUrlResolver returnUrlResolver;

    public PaymentService(PaymentRepository paymentRepository,
                          UserRepository userRepository,
                          PlanService planService,
                          PayMongoService payMongoService,
                          MembershipService membershipService,
                          PaymentReturnUrlResolver returnUrlResolver) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.planService = planService;
        this.payMongoService = payMongoService;
        this.membershipService = membershipService;
        this.returnUrlResolver = returnUrlResolver;
    }

    @Transactional
    public CheckoutResponse createCheckout(CheckoutRequest request, String memberEmail) {
        if (request == null || request.getPlanId() == null) {
            throw new IllegalArgumentException("Plan ID is required.");
        }
        User member = RoleGuard.requireMember(userRepository, memberEmail);
        SubscriptionPlan plan = planService.getActivePlan(request.getPlanId());

        if (member.getGym() == null) {
            throw new IllegalArgumentException(
                    "Visit a gym and scan the staff QR code to enroll before purchasing a plan."
            );
        }
        if (plan.getGym() == null || !plan.getGym().getId().equals(member.getGym().getId())) {
            throw new IllegalArgumentException("This plan is not available for your gym.");
        }

        String successUrl = returnUrlResolver.resolveSuccessUrl(request.getSuccessUrl());
        String cancelUrl = returnUrlResolver.resolveCancelUrl(request.getCancelUrl());

        Payment payment = new Payment();
        payment.setUser(member);
        payment.setPlan(plan);
        payment.setAmount(plan.getPrice());
        payment.setStatus(PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);

        String reference = "GT-" + payment.getId() + "-" + UUID.randomUUID().toString().substring(0, 8);
        payment.setReferenceNumber(reference);

        PayMongoService.CheckoutSessionResult session =
                payMongoService.createCheckoutSession(plan, reference, successUrl, cancelUrl);
        payment.setPaymongoCheckoutId(session.checkoutId());
        paymentRepository.save(payment);

        return new CheckoutResponse(
                payment.getId(),
                session.checkoutUrl(),
                payment.getStatus().name(),
                reference,
                session.mockCheckout(),
                payMongoService.currentMode().name()
        );
    }

    @Transactional(readOnly = true)
    public PaymentModeResponse paymentMode() {
        return new PaymentModeResponse(
                payMongoService.currentMode(),
                payMongoService.isMockEnabled(),
                payMongoService.modeDescription()
        );
    }

    @Transactional
    public void handleWebhook(String payload, String signatureHeader) {
        if (!payMongoService.verifyWebhookSignature(signatureHeader, payload)) {
            throw new SecurityException("Invalid PayMongo webhook signature.");
        }
        if (!payMongoService.isPaidEvent(payload)) {
            return;
        }

        Payment payment = resolvePaymentFromWebhook(payload);
        if (payment == null || payment.getStatus() == PaymentStatus.PAID) {
            return;
        }

        markPaid(payment, payMongoService.extractPaymentMethod(payload), payMongoService.extractPaymongoPaymentId(payload));
    }

    @Transactional
    public void confirmMockPayment(String reference, String memberEmail) {
        if (!payMongoService.isMockEnabled()) {
            throw new SecurityException("Mock payment confirmation is disabled.");
        }
        User member = RoleGuard.requireMember(userRepository, memberEmail);
        Payment payment = findPaymentByReference(reference);
        if (!payment.getUser().getId().equals(member.getId())) {
            throw new SecurityException("You do not have access to this payment.");
        }
        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }
        markPaid(payment, "mock", null);
    }

    @Transactional(readOnly = true)
    public PaymentStatusResponse paymentStatus(String reference, String memberEmail) {
        User member = RoleGuard.requireMember(userRepository, memberEmail);
        Payment payment = findPaymentByReference(reference);
        if (!payment.getUser().getId().equals(member.getId())) {
            throw new SecurityException("You do not have access to this payment.");
        }
        boolean mockCheckout = payment.getPaymongoCheckoutId() != null
                && payment.getPaymongoCheckoutId().startsWith("mock_");
        return new PaymentStatusResponse(
                payment.getReferenceNumber(),
                payment.getStatus().name(),
                payment.getStatus() == PaymentStatus.PAID,
                mockCheckout
        );
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> myPayments(String memberEmail) {
        User member = RoleGuard.requireMember(userRepository, memberEmail);
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(member.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> allPayments(String requesterEmail) {
        User requester = RoleGuard.requireAdmin(userRepository, requesterEmail);
        Gym gym = GymGuard.requireAdminGym(requester);

        return paymentRepository.findByPlanGymIdOrderByCreatedAtDesc(gym.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    private Payment resolvePaymentFromWebhook(String payload) {
        String checkoutId = payMongoService.extractCheckoutIdFromWebhook(payload);
        if (checkoutId != null) {
            Payment byCheckout = paymentRepository.findByPaymongoCheckoutId(checkoutId).orElse(null);
            if (byCheckout != null) {
                return byCheckout;
            }
        }

        String reference = payMongoService.extractReferenceFromWebhook(payload);
        if (reference != null) {
            return paymentRepository.findByReferenceNumber(reference).orElse(null);
        }

        return resolveMockPayment(checkoutId);
    }

    private void markPaid(Payment payment, String paymentMethod, String paymongoPaymentId) {
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(java.time.LocalDateTime.now());
        payment.setPaymentMethod(paymentMethod);
        if (paymongoPaymentId != null && !paymongoPaymentId.isBlank()) {
            payment.setPaymongoPaymentId(paymongoPaymentId);
        }
        paymentRepository.save(payment);
        membershipService.activateMembership(payment.getUser().getId(), payment.getPlan().getId());
    }

    private Payment findPaymentByReference(String reference) {
        if (reference == null || !reference.startsWith("GT-")) {
            throw new IllegalArgumentException("Invalid payment reference.");
        }
        return paymentRepository.findByReferenceNumber(reference)
                .orElseGet(() -> {
                    try {
                        Long paymentId = Long.parseLong(reference.split("-")[1]);
                        return paymentRepository.findById(paymentId)
                                .orElseThrow(() -> new IllegalArgumentException("Payment not found."));
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Payment not found.");
                    }
                });
    }

    private Payment resolveMockPayment(String checkoutId) {
        if (checkoutId == null || !checkoutId.startsWith("mock_GT-")) {
            return null;
        }
        String reference = checkoutId.replace("mock_", "");
        return paymentRepository.findByReferenceNumber(reference).orElse(null);
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getUser().getFullName(),
                payment.getPlan().getName(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus().name(),
                payment.getPaymentMethod(),
                payment.getCreatedAt(),
                payment.getPaidAt()
        );
    }
}
